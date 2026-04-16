package com.kavish.services.hcm.pages;

import com.kavish.core.base.BasePage;
import com.kavish.core.constants.WaitStrategy;
import com.kavish.core.logging.Log;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

/**
 * Page Object for the Apply Leave form.
 * URL: /hcm/leave/apply-leave
 *
 * Fields covered (from screenshot):
 *  Day Duration*  | Leave Type*       | From Date*           | To Date*
 *  Leave Count*   | Reason*           | Reported to Manager* | KT Handover To*
 *  Email Notif Date* | Email Proof File* | Attachment (optional) | Purpose*
 *
 * All locators marked [LOCATOR NEEDED] must be replaced after DOM inspection.
 */
public class ApplyLeavePage extends BasePage {

    // ── Form fields ───────────────────────────────────────────────────────────
    // [LOCATOR NEEDED] - Inspect each field's id / name / data-testid attribute
    private final By dayDurationDropdown    = By.cssSelector("body > div:nth-child(1) > div:nth-child(1) > div:nth-child(2) > main:nth-child(2) > div:nth-child(1) > div:nth-child(5) > div:nth-child(2) > div:nth-child(1) > div:nth-child(2) > select:nth-child(2)");
    private final By leaveTypeDropdown      = By.cssSelector("body > div:nth-child(1) > div:nth-child(1) > div:nth-child(2) > main:nth-child(2) > div:nth-child(1) > div:nth-child(5) > div:nth-child(2) > div:nth-child(2) > div:nth-child(2) > select:nth-child(2)");
    private final By fromDateInput          = By.cssSelector("body > div:nth-child(1) > div:nth-child(1) > div:nth-child(2) > main:nth-child(2) > div:nth-child(1) > div:nth-child(5) > div:nth-child(2) > div:nth-child(3) > div:nth-child(2) > input:nth-child(2)");
    private final By toDateInput            = By.cssSelector("body > div:nth-child(1) > div:nth-child(1) > div:nth-child(2) > main:nth-child(2) > div:nth-child(1) > div:nth-child(5) > div:nth-child(2) > div:nth-child(4) > div:nth-child(2) > input:nth-child(2)");
    private final By leaveCountDisplay      = By.cssSelector("body > div:nth-child(1) > div:nth-child(1) > div:nth-child(2) > main:nth-child(2) > div:nth-child(1) > div:nth-child(5) > div:nth-child(3) > div:nth-child(1) > div:nth-child(2) > input:nth-child(2)");
    private final By reasonDropdown         = By.cssSelector("body > div:nth-child(1) > div:nth-child(1) > div:nth-child(2) > main:nth-child(2) > div:nth-child(1) > div:nth-child(5) > div:nth-child(3) > div:nth-child(2) > div:nth-child(2) > select:nth-child(2)");
    private final By reportedToManagerDrop  = By.cssSelector("body > div:nth-child(1) > div:nth-child(1) > div:nth-child(2) > main:nth-child(2) > div:nth-child(1) > div:nth-child(5) > div:nth-child(3) > div:nth-child(3) > div:nth-child(2) > select:nth-child(2)");
    private final By ktHandoverToDrop       = By.cssSelector("body > div:nth-child(1) > div:nth-child(1) > div:nth-child(2) > main:nth-child(2) > div:nth-child(1) > div:nth-child(5) > div:nth-child(3) > div:nth-child(4) > div:nth-child(2) > select:nth-child(2)");
    private final By emailNotifDateInput    = By.cssSelector("");
    private final By emailProofFileInput    = By.cssSelector("");
    private final By attachmentInput        = By.cssSelector("");
    private final By purposeTextArea        = By.xpath("//div[@id='root']/div/div/main/div/div[3]/div[4]/div[2]/div/div/textarea");
    private final By purposeCharCounter     = By.cssSelector("");

    // ── Submit / Cancel ───────────────────────────────────────────────────────
    // [LOCATOR NEEDED]
    private final By submitButton  = By.xpath("//button[@type='submit']");
    private final By cancelButton  = By.cssSelector("button[type='button'][class*='cancel'], [data-testid='cancel-leave']");

