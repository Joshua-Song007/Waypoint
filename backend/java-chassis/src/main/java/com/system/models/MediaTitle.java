/*
    * class that bundles a Title, its ID, and its MediaVector
    * basically full obj of a given piece of media
*/
package com.system.models;

import java.util.Objects;

public final class MediaTitle {

    public enum MediaType {
        MOVIE, TV, ANIME, MANGA, BOOK, WEBCOMIC
    }

    public final String id;
    public final String title;
    public final MediaType mediaType;
    public final MediaVector vector;

    public MediaTitle(String id, String title, MediaType mediaType, MediaVector vector) {
        this.id = id;
        this.title = title;
        this.mediaType = mediaType;
        this.vector = vector;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MediaTitle)) return false;
        MediaTitle m = (MediaTitle) o;
        return Objects.equals(id, m.id)
            && Objects.equals(title, m.title)
            && mediaType == m.mediaType
            && Objects.equals(vector, m.vector);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, mediaType, vector);
    }

    @Override
    public String toString() {
        return String.format("MediaTitle{id='%s', title='%s', mediaType=%s, vector=%s}",
            id, title, mediaType, vector);
    }
}
