package io.dico.mediacatalogue.menu.searchmenu;

import io.dico.mediacatalogue.MediaContainer;
import io.dico.mediacatalogue.media.Media;
import io.dico.mediacatalogue.menu.MediaTypeMenu;
import io.dico.mediacatalogue.menu.Menu;
import io.dico.mediacatalogue.menu.MenuItem;
import io.dico.mediacatalogue.util.Printer;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class SearchMenuItem extends MenuItem {
    
    private static final String searchHelp;
    
    public static SearchMenuItem withConsumer(String actionDescription, MediaContainer mediaContainer, Consumer<List<Media>> consumer) {
        return new SearchMenuItem(actionDescription, mediaContainer) {
            @Override
            protected void process(List<Media> matches) {
                consumer.accept(matches);
            }
        };
    }
    
    private final MediaContainer mediaContainer;
    private final Menu listMenu;
    
    public SearchMenuItem(String actionDescription, MediaContainer mediaContainer) {
        super(actionDescription);
        listMenu = new MediaTypeMenu("Choose how you want to search for the item you want to " + actionDescription + ", I'll make a list of matches for you",
                "all", this::byClass).addItem(MenuItem.withRunnable("specific criteria", this::search));
        this.mediaContainer = mediaContainer;
    }
    
    public SearchMenuItem addToListMenu(MenuItem item) {
        listMenu.addItem(item);
        return this;
    }
    
    private void preProcess(List<Media> matches) {
        if (matches.isEmpty()) {
            console.writeLine("There were no matches");
            return;
        }
        process(matches);
    }
    
    protected abstract void process(List<Media> matches);
    
    @Override
    public void run() {
        listMenu.requestAction();
    }
    
    private void search() {
        Criteria criteria = requestCriteria("Enter criteria to search by");
        List<Media> matches = mediaContainer.getItemsByCriteria(criteria);
        preProcess(matches);
    }
    
    private void byClass(Class<? extends Media> clazz) {
        List<Media> items = mediaContainer.getItemsByType(clazz);
        preProcess(items);
    }
    
    public static Criteria requestCriteria(String header) {
        while (true) {
            console.writeLine(header + " (enter 'help' if you're clueless)");
            String input = console.requestLine();
            if ("help".equals(input)) {
                displaySearchHelp();
                continue;
            }
            
            try {
                return Criteria.parse(input);
            } catch (IllegalArgumentException e) {
                console.writeLine("There's a problem with your syntax: " + e.getMessage());
                console.writeLine("Remember that you can write 'help' for an explanation about criteria");
            }
        }
    }
    
    static {
        StringBuilder help = new StringBuilder();
        help.append("You must enter specific criteria to search by");
        help.append('\n').append("These criteria must follow a syntax");
        help.append('\n').append("Each criterium has this format: <left input> <operator> <right input>");
        help.append('\n').append("The left and right inputs can be anything. If you enter the name of one of the table columns displayed when you list items," +
                "it will be replaced with each distinct item's value for it");
        
        Criteria.getSearchOperators().keySet().stream().mapToInt(String::length).max().ifPresent(maxLength -> {
            help.append('\n').append("The operator can be any of the following:");
            for (Map.Entry<String, SearchOperator> entry : Criteria.getSearchOperators().entrySet()) {
                String key = entry.getKey();
                help.append("\n").append("  ").append(key);
                if (maxLength > key.length()) {
                    help.append(Printer.whitespace(maxLength - key.length()));
                }
                help.append(entry.getValue());
            }
        });
        
        help.append('\n').append("distinct conditions must be separated by a boolean operator, such as |, &, and can be negated by prefixing the search operator with !");
        searchHelp = help.toString();
    }
    
    private static void displaySearchHelp() {
        console.writeLine(searchHelp);
    }
    
}