    // ── Validation messages ───────────────────────────────────────────────────
    // [LOCATOR NEEDED] - Inspect the DOM structure of a validation error tooltip/message
    private final By validationErrors       = By.cssSelector(".error-message, .field-error, [data-error='true'], p.text-red-500");
    private final By dayDurationError       = By.xpath("//*[@data-testid='day-duration']/following-sibling::*[contains(@class,'error')] | //*[@for='dayDuration']/following-sibling::p[contains(@class,'error')]");
    private final By leaveTypeError         = By.xpath("//*[@data-testid='leave-type']/following-sibling::*[contains(@class,'error')]");
    private final By fromDateError          = By.xpath("//*[@data-testid='from-date']/following-sibling::*[contains(@class,'error')]");
    private final By toDateError            = By.xpath("//*[@data-testid='to-date']/following-sibling::*[contains(@class,'error')]");
    private final By balanceInsufficientErr = By.cssSelector("[data-testid='balance-error'], .insufficient-balance-msg");
    private final By overlapError           = By.cssSelector("[data-testid='overlap-error'], .overlap-leave-msg");

    // ── Success / Toast ───────────────────────────────────────────────────────
    // [LOCATOR NEEDED] - Inspect toast / snackbar component
    private final By successToast = By.cssSelector(".toast-success, [data-testid='toast-success'], .Toastify__toast--success");
    private final By errorToast   = By.cssSelector(".toast-error,   [data-testid='toast-error'],   .Toastify__toast--error");

    // ─────────────────────────────────────────────────────────────────────────
    // Page state
    // ─────────────────────────────────────────────────────────────────────────

