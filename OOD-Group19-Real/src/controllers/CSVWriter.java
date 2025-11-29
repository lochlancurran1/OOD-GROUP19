package controllers;

import java.io.*;
import java.util.*;

/** A utility class for writing data to CSV (comma-separated value) files.
 * Each row is written as a comma-separated String array.
 */
public class CSVWriter {

    /**
     * Writes data to a CSV file at the given file path.
     *
     * @param filePath the path to the output CSV file
     * @param data a list of String arrays, where each array represents one row
     */
    public static void writeCSV(String filePath, List<String[]> data) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            for (String[] row : data) {
                pw.println(String.join(",", row));
            }
        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + filePath);
            e.printStackTrace();
        }
    }
}