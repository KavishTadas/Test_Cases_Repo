package com.kavish.core.dataproviders;

import com.kavish.core.exceptions.FrameworkException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads test data from a CSV file on the classpath.
 *
 * Convention:
 *  - File lives in src/test/resources/testdata/<fileName>.csv
 *  - First row = comma-separated column headers
 *  - Each subsequent row = one test data record
 *  - Values containing commas must be wrapped in double quotes
 *
 * Usage in a TestNG @DataProvider:
 * <pre>
 *   @DataProvider(name = "loginData")
 *   public Object[][] loginData() {
 *       return DataProviderUtils.toObjectArray(
 *           CsvDataProvider.getData("testdata/login.csv")
 *       );
 *   }
 * </pre>
 */
public final class CsvDataProvider {

    private CsvDataProvider() {}

    /**
     * Reads all data rows from the given CSV file.
     *
     * @param fileName classpath-relative path, e.g. "testdata/login.csv"
     * @return list of row maps — each map is { columnHeader -> cellValue }
     * @throws FrameworkException if the file is not found, has no header,
     *                            has mismatched columns, or has no data rows
     */
    public static List<Map<String, String>> getData(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new FrameworkException("CsvDataProvider: fileName must not be null/blank.");
        }

        try (InputStream input = CsvDataProvider.class
                .getClassLoader().getResourceAsStream(fileName)) {

            if (input == null) {
                throw new FrameworkException(
                        "CSV file not found on classpath: " + fileName);
            }

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(input, StandardCharsets.UTF_8));

            // First line = headers
            String headerLine = reader.readLine();
            if (headerLine == null || headerLine.isBlank()) {
                throw new FrameworkException(
                        "CSV file has no header row: " + fileName);
            }

            String[] headers = parseCsvLine(headerLine);

            List<Map<String, String>> data = new ArrayList<>();
            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) continue;

                String[] values = parseCsvLine(line);

                if (values.length != headers.length) {
                    throw new FrameworkException(
                            "CSV column mismatch at line " + lineNumber
                                    + " in " + fileName
                                    + " | expected " + headers.length
                                    + " columns, found " + values.length);
                }

                Map<String, String> rowMap = new LinkedHashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    rowMap.put(headers[i].trim(), values[i].trim());
                }
                data.add(rowMap);
            }

            if (data.isEmpty()) {
                throw new FrameworkException(
                        "CSV file has a header row but no data rows: " + fileName);
            }

            return data;

        } catch (FrameworkException fe) {
            throw fe;
        } catch (Exception e) {
            throw new FrameworkException(
                    "Failed to read CSV file: " + fileName
                            + " | " + e.getMessage());
        }
    }

    // ── private helper ─────────────────────────────────────────────────────────

    /**
     * Splits a CSV line respecting double-quoted values that contain commas.
     * e.g.  hello,"world, again",test  →  ["hello", "world, again", "test"]
     */
    private static String[] parseCsvLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        tokens.add(current.toString());
        return tokens.toArray(new String[0]);
    }
}