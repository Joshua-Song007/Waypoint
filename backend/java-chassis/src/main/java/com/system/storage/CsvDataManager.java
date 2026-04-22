/*
    * The "Gatekeeper." The only file that reads/writes the CSVs in the /data folder
    * basically keeps the important data safe from corruption and messy logic
    * later when move to a real database chng to SqlDatabaseManager.java logic
*/
package com.system.storage;

import com.system.models.MediaTitle;
import com.system.models.MediaTitle.MediaType;
import com.system.models.MediaVector;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CsvDataManager {

    private static final String LIBRARY_PATH = "../../data/cache/global-library.csv";
    private static final String HISTORY_PATH  = "../../data/cache/user_history.csv";

    private static final String LIBRARY_HEADER = "id,title,type,source,d1,d2,d3,d4,d5,d6,d7,d8,d9,d10,tagged_at";
    private static final String HISTORY_HEADER  = "id,title,type,rating,consumed_at,d1,d2,d3,d4,d5,d6,d7,d8,d9,d10";

    // --- Public API ---

    public List<MediaTitle> loadLibrary() throws IOException {
        return loadCsv(LIBRARY_PATH, false);
    }

    public List<MediaTitle> loadUserHistory() throws IOException {
        return loadCsv(HISTORY_PATH, true);
    }

    public void appendToLibrary(MediaTitle title) throws IOException {
        appendRow(LIBRARY_PATH, LIBRARY_HEADER, String.join(",", toRow(title)));
    }

    public void appendToHistory(MediaTitle title, float rating) throws IOException {
        appendRow(HISTORY_PATH, HISTORY_HEADER, String.join(",", toHistoryRow(title, rating)));
    }

    // --- CSV parsing ---

    // global-library.csv: id,title,type,source,d1..d10,tagged_at  → vecOffset=4
    // user_history.csv:   id,title,type,rating,consumed_at,d1..d10 → vecOffset=5
    private MediaTitle toTitle(String[] row, boolean isHistory) {
        int o = isHistory ? 5 : 4;
        return new MediaTitle(
            row[0].trim(),
            row[1].trim(),
            MediaType.valueOf(row[2].trim()),
            new MediaVector(
                Float.parseFloat(row[o].trim()),   Float.parseFloat(row[o+1].trim()),
                Float.parseFloat(row[o+2].trim()), Float.parseFloat(row[o+3].trim()),
                Float.parseFloat(row[o+4].trim()), Float.parseFloat(row[o+5].trim()),
                Float.parseFloat(row[o+6].trim()), Float.parseFloat(row[o+7].trim()),
                Float.parseFloat(row[o+8].trim()), Float.parseFloat(row[o+9].trim())
            )
        );
    }

    private String[] toRow(MediaTitle t) {
        float[] v = t.vector.toArray();
        return new String[]{
            t.id, t.title, t.mediaType.name(), "",
            fmt(v[0]), fmt(v[1]), fmt(v[2]), fmt(v[3]), fmt(v[4]),
            fmt(v[5]), fmt(v[6]), fmt(v[7]), fmt(v[8]), fmt(v[9]),
            LocalDateTime.now().toString()
        };
    }

    private String[] toHistoryRow(MediaTitle t, float rating) {
        float[] v = t.vector.toArray();
        return new String[]{
            t.id, t.title, t.mediaType.name(), fmt(rating), LocalDateTime.now().toString(),
            fmt(v[0]), fmt(v[1]), fmt(v[2]), fmt(v[3]), fmt(v[4]),
            fmt(v[5]), fmt(v[6]), fmt(v[7]), fmt(v[8]), fmt(v[9])
        };
    }

    // --- I/O ---

    private List<MediaTitle> loadCsv(String path, boolean isHistory) throws IOException {
        List<MediaTitle> results = new ArrayList<>();
        File file = new File(path);
        if (!file.exists()) return results;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                results.add(toTitle(line.split(","), isHistory));
            }
        }
        return results;
    }

    private void appendRow(String path, String header, String row) throws IOException {
        File file = new File(path);
        file.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(file, true)) {
            FileChannel channel = fos.getChannel();
            FileLock lock = channel.lock();
            try {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
                if (channel.size() == 0) {
                    writer.write(header);
                    writer.newLine();
                }
                writer.write(row);
                writer.newLine();
                writer.flush();
            } finally {
                lock.release();
            }
        }
    }

    private String fmt(float f) {
        return String.format("%.2f", f);
    }
}
