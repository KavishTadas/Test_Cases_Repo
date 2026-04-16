package com.kavish.core.dataproviders;

import com.kavish.core.exceptions.FrameworkException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads test data from an Excel (.xlsx) file on the classpath.
 *
 * Convention:
 *  - File lives in src/test/resources/testdata/<fileName>.xlsx
 *  - First row of each sheet = column headers (keys)
 *  - Each subsequent row = one test data record (Map<String, String>)
 *
 * Usage in a TestNG @DataProvider:
 * <pre>
 *   @DataProvider(name = "loginData")
 *   public Object[][] loginData() {
 *       return DataProviderUtils.toObjectArray(
 *           ExcelDataProvider.getData("login.xlsx", "ValidCredentials")
 *       );
 *   }
 * </pre>
 */
public final class ExcelDataProvider {

    private ExcelDataProvider() {}

    /**
     * Reads all data rows from the given sheet in the given Excel file.
     *
     * @param fileName  classpath-relative path, e.g. "testdata/login.xlsx"
     * @param sheetName the exact sheet tab name to read
     * @return list of row maps — each map is { columnHeader -> cellValue }
     * @throws FrameworkException if the file or sheet is not found, or is empty
     */
    public static List<Map<String, String>> getData(String fileName, String sheetName) {
        validateArgs(fileName, sheetName);

        try (InputStream input = ExcelDataProvider.class
                .getClassLoader().getResourceAsStream(fileName)) {

            if (input == null) {
                throw new FrameworkException(
                        "Excel file not found on classpath: " + fileName);
            }

            try (Workbook workbook = new XSSFWorkbook(input)) {
                Sheet sheet = workbook.getSheet(sheetName);
                if (sheet == null) {
                    throw new FrameworkException(
                            "Sheet '" + sheetName + "' not found in: " + fileName);
                }
                return parseSheet(sheet, fileName, sheetName);
            }

        } catch (FrameworkException fe) {
            throw fe;
        } catch (Exception e) {
            throw new FrameworkException(
                    "Failed to read Excel file: " + fileName
                            + " | sheet: " + sheetName
                            + " | " + e.getMessage());
        }
    }

    // ── private helpers ───────────────────────────────────────────────────────

    private static List<Map<String, String>> parseSheet(
            Sheet sheet, String fileName, String sheetName) {

        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            throw new FrameworkException(
                    "Sheet '" + sheetName + "' in " + fileName
                            + " has no header row.");
        }

        List<String> headers = new ArrayList<>();
        for (Cell cell : headerRow) {
            headers.add(getCellValue(cell).trim());
        }

        List<Map<String, String>> data = new ArrayList<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            Map<String, String> rowMap = new LinkedHashMap<>();
            for (int j = 0; j < headers.size(); j++) {
                Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                rowMap.put(headers.get(j), getCellValue(cell).trim());
            }
            data.add(rowMap);
        }

        if (data.isEmpty()) {
            throw new FrameworkException(
                    "Sheet '" + sheetName + "' in " + fileName
                            + " has a header row but no data rows.");
        }

        return data;
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) return "";
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell);
    }

    private static void validateArgs(String fileName, String sheetName) {
        if (fileName == null || fileName.isBlank()) {
            throw new FrameworkException("ExcelDataProvider: fileName must not be null/blank.");
        }
        if (sheetName == null || sheetName.isBlank()) {
            throw new FrameworkException("ExcelDataProvider: sheetName must not be null/blank.");
        }
    }
}