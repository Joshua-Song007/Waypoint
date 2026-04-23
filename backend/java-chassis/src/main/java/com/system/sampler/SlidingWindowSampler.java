/*
    * implements the sliding window logic to build current taste profile
    * basically a helper for weight multipler to be calculated
*/
package com.system.sampler;

import com.system.models.HistoryEntry;
import com.system.models.MediaTitle;

import java.util.*;

public class SlidingWindowSampler {

    private static final int TOTAL = 50;
    private static final int RECENT_SIZE = 25;
    private static final int ANCHOR_SIZE = 25;
    private static final double ANCHOR_POOL_PERCENTILE = 0.10;

    public List<MediaTitle> sample(List<HistoryEntry> history, Map<String, MediaTitle> library) {
        if (history.size() <= TOTAL) {
            return resolve(history, library);
        }

        // Recent buffer — latest 25 by timestamp (ISO-8601 sorts lexicographically)
        List<HistoryEntry> byTime = new ArrayList<>(history);
        byTime.sort((a, b) -> b.timestamp.compareTo(a.timestamp));
        List<HistoryEntry> recent = byTime.subList(0, RECENT_SIZE);

        // Anchor pool — top 10% by rating
        List<HistoryEntry> byRating = new ArrayList<>(history);
        byRating.sort((a, b) -> Float.compare(b.rating, a.rating));
        int poolSize = Math.max(1, (int) (history.size() * ANCHOR_POOL_PERCENTILE));
        List<HistoryEntry> pool = new ArrayList<>(byRating.subList(0, poolSize));

        // Stochastic sample — shuffle pool, take up to ANCHOR_SIZE
        Collections.shuffle(pool);
        List<HistoryEntry> anchor = pool.subList(0, Math.min(ANCHOR_SIZE, pool.size()));

        List<MediaTitle> result = new ArrayList<>(resolve(recent, library));
        result.addAll(resolve(anchor, library));
        return result;
    }

    private List<MediaTitle> resolve(List<HistoryEntry> entries, Map<String, MediaTitle> library) {
        List<MediaTitle> result = new ArrayList<>();
        for (HistoryEntry e : entries) {
            MediaTitle t = library.get(e.titleId);
            if (t != null) result.add(t);
        }
        return result;
    }
}
