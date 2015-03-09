package org.activityinfo.test.pageobject.web;

import org.activityinfo.test.pageobject.api.FluentElement;

import static org.activityinfo.test.pageobject.api.XPathBuilder.containingText;


public class SettingsMenu {
    
    private FluentElement menu;

    public SettingsMenu(FluentElement menu) {
        this.menu = menu;
    }

    public void enableOfflineMode() {
        menu.find().div(containingText("offline")).clickWhenReady();
    }
}
