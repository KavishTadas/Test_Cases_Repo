package com.kavish.core.constants;

public final class FrameworkConstants {

    private FrameworkConstants() {}

    // ── Config ────────────────────────────────────────────────────────────────
    public static final String CONFIG_PATH = "src/main/resources/config/";

    // ── All test output lives under Maven's target/ directory ─────────────────
    // Jenkins can archive the entire subtree with: target/test-output/**
    public static final String TEST_OUTPUT_DIR  = "target/test-output/";
    public static final String SCREENSHOTS_DIR  = TEST_OUTPUT_DIR + "screenshots/";
    public static final String LOGS_DIR         = TEST_OUTPUT_DIR + "logs/";

    public static final String EVIDENCE_DIR     = TEST_OUTPUT_DIR + "evidence/";  // optional zip staging

    // ── Legacy — kept for any existing references, points to same root ────────
    /** @deprecated Use {@link #TEST_OUTPUT_DIR} instead. */
    @Deprecated
    public static final String REPORT_PATH = TEST_OUTPUT_DIR;

    // ── Waits ─────────────────────────────────────────────────────────────────
    public static final int DEFAULT_WAIT = 10;
}