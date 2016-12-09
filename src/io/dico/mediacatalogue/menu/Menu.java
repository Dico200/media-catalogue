package io.dico.mediacatalogue.menu;

import java.util.Iterator;
import java.util.NavigableSet;
import java.util.TreeSet;

public class Menu {

    private final String header;
    private final NavigableSet<MenuItem> items = new TreeSet<>();
    
    /**
     * A menu takes care of showing a menu to the console, taking an action from the list and executing it.
     * @param header The string displayed above the menu
     */
    public Menu(String header) {
        this.header = header;
    }

    public MenuItem asItem(String name) {
        return new MenuItem(name) {
            @Override
            public void run() {
                requestAction();
            }
        };
    }

    public Menu addItem(MenuItem item) {
        // set position
        item.setIndex(items.size());
        items.add(item);
        return this;
    }

    public void requestAction() {
        MenuItem.console.writeLine(generateMenu());
        requestItem().run();
    }

    private MenuItem requestItem() {
        return MenuItem.console.requestWithExceptions(() -> this.parseItem(MenuItem.console.requestLine()), null);
    }

    private MenuItem parseItem(String input) {
        try {
            int number = Integer.parseInt(input);
            if (1 <= number && number <= items.size()) {
                return getItem(number - 1);
            }
            throw new IllegalArgumentException("That number is not in the list! Please try again");
        } catch (NumberFormatException e) {
            input = input.toLowerCase().replace(" ", "");
            for (MenuItem item : items) {
                if (input.equals(item.getName().toLowerCase().replace(" ", ""))) {
                    return item;
                }
            }
            throw new IllegalArgumentException("That item is not in the list! Please try again");
        }
    }

    private MenuItem getItem(int index) {
        int half = items.size() / 2;
        if (0 <= index && index <= half) {
            Iterator<MenuItem> iterator = items.iterator();
            int current = 0;
            while (current++ < index) {
                iterator.next();
            }
            return iterator.next();
        } else if (half < index && index < items.size()) {
            Iterator<MenuItem> iterator = items.descendingIterator();
            int current = items.size() - 1;
            while (current-- > index) {
                iterator.next();
            }
            return iterator.next();

        }
        throw new IndexOutOfBoundsException("for index: " + index);
    }

    public String generateMenu() {
        StringBuilder sb = new StringBuilder(header);
        for (MenuItem item : items) {
            sb.append('\n').append(item.getIndex() + 1).append(": ").append(item.getName());
        }
        return sb.toString();
    }

}