    /** Confirms the Apply Leave form is rendered (submit button is visible). */
    public boolean isLoaded() {
        try {
            waitFor(submitButton, WaitStrategy.VISIBLE);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Fluent field setters
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Selects Day Duration from the dropdown.
     * @param value e.g. "Full Day", "First Half", "Second Half"
     */
    public ApplyLeavePage selectDayDuration(String value) {
        Log.info("ApplyLeavePage — selecting Day Duration: " + value);
        selectByVisibleText(dayDurationDropdown, value);
        return this;
    }

    /**
     * Selects Leave Type from the dropdown.
     * @param value e.g. "PL", "CL", "SL"
     */
    public ApplyLeavePage selectLeaveType(String value) {
        Log.info("ApplyLeavePage — selecting Leave Type: " + value);
        selectByVisibleText(leaveTypeDropdown, value);
        return this;
    }

    /**
     * Sets the From Date.
     * @param date in format accepted by the date picker (e.g. "04-07-2026" or "2026-07-04")
     */
    public ApplyLeavePage setFromDate(String date) {
        Log.info("ApplyLeavePage — setting From Date: " + date);
        clearAndType(fromDateInput, date);
        return this;
    }

    /**
     * Sets the To Date.
     * @param date same format as From Date
     */
    public ApplyLeavePage setToDate(String date) {
        Log.info("ApplyLeavePage — setting To Date: " + date);
        clearAndType(toDateInput, date);
        return this;
    }

    /** Selects the Reason dropdown value. */
    public ApplyLeavePage selectReason(String value) {
        selectByVisibleText(reasonDropdown, value);
        return this;
    }

    /** Selects Reported to Manager (Yes / No). */
    public ApplyLeavePage selectReportedToManager(String value) {
        selectByVisibleText(reportedToManagerDrop, value);
        return this;
    }

    /** Selects KT Handover To employee by visible name. */
    public ApplyLeavePage selectKtHandoverTo(String employeeName) {
        Log.info("ApplyLeavePage — selecting KT Handover To: " + employeeName);
        selectByVisibleText(ktHandoverToDrop, employeeName);
        return this;
    }

    /** Sets the Email Notification Date. */
    public ApplyLeavePage setEmailNotifDate(String date) {
        clearAndType(emailNotifDateInput, date);
        return this;
    }

    /**
     * Uploads a file to the Email Proof File input.
     * @param absolutePath full path to the file on the test executor machine
     */
    public ApplyLeavePage uploadEmailProofFile(String absolutePath) {
        Log.info("ApplyLeavePage — uploading Email Proof File: " + absolutePath);
        // Use PRESENCE strategy to locate the potentially hidden <input type="file">
        waitFor(emailProofFileInput, WaitStrategy.PRESENCE).sendKeys(absolutePath);
        return this;
    }

    /**
     * Uploads a file to the Attachment field.
     * @param absolutePath full path to the file on the test executor machine
     */
    public ApplyLeavePage uploadAttachment(String absolutePath) {
        Log.info("ApplyLeavePage — uploading Attachment: " + absolutePath);
        waitFor(attachmentInput, WaitStrategy.PRESENCE).sendKeys(absolutePath);
        return this;
    }

    /** Types into the Purpose textarea. */
    public ApplyLeavePage enterPurpose(String text) {
        type(purposeTextArea, WaitStrategy.VISIBLE, text);
        return this;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Submit / Cancel
    // ─────────────────────────────────────────────────────────────────────────

    /** Clicks Submit and waits for the success toast to appear. */
    public ApplyLeavePage clickSubmit() {
        Log.info("ApplyLeavePage — clicking Submit");
        click(submitButton, WaitStrategy.CLICKABLE);
        return this;
    }

    /** Clicks Cancel to discard the form. */
    public LeaveManagementPage clickCancel() {
        Log.info("ApplyLeavePage — clicking Cancel");
        click(cancelButton, WaitStrategy.CLICKABLE);
        waitForUrlContains("/hcm/leave");
        return new LeaveManagementPage();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Readers — Leave Count
    // ─────────────────────────────────────────────────────────────────────────

    /** Returns the auto-computed Leave Count value shown on the form. */
    public double getLeaveCount() {
        String raw = getText(leaveCountDisplay, WaitStrategy.VISIBLE).trim();
        try { return Double.parseDouble(raw); }
        catch (NumberFormatException e) { return -1; }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Readers — Validation Errors
    // ─────────────────────────────────────────────────────────────────────────

    public boolean isAnyValidationErrorVisible() {
        List<WebElement> errors = driver().findElements(validationErrors);
        return errors.stream().anyMatch(WebElement::isDisplayed);
    }

    public String getDayDurationError()   { return getTextSafe(dayDurationError); }
    public String getLeaveTypeError()     { return getTextSafe(leaveTypeError); }
    public String getFromDateError()      { return getTextSafe(fromDateError); }
    public String getToDateError()        { return getTextSafe(toDateError); }
    public String getBalanceError()       { return getTextSafe(balanceInsufficientErr); }
    public String getOverlapError()       { return getTextSafe(overlapError); }

    public boolean isBalanceErrorVisible() {
        return isElementVisible(balanceInsufficientErr);
    }

    public boolean isOverlapErrorVisible() {
        return isElementVisible(overlapError);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Readers — Toasts / Success
    // ─────────────────────────────────────────────────────────────────────────

    public boolean isSuccessToastVisible() {
        try {
            waitFor(successToast, WaitStrategy.VISIBLE);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isErrorToastVisible() {
        return isElementVisible(errorToast);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Readers — Purpose char counter
    // ─────────────────────────────────────────────────────────────────────────

    /** Returns the current character counter text, e.g. "45/100". */
    public String getPurposeCharCounterText() {
        return getText(purposeCharCounter, WaitStrategy.VISIBLE);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Field default readers
    // ─────────────────────────────────────────────────────────────────────────

    public String getReportedToManagerValue() {
        WebElement el = waitFor(reportedToManagerDrop, WaitStrategy.VISIBLE);
        return new Select(el).getFirstSelectedOption().getText().trim();
    }

    /** Returns all visible option texts from the Leave Type dropdown. */
    public List<String> getLeaveTypeOptions() {
        WebElement el = waitFor(leaveTypeDropdown, WaitStrategy.VISIBLE);
        return new Select(el).getOptions()
                .stream()
                .map(WebElement::getText)
                .toList();
    }

    /** Returns all visible option texts from the Day Duration dropdown. */
    public List<String> getDayDurationOptions() {
        WebElement el = waitFor(dayDurationDropdown, WaitStrategy.VISIBLE);
        return new Select(el).getOptions()
                .stream()
                .map(WebElement::getText)
                .toList();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Private helpers
    // ─────────────────────────────────────────────────────────────────────────

    private void selectByVisibleText(By locator, String text) {
        WebElement el = waitFor(locator, WaitStrategy.VISIBLE);
        new Select(el).selectByVisibleText(text);
    }

    /**
     * Clears a date/text input and types the value.
     * Uses JS to clear because some custom date pickers ignore Selenium's clear().
     */
    private void clearAndType(By locator, String value) {
        WebElement el = waitFor(locator, WaitStrategy.VISIBLE);
        ((JavascriptExecutor) driver()).executeScript("arguments[0].value='';", el);
        el.sendKeys(value);
    }

    private boolean isElementVisible(By locator) {
        try {
            return waitFor(locator, WaitStrategy.VISIBLE).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    private String getTextSafe(By locator) {
        try {
            return getText(locator, WaitStrategy.VISIBLE).trim();
        } catch (Exception e) {
            return "";
        }
    }
}