package org.activityinfo.legacy.shared.reports.model;

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

import org.activityinfo.legacy.shared.reports.content.DimensionCategory;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public class MapSymbol {

    private Map<Dimension, DimensionCategory> categories = new HashMap<Dimension, DimensionCategory>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MapSymbol that = (MapSymbol) o;

        if (categories != null ? !categories.equals(that.categories) : that.categories != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return categories != null ? categories.hashCode() : 0;
    }

    public void put(Dimension dimension, DimensionCategory category) {
        categories.put(dimension, category);
    }

    public DimensionCategory get(Dimension dimension) {
        return categories.get(dimension);
    }
}
