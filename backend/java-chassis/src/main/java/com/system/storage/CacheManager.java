/*
    * Handles saving and retrieving raw API data from the /cache folder
*/
package com.system.storage;

import java.io.*;

public class CacheManager {

    private static final String CACHE_DIR = "../../data/cache/raw/";

    public void save(String titleId, String provider, String json) throws IOException {
        new File(CACHE_DIR).mkdirs();
        try (FileWriter writer = new FileWriter(resolve(titleId, provider))) {
            writer.write(json);
        }
    }

    public String load(String titleId, String provider) throws IOException {
        File file = resolve(titleId, provider);
        if (!file.exists()) return null;
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line).append('\n');
        }
        return sb.toString();
    }

    public boolean exists(String titleId, String provider) {
        return resolve(titleId, provider).exists();
    }

    private File resolve(String titleId, String provider) {
        return new File(CACHE_DIR, titleId + "_" + provider + ".json");
    }
}
