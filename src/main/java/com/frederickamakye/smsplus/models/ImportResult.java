package com.frederickamakye.smsplus.models;

import java.util.ArrayList;
import java.util.List;

public class ImportResult {

    private int successCount;
    private int errorCount;

    private final List<String> errorRows;

    public ImportResult() {
        errorRows = new ArrayList<>();
    }

    public void incrementSuccess() {
        successCount++;
    }

    public void incrementError(String row) {
        errorCount++;
        errorRows.add(row);
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public List<String> getErrorRows() {
        return errorRows;
    }
}