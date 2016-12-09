package io.dico.mediacatalogue.menu;

import io.dico.mediacatalogue.util.ConsoleOperator;

public abstract class MenuItem implements Comparable<MenuItem>, Runnable {

    protected static ConsoleOperator console;

    public static void setConsole(ConsoleOperator console) {
        MenuItem.console = console;
    }
    
    public static MenuItem withRunnable(String name, Runnable runnable) {
        return new MenuItem(name) {
            @Override
            public void run() {
                runnable.run();
            }
        };
    }

    // this index represents the position within the menu. It is set when added to a menu.
    protected int index;
    protected final String name;
    
    /**
     * A menu item is one item in a menu. It has a name and an action to be executed.
     * @param name the name of this item
     */
    public MenuItem(String name) {
        this.name = name;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(MenuItem o) {
        return index - o.index;
    }
}
