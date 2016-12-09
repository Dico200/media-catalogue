package io.dico.mediacatalogue.menu;

import io.dico.mediacatalogue.media.AudioTrack;
import io.dico.mediacatalogue.media.Film;
import io.dico.mediacatalogue.media.Media;
import io.dico.mediacatalogue.media.TelevisionProgramme;

import java.util.function.Consumer;

public class MediaTypeMenu extends Menu {

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
