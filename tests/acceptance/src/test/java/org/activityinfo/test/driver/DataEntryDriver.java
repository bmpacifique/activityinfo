package org.activityinfo.test.driver;


import org.joda.time.LocalDate;

import java.util.List;

/**
 * Common interface to entering data through the web,
 * ODK, etc.
 */
public interface DataEntryDriver {


    /**
     * Navigates to the next field.
     * @return true if there is a next field, or false if the end of the 
     * form has been reached.
     */
    boolean nextField();

    /**
     * Submits the form
     * @return the new id of the form
     */
    void submit() throws InterruptedException;

    /**
     *
     * @return the label of the current data entry field
     */
    String getLabel();


    /**
     * 
     * Fills the current field with text
     */
    void fill(String text);
    
    void fill(LocalDate date);

    void select(String itemLabel);

    boolean isValid();


    /**
     *
     * @return true if it possible to navigate to the next field
     */
    boolean isNextEnabled();

    void sendKeys(CharSequence keys);

    List<String> availableValues();

    void close();
}
