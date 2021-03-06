package org.activityinfo.test.driver;
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

/**
 * @author yuriyz on 06/09/2015.
 */
public enum ControlType {
    RADIO_BUTTONS("radiobuttons"),
    CHECK_BOXES("checkboxes"),
    SUGGEST_BOX("suggestbox"),
    DROP_DOWN("dropdown");

    private final String value;

    ControlType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ControlType fromValue(String value) {
        for (ControlType type : values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new RuntimeException("Failed to identify control type for value: " + value);
    }
}
