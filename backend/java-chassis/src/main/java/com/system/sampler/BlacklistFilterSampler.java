/*
    * this handles the dislike and blacklist logic
    * The Java BlacklistFilter pulls the last 20
    * titles that user rated below 2 stars
    * sends recent hates to constraint_discoverer.py
    * maybe later implement variable shows to pull based on user activity
    * , but for now just pull the last 20 hated titles
*/
package com.system.sampler;

import com.system.models.HistoryEntry;
import com.system.models.MediaTitle;

import java.util.*;

public class BlacklistFilterSampler {

    private static final float RATING_THRESHOLD = 2.0f;
    private static final int BLACKLIST_SIZE = 20;

    public List<MediaTitle> sample(List<HistoryEntry> history, Map<String, MediaTitle> library) {
        List<HistoryEntry> hated = new ArrayList<>();
        for (HistoryEntry e : history) {
            if (e.rating < RATING_THRESHOLD) hated.add(e);
        }

        hated.sort((a, b) -> b.timestamp.compareTo(a.timestamp));

        List<MediaTitle> result = new ArrayList<>();
        for (int i = 0; i < Math.min(BLACKLIST_SIZE, hated.size()); i++) {
            MediaTitle t = library.get(hated.get(i).titleId);
            if (t != null) result.add(t);
        }
        return result;
    }
}
