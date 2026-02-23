package com.frederickamakye.smsplus.models;

public class GpaBandSummary {

    private final String band;
    private final int totalStudents;

    public GpaBandSummary(String band, int totalStudents) {
        this.band = band;
        this.totalStudents = totalStudents;
    }

    public String getBand() {
        return band;
    }

    public int getTotalStudents() {
        return totalStudents;
    }
}