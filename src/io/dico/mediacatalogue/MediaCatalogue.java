package io.dico.mediacatalogue;

import com.google.common.collect.ImmutableList;
import io.dico.mediacatalogue.media.AudioTrack;
import io.dico.mediacatalogue.media.Film;
import io.dico.mediacatalogue.media.Media;
import io.dico.mediacatalogue.media.TelevisionProgramme;
import io.dico.mediacatalogue.media.builder.AudioTrackBuilder;
import io.dico.mediacatalogue.media.builder.FilmBuilder;
import io.dico.mediacatalogue.media.builder.MediaBuilder;
import io.dico.mediacatalogue.media.builder.TelevisionProgrammeBuilder;
import io.dico.mediacatalogue.menu.MediaTypeMenu;
import io.dico.mediacatalogue.menu.Menu;
import io.dico.mediacatalogue.menu.MenuItem;
import io.dico.mediacatalogue.menu.searchmenu.SearchMenuItem;
import io.dico.mediacatalogue.util.ConsoleOperator;
import io.dico.mediacatalogue.util.Duration;
import io.dico.mediacatalogue.util.Printer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MediaCatalogue {
    
    public static void main(String[] args) {
        MediaCatalogue instance = new MediaCatalogue();
        instance.beginConversation();
    }
    
    private final ConsoleOperator console;
    private final Menu menu;
    private final FilmBuilder filmBuilder;
    private final AudioTrackBuilder audioTrackBuilder;
    private final TelevisionProgrammeBuilder televisionProgrammeBuilder;
    private final MediaContainer mediaContainer;
    private List<Media> lastSearch;
    private String fileLoadedFrom;
    private boolean saveScheduled;
    
    private MediaCatalogue() {
        console = new ConsoleOperator(new Supplier<String>() {
            private final Scanner scanner = new Scanner(System.in);
            
            @Override
            public String get() {
                String result = scanner.nextLine();
                if ("exit".equals(result)) {
                    exit();
                }
                if ("menu".equals(result)) {
                    throw new MenuRequestException();
                }
                return result;
            }
        }, System.out::println);
        MenuItem.setConsole(console);
        
        mediaContainer = new MediaContainer();
        menu = createMenu();
        filmBuilder = new FilmBuilder(console);
        audioTrackBuilder = new AudioTrackBuilder(console);
        televisionProgrammeBuilder = new TelevisionProgrammeBuilder(console);
    }
    
    private Menu createMenu() {
        return new Menu("Welcome to your media catalogue. Please select an item by number or name to continue:")
                .addItem(MenuItem.withRunnable("exit", this::exit))
                .addItem(MenuItem.withRunnable("load", this::load))
                .addItem(MenuItem.withRunnable("save", this::save))
                .addItem(new MediaTypeMenu("Enter type of media to add", null, this::newItem)
                        .asItem("new"))
                .addItem(SearchMenuItem.withConsumer("search", mediaContainer, this::processSearch))
                .addItem(SearchMenuItem.withConsumer("remove", mediaContainer, this::processRemove)
                        .addToListMenu(MenuItem.withRunnable("use last search", usingLastSearchForAction(this::processRemove))))
                .addItem(SearchMenuItem.withConsumer("edit", mediaContainer, this::processEdit)
                        .addToListMenu(MenuItem.withRunnable("use last search", usingLastSearchForAction(this::processEdit))));
    }
    
    private void beginConversation() {
        try {
            load();
            requestActionsContinuously();
        } catch (ExitingException e) {
            console.writeLine("See you next time!");
        }
    }
    
    private void requestActionsContinuously() {
        while (true) {
            try {
                menu.requestAction();
            } catch (MenuRequestException ignored) {
            } catch (ExitingException e) {
                break;
            }
        }
    
        if (saveScheduled) {
            save();
        }
    }
    
    private MediaBuilder builderFor(Class<? extends Media> mediaClass) {
        if (mediaClass == Film.class) {
            return filmBuilder;
        } else if (mediaClass == AudioTrack.class) {
            return audioTrackBuilder;
        } else if (mediaClass == TelevisionProgramme.class) {
            return televisionProgrammeBuilder;
        } else {
            return null;
        }
    }
    
    private void addMedia(Media media) {
        if (!mediaContainer.add(media)) {
            console.writeLine("This media already exists. Discarded it.");
        } else {
            console.writeLine("Added: ");
            console.writeLine(Printer.createItemTable(ImmutableList.of(media), false));
            saveScheduled = true;
        }
    }
    
    private void removeMedia(Media media) {
        if (mediaContainer.remove(media))
            saveScheduled = true;
            if (lastSearch != null) {
                lastSearch.remove(media);
            }
    }
    
    private void newItem(Class<? extends Media> type) {
        MediaBuilder builder = builderFor(type);
        if (builder == null) {
            // shouldn't happen
            console.writeLine("That type does not exist");
        } else {
            addMedia(builder.build());
        }
    }
    
    private Runnable usingLastSearchForAction(Consumer<List<Media>> action) {
        return () -> {
            if (lastSearch == null) {
                console.writeLine("You haven't searched anything yet");
            } else {
                action.accept(lastSearch);
            }
        };
    }
    
    private void processEdit(List<Media> matches) {
        lastSearch = matches;
        console.writeLine("The following matches were found:");
        String table = Printer.createItemTable(matches, true);
        console.writeLine(table);
        console.writeLine("If the item you wish to edit is in the list, please enter its number, else enter 'menu'");
        Media toEdit = selectItem(matches, false);
        Map<String, String> defaults = new HashMap<>(toEdit.getFields().size());
        for (Map.Entry<String, Object> entry : toEdit.getFields().entrySet()) {
            Object value = entry.getValue();
            String valueAsString;
            if (value instanceof Duration) {
                valueAsString = Integer.toString(((Number) value).intValue());
            } else {
                valueAsString = value.toString();
            }
            defaults.put(entry.getKey(), valueAsString);
        }
        
        // construct a media builder and set its defaults to the current values
        MediaBuilder builder;
        if (toEdit instanceof Film) {
            builder = new FilmBuilder(console, defaults);
        } else if (toEdit instanceof AudioTrack) {
            builder = new AudioTrackBuilder(console, defaults);
        } else if (toEdit instanceof TelevisionProgramme) {
            builder = new TelevisionProgrammeBuilder(console, defaults);
        } else {
            // this should never happen
            console.writeLine("An error occurred. This item cannot be edited");
            return;
        }
        
        Media newItem = builder.build();
        if (newItem.equals(toEdit)) {
            console.writeLine("You haven't changed anything! hihi");
            return;
        }
    
        if (!mediaContainer.add(newItem)) {
            console.writeLine("The new item already exists");
            return;
        }
        
        saveScheduled = true;
        console.writeLine("Would you like to keep the old item? (y/n)");
        boolean keep = console.requestYesOrNo();
        if (!keep) {
            removeMedia(toEdit);
        }
    }
    
    private void processRemove(List<Media> matches) {
        lastSearch = matches;
        console.writeLine("The following matches were found:");
        String table = Printer.createItemTable(matches, true);
        console.writeLine(table);
        console.writeLine("If the item you wish to remove is in the list, please enter its number, or 'all' to remove all of them. Else enter 'menu'.");
        Media toRemove = selectItem(matches, true);
        if (toRemove == null) {
            // remove all matches
            for (Media match : matches) {
                removeMedia(match);
            }
            console.writeLine("Removed items successfully");
        } else {
            removeMedia(toRemove);
            console.writeLine("Removed item successfully");
        }
    }
    
    private void processSearch(List<Media> matches) {
        lastSearch = matches;
        console.writeLine("The following matches were found:");
        String table = Printer.createItemTable(matches, false);
        console.writeLine(table);
    }
    
    private Media selectItem(List<Media> items, boolean allowAll) {
        // request a number from the user that was in front of the list of media items displayed
        // then return the selected media item
        // return null if all of the items should be removed and allowAll is true.
        int item;
        while (true) {
            String input = console.requestLine();
            if (allowAll && "all".equals(input)) {
                return null;
            }
            
            try {
                item = Integer.parseInt(input);
                if (1 <= item && item <= items.size()) {
                    return items.get(item - 1);
                }
                console.writeLine("That number is not in the list!");
            } catch (NumberFormatException e) {
                console.writeLine("That's not a number, please try again");
            }
        }
    }
    
    private void exit() {
        throw new ExitingException();
    }
    
    private void load() {
        while (true) {
            console.writeLine("Would you like to load from file? (y/n)");
            if (!console.requestYesOrNo()) {
                return;
            }
            
            console.writeLine("Enter path to file (from home folder)");
            String input = console.requestLine();
            String fileName = System.getProperty("user.home") + File.separator + input;
            console.writeLine("Loading from " + fileName);
            
            try {
                mediaContainer.load(fileName);
                if (fileLoadedFrom == null) {
                    fileLoadedFrom = input;
                }
                return;
            } catch (IOException e) {
                console.writeLine("Error occurred while loading: " + e.getMessage());
            }
            
        }
    }
    
    private void save() {
        boolean createFile = false;
        while (true) {
            console.writeLine("Would you like to save? (y/n)");
            if (!console.requestYesOrNo()) {
                return;
            }
            
            String line = "Enter path to file (from home folder)";
            if (fileLoadedFrom != null){
                line += " (Enter: " + fileLoadedFrom + ")";
            }
            console.writeLine(line);
            String file = console.requestLine();
            if (file.isEmpty() && fileLoadedFrom != null) {
                file = fileLoadedFrom;
            }
            file = System.getProperty("user.home") + File.separator + file;
            
            try {
                mediaContainer.save(file, createFile);
                return;
            } catch (IOException e) {
                if (e instanceof FileNotFoundException && !createFile) {
                    console.writeLine("File not found, would you like to create it? (y/n)");
                    createFile = console.requestYesOrNo();
                    if (!createFile) {
                        return;
                    }
                } else {
                    console.writeLine("Error occurred while saving: " + e.getMessage());
                }
            }
        }
    }
    
}
