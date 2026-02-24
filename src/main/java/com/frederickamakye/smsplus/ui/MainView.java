package com.frederickamakye.smsplus.ui;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class MainView extends TabPane {

    public MainView() {

        // Students screen
        Tab studentsTab = new Tab("Students");
        studentsTab.setClosable(false);
        studentsTab.setContent(new StudentsView());

        // Reports screen
        // Tab reportsTab = new Tab("Reports");
        // reportsTab.setClosable(false);
        // reportsTab.setContent(new ReportsView());

        getTabs().addAll(studentsTab);
    }
}