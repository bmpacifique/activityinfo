package org.activityinfo.test.pageobject.gxt;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.test.pageobject.api.FluentElement;
import org.activityinfo.test.pageobject.api.XPathBuilder;
import org.activityinfo.test.pageobject.web.components.Form;
import org.activityinfo.test.pageobject.web.components.ModalDialog;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static org.activityinfo.test.pageobject.api.XPathBuilder.withRole;


public class GxtModal extends ModalDialog {

    public static final String CLASS_NAME = "x-window";
    private FluentElement windowElement;
    
    private GxtFormPanel form;
    
    public GxtModal(FluentElement parent) {
        this.windowElement = parent.root().waitFor(By.className(CLASS_NAME));
    }

    public GxtModal(FluentElement parent, int timeout) {
        this.windowElement = parent.root().waitFor(By.className(CLASS_NAME), timeout);
    }

    public static GxtModal waitForModal(final FluentElement container) {
        container.waitUntil(new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver input) {
                return container.root().exists(By.className(GxtModal.CLASS_NAME));
            }
        });
        return new GxtModal(container);
    }

    public FluentElement getWindowElement() {
        return windowElement;
    }

    public String getTitle() {
        return windowElement.find().span(withRole("heading")).first().text();
    }

    @Override
    public Form form() {
        if(form == null) {
            form = new GxtFormPanel(windowElement.findElement(By.tagName("form")));
        }
        return form;
    }

    public void close() {
        clickButton("Close");
    }

    public void closeByWindowHeaderButton() {
        windowElement.find().div(XPathBuilder.withClass("x-tool-close")).clickWhenReady();
    }

    public void discardChanges() {
        clickButton("Discard Changes");
    }

    public void clickButton(String buttonLabel) {
        windowElement.find().button(XPathBuilder.withText(buttonLabel)).clickWhenReady();
    }

    @Override
    public void accept() {
        accept(I18N.CONSTANTS.save());
    }

    public void accept(String buttonName) {
        windowElement.find().button(XPathBuilder.withText(buttonName)).clickWhenReady();
        windowElement.waitUntil(new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver input) {
                
                if(!windowElement.isDisplayed()) {
                    return true;
                }
                
                // check for an error
                Optional<GxtMessageBox> mb = GxtMessageBox.get(windowElement);
                if(mb.isPresent() && mb.get().isWarning()) {
                    throw new AssertionError("Could not save form: " + mb.get().getMessage());
                }
                
                // is the box closed?
                return false;
            }
        });
        
    }

    public boolean isClosed() {
        return !windowElement.isDisplayed();
    }
}
