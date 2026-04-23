/*
    * obj for user_history.csv
    * stores basic info about entry:
    * titleId/pointer, timestamp, rating, tags (List <String>) optional
*/
package com.system.models;

import java.util.Collections;
import java.util.List;

public final class HistoryEntry {

    public final String titleId;
    public final String timestamp;
    public final float rating;
    public final List<String> tags; // optional might be user manual for now

    public HistoryEntry(String titleId, String timestamp, float rating, List<String> tags) {
        this.titleId = titleId;
        this.timestamp = timestamp;
        this.rating = rating;
        this.tags = tags != null ? Collections.unmodifiableList(tags) : Collections.emptyList();
    }

    public HistoryEntry(String titleId, String timestamp, float rating) {
        this(titleId, timestamp, rating, null);
    }
}
