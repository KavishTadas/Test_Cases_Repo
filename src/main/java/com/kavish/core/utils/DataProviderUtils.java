package com.kavish.core.utils;

import com.kavish.core.exceptions.FrameworkException;

import java.util.List;
import java.util.Map;

/**
 * Utility to bridge the data provider output (List of Maps) with
 * the Object[][] format that TestNG @DataProvider methods must return.
 *
 * All three providers (Excel, JSON, CSV) return List<Map<String, String>>.
 * TestNG @DataProvider needs Object[][].
 * This class does that single conversion so no test class repeats it.
 *
 * Usage:
 * <pre>
 *   @DataProvider(name = "loginData")
 *   public Object[][] loginData() {
 *       return DataProviderUtils.toObjectArray(
 *           ExcelDataProvider.getData("testdata/login.xlsx", "Login")
 *       );
 *   }
 *
 *   @Test(dataProvider = "loginData")
 *   public void verifyLogin(Map<String, String> data) {
 *       String username = data.get("username");
 *       String password = data.get("password");
 *       ...
 *   }
 * </pre>
 */
public final class DataProviderUtils {

    private DataProviderUtils() {}

    /**
     * Converts a list of row maps into a TestNG-compatible Object[][].
     * Each row becomes Object[1] wrapping the Map so the test method
     * receives a single Map<String, String> parameter per row.
     *
     * @param data list of row maps from any of the data provider classes
     * @return Object[][] ready to return from a @DataProvider method
     * @throws FrameworkException if data is null or empty
     */
    public static Object[][] toObjectArray(List<Map<String, String>> data) {
        if (data == null || data.isEmpty()) {
            throw new FrameworkException(
                    "DataProviderUtils: cannot convert null or empty data to Object[][].");
        }

        Object[][] result = new Object[data.size()][1];
        for (int i = 0; i < data.size(); i++) {
            result[i][0] = data.get(i);
        }
        return result;
    }
}