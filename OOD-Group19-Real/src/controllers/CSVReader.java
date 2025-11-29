package controllers;

import java.io.*;
import java.util.*;

public class CSVReader {

    public static List<String[]> readCSV(String filePath) {
        List<String[]> rows = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) continue;

                String[] parts = Arrays.stream(line.split(","))
                        .map(String::trim)
                        .toArray(String[]::new);

                rows.add(parts);

            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + filePath);
            e.printStackTrace();
        }
        return rows;
    }
}
