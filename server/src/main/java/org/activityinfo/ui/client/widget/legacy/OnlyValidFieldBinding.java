package org.activityinfo.ui.client.widget.legacy;
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

import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.widget.form.Field;

/**
 * @author yuriyz on 10/23/2014.
 */
public class OnlyValidFieldBinding extends FieldBinding {

    /**
     * Creates a new binding instance.
     *
     * @param field    the bound field for the binding
     * @param property property name
     */
    public OnlyValidFieldBinding(Field field, String property) {
        super(field, property);
    }

    @Override
    public void updateModel() {
        if (field.isValid()) { // update model only if field value is valid
            super.updateModel();
        }
    }
}
