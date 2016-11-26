package io.dico.mediacatalogue;

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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
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

    private MediaCatalogue() {
        console = new ConsoleOperator(new Supplier<String>() {
            private final Scanner scanner = new Scanner(System.in);

            @Override
            public String get() {
                String result = scanner.nextLine();
                if ("exit".equals(result)) {
                    exit();
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
                .addItem(new MediaTypeMenu("Enter type of media to list", "all", this::listItems)
                        .asItem("list"))
                .addItem(new MediaTypeMenu("Enter type of media to remove", null, this::removeItem)
                        .addItem(new SearchMenuItem("I want to remove by condition", mediaContainer, mediaContainer::removeAll))
                        .asItem("remove"))
                .addItem(new SearchMenuItem("search", mediaContainer, list -> listItems(list, true)));
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
            menu.requestAction();
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
            console.writeLine("Added: " + media.toString());
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

    private void listItems(List<? extends Media> items, boolean displayFields) {
        int index = 0;
        for (Media item : items) {
            index++;
            console.writeLine(index + ": " + item.toString());
        }
    }

    private void listItems(Class<? extends Media> type) {
        listItems(mediaContainer.getItemsByType(type), type == null);
    }

    private void removeItem(List<? extends Media> items, boolean displayFields) {
        listItems(items, displayFields);
        console.writeLine("Enter the number of the item in the list to remove");

        int itemId = console.requestWithValidator(console::requestInt, id -> 1 <= id && id <= items.size(), "That number is not in the list! Please try another") - 1;
        mediaContainer.remove(items.get(itemId));
    }

    private void removeItem(Class<? extends Media> type) {
        removeItem(mediaContainer.getItemsByType(type), type == null);
    }

    private void exit() {
        save();
        throw new ExitingException();
    }

    private void load() {
        while (true) {
            console.writeLine("Would you like to load from file? (y/n)");
            if (!console.requestYesOrNo()) {
                return;
            }

            console.writeLine("Enter path to file");
            String file = console.requestLine();

            try {
                mediaContainer.load(file);
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

            console.writeLine("Enter path to file");
            String file = console.requestLine();

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
