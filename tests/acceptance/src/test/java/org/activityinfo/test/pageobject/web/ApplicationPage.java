package org.activityinfo.test.pageobject.web;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import org.activityinfo.test.pageobject.api.FluentElement;
import org.activityinfo.test.pageobject.gxt.Gxt;
import org.activityinfo.test.pageobject.web.design.DesignTab;
import org.activityinfo.test.pageobject.web.reports.ReportsTab;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.activityinfo.test.pageobject.api.XPathBuilder.withText;
import static org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfElementLocated;

/**
 * Interface to the single-pageobject application
 */
public class ApplicationPage {

    
    private static final By SETTINGS_BUTTON = By.xpath("//div[text() = 'ActivityInfo']/following-sibling::div[2]");
    private static final By DESIGN_TAB = By.xpath("//div[contains(text(), 'Design')]");
    
    private final FluentElement page;


    @Inject
    public ApplicationPage(WebDriver webDriver) {
        this.page = new FluentElement(webDriver);        
    }
    
    public ApplicationPage(FluentElement page) {
        this.page = page;
    }

    public Dashboard dashboard() {
        return new Dashboard(container());
    }
    
    public void waitUntilLoaded() {
        page.waitUntil(invisibilityOfElementLocated(By.id("loading")));
    }
    
    public SettingsMenu openSettingsMenu() {
        page.findElement(SETTINGS_BUTTON).click();
        
        return new SettingsMenu(page);
    }
    
    public OfflineMode getOfflineMode() {
        return page.waitFor(new Function<WebDriver, OfflineMode>() {
            @Nullable
            @Override
            public OfflineMode apply(WebDriver driver) {
                List<WebElement> elements = driver.findElements(By.className("x-status-text"));
                for (WebElement element : elements) {
                    if (element.getText().contains("Working online") || element.getText().contains("Last sync")) {
                        return OfflineMode.ONLINE;

                    } else if (element.getText().contains("Working offline")) {
                        return OfflineMode.OFFLINE;
                    }
                }
                return null;
            }
        });
    }

    public void assertOfflineModeLoads() {
        page.wait(5, MINUTES).until(new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver driver) {
                List<WebElement> elements = driver.findElements(By.className("x-status-text"));
                for (WebElement element : elements) {
                    if (element.getText().contains("Working offline")) {
                        return true;

                    } else if (element.getText().contains("Sync error")) {
                        throw new AssertionError(element.getText());


                    } else if (element.getText().contains("%")) {
                        System.out.println(element.getText());
                    }
                }
                return false;
            }
        });
    }

    public DesignTab navigateToDesignTab() {
        try {
            page.findElement(DESIGN_TAB).click();
        } catch(Exception ignored) {
        }
        return new DesignTab(container());
    }
    
    public ReportsTab navigateToReportsTab() {
        FluentElement container = container();
        container.find().div(withText("Reports")).clickWhenReady();
        
        return new ReportsTab(container);
    }

    private FluentElement container() {
        return page.waitFor(By.className(Gxt.BORDER_LAYOUT_CONTAINER));
    }

}