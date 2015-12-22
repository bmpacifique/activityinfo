package org.activityinfo.test.pageobject.web.design.designer;
/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.google.common.base.Preconditions;
import cucumber.api.DataTable;
import gherkin.formatter.model.DataTableRow;
import org.activityinfo.test.pageobject.api.FluentElement;
import org.activityinfo.test.pageobject.api.XPathBuilder;
import org.activityinfo.test.pageobject.bootstrap.BsFormPanel;
import org.activityinfo.test.pageobject.bootstrap.BsModal;

import java.util.List;

/**
 * @author yuriyz on 12/22/2015.
 */
public class RelevanceDialog {

    private final BsModal modal;

    public RelevanceDialog(BsModal modal) {
        this.modal = modal;
    }

    public BsModal getModal() {
        return modal;
    }

    public void set(DataTable dataTable) {
        for (int i = 0; i < dataTable.getGherkinRows().size(); i++) {
            DataTableRow row = dataTable.getGherkinRows().get(i);
            List<String> cells = row.getCells();
            Preconditions.checkState(cells.size() == 4);

            String fieldLabel = cells.get(0);
            String operation = cells.get(1);
            String value = cells.get(2);
            String controlType = cells.get(3);

            FluentElement lastRow = lastRow();

            List<FluentElement> select = lastRow.find().select().waitForList().list();

            BsFormPanel.BsField fieldSelector = new BsFormPanel.BsField(select.get(0));
            fieldSelector.select(fieldLabel);

            BsFormPanel.BsField operationSelector = new BsFormPanel.BsField(select.get(1));
            operationSelector.select(operation);

            switch(controlType) {
                case "radio":
                    FluentElement radioLabel = lastRow().find().label(XPathBuilder.withText(value)).first();
                    radioLabel.find().ancestor().span(XPathBuilder.withClass("radio")).waitForFirst().clickWhenReady();
                    break;
            }


        }
    }

    private FluentElement lastRow() {
        List<FluentElement> rows = modal.form().getForm().find().div(XPathBuilder.withClass("row")).waitForList().list();
        return rows.get(rows.size() - 1);
    }
}
