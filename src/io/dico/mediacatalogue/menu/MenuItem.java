package io.dico.mediacatalogue.menu;

import io.dico.mediacatalogue.ConsoleOperator;

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

    protected int index;
    protected final String name;

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
