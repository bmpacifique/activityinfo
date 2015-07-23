package org.activityinfo.test.pageobject.web;

import org.activityinfo.test.pageobject.api.FluentElement;
import org.activityinfo.test.pageobject.api.FluentElements;
import org.activityinfo.test.pageobject.gxt.Gxt;
import org.activityinfo.test.pageobject.web.reports.DashboardPortlet;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.activityinfo.test.pageobject.api.XPathBuilder.containingText;
import static org.activityinfo.test.pageobject.api.XPathBuilder.withClass;
import static org.activityinfo.test.pageobject.api.XPathBuilder.withText;


public class Dashboard {

    private final FluentElement container;

    public Dashboard(FluentElement container) {
        this.container = container;
    }
    
    public void assertAtLeastOnePortletIsVisible() {
        container.waitUntil(ExpectedConditions.visibilityOfElementLocated(By.className(Gxt.PORTLET)));
    }
    
    public DashboardPortlet findPortlet(String name) {
        FluentElement portlet = container.find().span(withClass("x-panel-header-text"), withText(name)).ancestor()
                .div(withClass(Gxt.PORTLET))
                .waitForFirst();
        
        return new DashboardPortlet(portlet);
    }
    
}
