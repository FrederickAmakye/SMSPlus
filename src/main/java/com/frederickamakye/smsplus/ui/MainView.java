package com.frederickamakye.smsplus.ui;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class MainView extends TabPane {
    private final DashboardView dashboardView;
    private final StudentsView studentsView;
    private final ReportsView reportsView;

    public MainView() {
        dashboardView = new DashboardView();
        studentsView = new StudentsView();
        reportsView = new ReportsView();

        // Dashboard screen
        Tab dashboardTab = new Tab("Dashboard");
        dashboardTab.setClosable(false);
        dashboardTab.setContent(dashboardView);

        // Students screen
        Tab studentsTab = new Tab("Students");
        studentsTab.setClosable(false);
        studentsTab.setContent(studentsView);

        // Reports screen
        Tab reportsTab = new Tab("Reports");
        reportsTab.setClosable(false);
        reportsTab.setContent(reportsView);

        // Import/Export screen
        Tab importExportTab = new Tab("Import/Export");
        importExportTab.setClosable(false);
        importExportTab.setContent(new ImportExportView());

        // Settings screen
        Tab settingsTab = new Tab("Settings");
        settingsTab.setClosable(false);
        settingsTab.setContent(new SettingsView());

        // ================= REFRESH LOGIC =================
        dashboardTab.setOnSelectionChanged(event -> {
            if (dashboardTab.isSelected()) {
                dashboardView.refresh();
            }
        });
        
        studentsTab.setOnSelectionChanged(event -> {
            if (studentsTab.isSelected()) {
                studentsView.refresh();
            }
        });

        reportsTab.setOnSelectionChanged(event -> {
            if (reportsTab.isSelected()) {
                reportsView.refresh();
            }
        });


        getTabs().addAll(dashboardTab, studentsTab, reportsTab, importExportTab, settingsTab);
    }
}