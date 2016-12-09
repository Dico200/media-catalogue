package io.dico.mediacatalogue.menu;

import io.dico.mediacatalogue.media.AudioTrack;
import io.dico.mediacatalogue.media.Film;
import io.dico.mediacatalogue.media.Media;
import io.dico.mediacatalogue.media.TelevisionProgramme;

import java.util.function.Consumer;

public class MediaTypeMenu extends Menu {
    
    /**
     * @param header The header of this menu
     * @param nameForAllTypes null if the option to choose all types should not be available, else the name in the menu
     * @param consumer a consumer that takes the requested media class, or null if 'all' was selected
     */
    public MediaTypeMenu(String header, String nameForAllTypes, Consumer<Class<? extends Media>> consumer) {
        super(header);
        addItem(MenuItem.withRunnable("film", () -> consumer.accept(Film.class)));
        addItem(MenuItem.withRunnable("audio track", () -> consumer.accept(AudioTrack.class)));
        addItem(MenuItem.withRunnable("tv programme", () -> consumer.accept(TelevisionProgramme.class)));
        if (nameForAllTypes != null && !nameForAllTypes.isEmpty()) {
            addItem(MenuItem.withRunnable(nameForAllTypes, () -> consumer.accept(null)));
        }
    }
}
