/*
    * The "Gatekeeper." The only file that reads/writes the CSVs in the /data folder
    * basically keeps the important data safe from corruption and messy logic
    * later when move to a real database chng to SqlDatabaseManager.java logic
*/
package com.system.storage;

import com.system.models.HistoryEntry;
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
    private static final String HISTORY_HEADER  = "UserID,TitleID,UserRating,Timestamp";

    private static final String USER_ID = "user1"; // single-user prototype

    // --- Library channel ---

    public List<MediaTitle> loadLibrary() throws IOException {
        List<MediaTitle> results = new ArrayList<>();
        File file = new File(LIBRARY_PATH);
        if (!file.exists()) return results;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                results.add(toTitle(line.split(",")));
            }
        }
        return results;
    }

    public void appendToLibrary(MediaTitle title) throws IOException {
        appendRow(LIBRARY_PATH, LIBRARY_HEADER, String.join(",", toRow(title)));
    }

    // global-library.csv: id,title,type,source,d1..d10,tagged_at → vecOffset=4
    private MediaTitle toTitle(String[] row) {
        return new MediaTitle(
            row[0].trim(),
            row[1].trim(),
            MediaType.valueOf(row[2].trim()),
            new MediaVector(
                Float.parseFloat(row[4].trim()),  Float.parseFloat(row[5].trim()),
                Float.parseFloat(row[6].trim()),  Float.parseFloat(row[7].trim()),
                Float.parseFloat(row[8].trim()),  Float.parseFloat(row[9].trim()),
                Float.parseFloat(row[10].trim()), Float.parseFloat(row[11].trim()),
                Float.parseFloat(row[12].trim()), Float.parseFloat(row[13].trim())
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

    // --- History channel ---

    public List<HistoryEntry> loadUserHistory() throws IOException {
        List<HistoryEntry> results = new ArrayList<>();
        File file = new File(HISTORY_PATH);
        if (!file.exists()) return results;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                results.add(toHistoryEntry(line.split(",")));
            }
        }
        return results;
    }

    public void appendToHistory(String titleId, float rating) throws IOException {
        appendRow(HISTORY_PATH, HISTORY_HEADER, String.join(",", toHistoryRow(titleId, rating)));
    }

    // user_history.csv: UserID,TitleID,UserRating,Timestamp
    private HistoryEntry toHistoryEntry(String[] row) {
        return new HistoryEntry(
            row[1].trim(),
            row[3].trim(),
            Float.parseFloat(row[2].trim())
        );
    }

    private String[] toHistoryRow(String titleId, float rating) {
        return new String[]{ USER_ID, titleId, fmt(rating), LocalDateTime.now().toString() };
    }

    // --- Shared I/O ---

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
