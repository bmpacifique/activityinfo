package org.activityinfo.test.pageobject.gxt;

import org.activityinfo.test.pageobject.api.FluentElement;
import org.openqa.selenium.By;

import static org.activityinfo.test.pageobject.api.XPathBuilder.*;

public class GxtPanel {

    private final FluentElement panel;

    public static GxtPanel find(FluentElement container, String heading) {
        FluentElement panel = container.find().span(withText(heading)).ancestor().div(withClass("x-panel")).waitForFirst();
        return new GxtPanel(panel);
    }

    public static GxtPanel findStartsWith(FluentElement container, String headingStartsWith) {
        FluentElement panel = container.find().span(containingText(headingStartsWith)).ancestor().div(withClass("x-panel")).waitForFirst();
        return new GxtPanel(panel);
    }

    public GxtPanel(FluentElement panel) {
        this.panel = panel;
    }
    
    public GxtTree tree() {
        return GxtTree.tree(panel.findElement(By.className("x-tree3")));
    }

    public GxtTree treeGrid() {
        return GxtTree.treeGrid(panel.findElement(By.className("x-treegrid")));
    }

    public ToolbarMenu toolbarMenu() {
        return ToolbarMenu.find(panel);
    }
}
