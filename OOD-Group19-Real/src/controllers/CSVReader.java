package controllers;

import java.io.*;
import java.util.*;

/**
 * Class for reading CSV files used by the system.
 * Each line of the file is split by commas and stored as a String array.
 */

public class CSVReader {

    /**
     * Reads a CSV file from the given file path and returns its contents.
     * 
     * @param filePath the path to the CSV file
     * @return a list of String arrays, where each array represents one row of the CSV
     */
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
