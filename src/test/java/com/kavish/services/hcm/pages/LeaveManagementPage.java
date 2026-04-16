package com.kavish.services.hcm.pages;

import com.kavish.core.base.BasePage;
import com.kavish.core.constants.WaitStrategy;
import com.kavish.core.logging.Log;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class LeaveManagementPage extends BasePage {

    // ═════════════════════════════════════════════════════════════════════════
    // TAB NAVIGATION
    // Verified from absolute XPath — using positional buttons under the tab bar.
    // These are stable as long as tab order doesn't change.
    // ═════════════════════════════════════════════════════════════════════════

    private final By overviewTab    = By.xpath("/html/body/div[1]/div/div[1]/main/div/div[2]/div/div/button[1]");
    private final By applyLeaveTab  = By.xpath("/html/body/div[1]/div/div[1]/main/div/div[2]/div/div/button[2]");
    private final By myTeamTab      = By.xpath("/html/body/div[1]/div/div[1]/main/div/div[2]/div/div/button[3]");
    private final By leavePolicyTab = By.xpath("/html/body/div[1]/div/div[1]/main/div/div[2]/div/div/button[4]");
    private final By outdoorDutyTab = By.xpath("/html/body/div[1]/div/div[1]/main/div/div[2]/div/div/button[5]");

    // ═════════════════════════════════════════════════════════════════════════
    // CALENDAR
    // Month label verified from absolute XPath.
    // Prev/Next: the buttons sit adjacent to the <h2> month label — located
    // by their position relative to that known heading rather than aria-label
    // (which may not be set in this app).
    // ═════════════════════════════════════════════════════════════════════════

    private final By calendarMonthLabel = By.xpath(
            "/html/body/div[1]/div[1]/div[1]/main[1]/div[1]/div[3]/div[1]/div[1]/div[1]/h2[1]");

    // Prev/Next sit as sibling buttons to the h2 inside the calendar header row.
    // [LOCATOR NEEDED] If these don't click, inspect the <button> elements left
    // and right of the month label and replace with their absolute XPath.
    private final By calendarPrevBtn = By.xpath(
            "/html/body/div[1]/div[1]/div[1]/main[1]/div[1]/div[3]/div[1]/div[1]/div[1]/button[1]");
    private final By calendarNextBtn = By.xpath(
            "/html/body/div[1]/div[1]/div[1]/main[1]/div[1]/div[3]/div[1]/div[1]/div[1]/button[2]");

    // Highlighted leave dates — the calendar day cells that have a leave marker.
    // [LOCATOR NEEDED] Apply a leave, then inspect the date cell to find the
    // distinguishing class added to it and replace the selector below.
    private final By highlightedDates = By.cssSelector("div:nth-child(17)");

    // ═════════════════════════════════════════════════════════════════════════
    // LEAVE BALANCE SECTION
    // ═════════════════════════════════════════════════════════════════════════

    private final By leaveBalanceSection = By.xpath(
            "//div[starts-with(@class,'grid grid-cols-1 gap-3')]");

    // PL row values — XPath: inside the block containing the "PL" badge,
    // find the card whose header text is CREDITED/UTILIZED/BALANCE and read
    // the numeric sibling <span>.
    private final By plCreditedValue = By.xpath(
            "//div[.//span[normalize-space(text())='PL']]" +
                    "//div[.//span[normalize-space(text())='CREDITED']][1]" +
                    "//span[normalize-space(text()) and " +
                    "       translate(normalize-space(text()),'0123456789.','')=''][last()]");

    private final By plUtilizedValue = By.xpath(
            "//div[.//span[normalize-space(text())='PL']]" +
                    "//div[.//span[normalize-space(text())='UTILIZED']][1]" +
                    "//span[normalize-space(text()) and " +
                    "       translate(normalize-space(text()),'0123456789.','')=''][last()]");

    private final By plBalanceValue = By.xpath(
            "//div[.//span[normalize-space(text())='PL']]" +
                    "//div[.//span[normalize-space(text())='BALANCE']][1]" +
                    "//span[normalize-space(text()) and " +
                    "       translate(normalize-space(text()),'0123456789.','')=''][last()]");

    // CL row values — same pattern, scoped to the block containing "CL" badge
    private final By clCreditedValue = By.xpath(
            "//div[.//span[normalize-space(text())='CL']]" +
                    "//div[.//span[normalize-space(text())='CREDITED']][1]" +
                    "//span[normalize-space(text()) and " +
                    "       translate(normalize-space(text()),'0123456789.','')=''][last()]");

    private final By clUtilizedValue = By.xpath(
            "//div[.//span[normalize-space(text())='CL']]" +
                    "//div[.//span[normalize-space(text())='UTILIZED']][1]" +
                    "//span[normalize-space(text()) and " +
                    "       translate(normalize-space(text()),'0123456789.','')=''][last()]");

    private final By clBalanceValue = By.xpath(
            "//div[.//span[normalize-space(text())='CL']]" +
                    "//div[.//span[normalize-space(text())='BALANCE']][1]" +
                    "//span[normalize-space(text()) and " +
                    "       translate(normalize-space(text()),'0123456789.','')=''][last()]");

    // ═════════════════════════════════════════════════════════════════════════
    // APPROVAL STATUS OVERVIEW — COUNT EXTRACTION
    // ═════════════════════════════════════════════════════════════════════════

    // Approval Workflow Status — located by heading text which is stable
    private final By approvalWorkflowSection = By.xpath(
            "//*[contains(normalize-space(text()),'Approval Workflow Status')]");

    // ═════════════════════════════════════════════════════════════════════════
    // TAB ACTIONS
    // ═════════════════════════════════════════════════════════════════════════

    public ApplyLeavePage clickApplyLeaveTab() {
        Log.info("LeaveManagementPage — clicking Apply Leave tab");
        click(applyLeaveTab, WaitStrategy.CLICKABLE);
        waitForUrlContains("/apply-leave");
        return new ApplyLeavePage();
    }

    public LeaveManagementPage clickOverviewTab() {
        Log.info("LeaveManagementPage — clicking Overview tab");
        click(overviewTab, WaitStrategy.CLICKABLE);
        waitForUrlContains("/leave");
        return this;
    }

    public LeaveManagementPage clickMyTeamTab() {
        click(myTeamTab, WaitStrategy.CLICKABLE);
        return this;
    }

    public LeaveManagementPage clickLeavePolicyTab() {
        click(leavePolicyTab, WaitStrategy.CLICKABLE);
        return this;
    }

    public LeaveManagementPage clickOutdoorDutyTab() {
        click(outdoorDutyTab, WaitStrategy.CLICKABLE);
        return this;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // CALENDAR ACTIONS
    // ═════════════════════════════════════════════════════════════════════════

    public String getCalendarMonthLabel() {
        return getText(calendarMonthLabel, WaitStrategy.VISIBLE).trim();
    }

    public LeaveManagementPage clickPreviousMonth() {
        click(calendarPrevBtn, WaitStrategy.CLICKABLE);
        return this;
    }

    public LeaveManagementPage clickNextMonth() {
        click(calendarNextBtn, WaitStrategy.CLICKABLE);
        return this;
    }

    public int getHighlightedLeaveDateCount() {
        return driver().findElements(highlightedDates).size();
    }

    // ═════════════════════════════════════════════════════════════════════════
    // LEAVE BALANCE READERS
    // ═════════════════════════════════════════════════════════════════════════

    public boolean isLeaveBalanceSectionVisible() {
        try {
            waitFor(leaveBalanceSection, WaitStrategy.VISIBLE);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public double getPLCredited() { return parseDouble(getText(plCreditedValue, WaitStrategy.VISIBLE)); }
    public double getPLUtilized() { return parseDouble(getText(plUtilizedValue, WaitStrategy.VISIBLE)); }
    public double getPLBalance()  { return parseDouble(getText(plBalanceValue,  WaitStrategy.VISIBLE)); }

    public double getCLCredited() { return parseDouble(getText(clCreditedValue, WaitStrategy.VISIBLE)); }
    public double getCLUtilized() { return parseDouble(getText(clUtilizedValue, WaitStrategy.VISIBLE)); }
    public double getCLBalance()  { return parseDouble(getText(clBalanceValue,  WaitStrategy.VISIBLE)); }

    // ═════════════════════════════════════════════════════════════════════════
    // APPROVAL STATUS READERS
    // ═════════════════════════════════════════════════════════════════════════

    public int getPendingCount()   { return getApprovalCount("Pending");   }
    public int getApprovedCount()  { return getApprovalCount("Approved");  }
    public int getCancelledCount() { return getApprovalCount("Cancelled"); }
    public int getRejectedCount()  { return getApprovalCount("Rejected");  }


    private int getApprovalCount(String label) {
        try {
            WebDriverWait apiWait = new WebDriverWait(driver(), Duration.ofSeconds(5));

            // Step 1 — wait until the numeric child appears inside the circle
            // that contains the given label as one of its children's text nodes.
            By numericChild = By.xpath(
                    // Container: an element that has a direct or close child with ONLY the label text
                    "//*[" +
                            "  *[normalize-space(text())='" + label + "'] or " +
                            "  normalize-space(text())='" + label + "'" +
                            "]" +
                            // The numeric descendant: text is non-empty AND all chars are digits
                            "//*[" +
                            "  string-length(normalize-space(text())) > 0 and " +
                            "  translate(normalize-space(text()),'0123456789','')='' " +
                            "]"
            );

            apiWait.until(ExpectedConditions.presenceOfElementLocated(numericChild));

            // Step 2 — read the text of the FIRST matching numeric element.
            // (If multiple numeric children exist — e.g. a date number in
            // a nearby calendar cell also matches — we scope more tightly in
            // the JS fallback below.)
            List<WebElement> matches = driver().findElements(numericChild);
            for (WebElement el : matches) {
                String text = el.getText().trim();
                if (text.matches("\\d+")) {
                    int count = Integer.parseInt(text);
                    Log.info("getApprovalCount(" + label + ") = " + count);
                    return count;
                }
            }

            // No match found via XPath — fall through to JS
            throw new Exception("No purely-numeric child found for label: " + label);

        } catch (Exception e) {
            Log.info("getApprovalCount(" + label + ") XPath strategy failed: "
                    + e.getMessage() + " — trying JS fallback");
            return getApprovalCountViaJS(label);
        }
    }


    private int getApprovalCountViaJS(String label) {
        try {
            String script =
                    "var label = arguments[0];" +
                            // Walk every element, looking for a compact container
                            // (childElementCount 1–4) whose innerText contains the label
                            "var all = document.querySelectorAll('*');" +
                            "for (var i = 0; i < all.length; i++) {" +
                            "  var el = all[i];" +
                            "  if (el.childElementCount < 1 || el.childElementCount > 6) continue;" +
                            "  var text = (el.innerText || '').trim();" +
                            "  if (!text.includes(label)) continue;" +
                            // Strip the label and any surrounding whitespace/newlines
                            "  var stripped = text.split(label).join('').replace(/\\s+/g, '').trim();" +
                            // What remains should be a pure number
                            "  if (/^\\d+$/.test(stripped)) {" +
                            "    return stripped;" +
                            "  }" +
                            "}" +
                            "return '-1';";

            Object result = ((JavascriptExecutor) driver()).executeScript(script, label);
            String raw = (result == null) ? "-1" : result.toString().trim();
            Log.info("getApprovalCountViaJS(" + label + ") = " + raw);
            return Integer.parseInt(raw);

        } catch (Exception ex) {
            Log.info("getApprovalCountViaJS(" + label + ") also failed: " + ex.getMessage());
            return -1;
        }
    }

    public boolean isApprovalWorkflowSectionVisible() {
        try {
            waitFor(approvalWorkflowSection, WaitStrategy.VISIBLE);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // GENERAL STATE
    // ═════════════════════════════════════════════════════════════════════════

    public boolean isLoaded() {
        try {
            waitFor(calendarMonthLabel, WaitStrategy.VISIBLE);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // PRIVATE HELPERS
    // ═════════════════════════════════════════════════════════════════════════

    private double parseDouble(String raw) {
        try { return Double.parseDouble(raw.trim()); }
        catch (NumberFormatException e) { return -1; }
    }

    private int parseInt(String raw) {
        try { return Integer.parseInt(raw.trim()); }
        catch (NumberFormatException e) { return -1; }
    }
}