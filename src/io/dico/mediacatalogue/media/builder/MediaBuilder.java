package io.dico.mediacatalogue.media.builder;

import io.dico.mediacatalogue.media.Media;

public interface MediaBuilder<T extends Media> {

    /**
     * @return The type of media constructed by this MediaBuilder
     */
    Class<T> type();

    /**
     * Constructs an instance of a type of media using console input
     * @return a new instance of the type of media that this constructs
     */
    T build();

}
