package org.activityinfo.test.pageobject.web.design;
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

import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.test.pageobject.api.FluentElement;
import org.activityinfo.test.pageobject.gxt.GxtModal;
import org.activityinfo.test.pageobject.gxt.GxtPanel;
import org.activityinfo.test.pageobject.gxt.GxtTree;
import org.activityinfo.test.pageobject.gxt.ToolbarMenu;
import org.activityinfo.test.pageobject.web.entry.ImportSchemaDialog;

import static org.activityinfo.test.pageobject.api.XPathBuilder.containingText;

/**
 * @author yuriyz on 04/02/2015.
 */
public class DesignPage {

    private FluentElement container;
    private GxtTree designTree;
    private ToolbarMenu toolbarMenu;

    public DesignPage(FluentElement container) {

        GxtPanel panel = GxtPanel.findStartsWith(container, "Design ");

        this.container = container;
        this.designTree = panel.treeGrid();
        this.toolbarMenu = panel.toolbarMenu();
    }

    public GxtTree getDesignTree() {
        return designTree;
    }

    public ToolbarMenu getToolbarMenu() {
        return toolbarMenu;
    }

    public FluentElement getContainer() {
        return container;
    }

    public ImportSchemaDialog clickImport() {
        getToolbarMenu().clickButton(I18N.CONSTANTS.importText());
        return ImportSchemaDialog.waitOnDialog(getContainer());
    }
    
    public GxtModal newBetaForm() {
        getToolbarMenu().clickButton(I18N.CONSTANTS.newText());
        container.root().find().span(containingText(I18N.CONSTANTS.newForm())).clickWhenReady();
        return GxtModal.waitForModal(container.root());
    }
}
