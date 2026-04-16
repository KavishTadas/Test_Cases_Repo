package com.kavish.core.dataproviders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kavish.core.exceptions.FrameworkException;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Reads test data from a JSON file on the classpath.
 *
 * Convention:
 *  - File lives in src/test/resources/testdata/<fileName>.json
 *  - File must be a JSON array of objects, e.g.:
 *    [
 *      { "username": "user1", "password": "pass1" },
 *      { "username": "user2", "password": "pass2" }
 *    ]
 *
 * Usage in a TestNG @DataProvider:
 * <pre>
 *   @DataProvider(name = "loginData")
 *   public Object[][] loginData() {
 *       return DataProviderUtils.toObjectArray(
 *           JsonDataProvider.getData("testdata/login.json")
 *       );
 *   }
 * </pre>
 */
public final class JsonDataProvider {

    private JsonDataProvider() {}

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Reads all records from the given JSON file.
     *
     * @param fileName classpath-relative path, e.g. "testdata/login.json"
     * @return list of row maps — each map is { key -> value }
     * @throws FrameworkException if the file is not found, is not valid JSON,
     *                            is not a JSON array, or contains no records
     */
    public static List<Map<String, String>> getData(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new FrameworkException("JsonDataProvider: fileName must not be null/blank.");
        }

        try (InputStream input = JsonDataProvider.class
                .getClassLoader().getResourceAsStream(fileName)) {

            if (input == null) {
                throw new FrameworkException(
                        "JSON file not found on classpath: " + fileName);
            }

            List<Map<String, String>> data = MAPPER.readValue(
                    input, new TypeReference<List<Map<String, String>>>() {});

            if (data == null || data.isEmpty()) {
                throw new FrameworkException(
                        "JSON file contains no records: " + fileName);
            }

            return data;

        } catch (FrameworkException fe) {
            throw fe;
        } catch (Exception e) {
            throw new FrameworkException(
                    "Failed to parse JSON file: " + fileName
                            + " | Ensure it is a JSON array of objects."
                            + " | " + e.getMessage());
        }
    }
}