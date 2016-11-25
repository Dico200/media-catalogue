package io.dico.mediacatalogue.mediatypes;

import io.dico.mediacatalogue.JsonSerializable;

/**
 * An interface for any type of work to implement
 */
public interface Media extends JsonSerializable {

    /**
     * @return A string identifying the type of this work
     */
    String type();

    /**
     * @return The title of this work
     */
    String title();

    /**
     * @return The duration of this work, in minutes
     */
    int duration();

    /**
     * @return The star rating that the user gave this work, between 1 and 10
     */
    int rating();

    /**
     * @return The year in which this work was released
     */
    int releaseYear();
/*
    /**
     * Set the field with the given name to the given value
     * @param field the name of the field
     * @param value the new value
     * @throws IllegalArgumentException if field does not exist or the value is of the wrong type
     * /
    void set(String field, Object value);
*/
    /**
     * @return A human-readable string representation of this work
     */
    String toString();

    /**
     * @return A hashCode for this work, complying with the hashCode contract
     * Used to check if the user doesn't enter a work that is already in the catalogue
     */
    int hashCode();

    boolean equals(Object other);
}
