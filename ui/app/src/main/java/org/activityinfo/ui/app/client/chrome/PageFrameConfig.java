package org.activityinfo.ui.app.client.chrome;
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

import org.activityinfo.ui.app.client.dialogs.DeleteResourceAction;
import org.activityinfo.ui.app.client.dialogs.EditLabelDialog;

/**
 * @author yuriyz on 9/23/14.
 */
public class PageFrameConfig {

    public PageFrameConfig() {
    }

    private EditLabelDialog enableRename;
    private DeleteResourceAction enableDeletion;
    private boolean editAllowed = false;

    public EditLabelDialog getEnableRename() {
        return enableRename;
    }

    public PageFrameConfig setEnableRename(EditLabelDialog enableRename) {
        this.enableRename = enableRename;
        return this;
    }

    public DeleteResourceAction enableDeletion() {
        return enableDeletion;
    }

    public PageFrameConfig setEnableDeletion(DeleteResourceAction enableDeletion) {
        this.enableDeletion = enableDeletion;
        return this;
    }

    public boolean isEditAllowed() {
        return editAllowed;
    }

    public PageFrameConfig setEditAllowed(boolean editAllowed) {
        this.editAllowed = editAllowed;
        return this;
    }
}