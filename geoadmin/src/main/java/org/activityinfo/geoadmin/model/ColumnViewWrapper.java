package org.activityinfo.geoadmin.model;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.activityinfo.model.query.ColumnType;
import org.activityinfo.model.query.ColumnView;
import org.activityinfo.model.type.geo.Extents;


public class ColumnViewWrapper implements ColumnView {
    private JsonArray array;
    private final int numRows;

    public ColumnViewWrapper(int numRows, JsonArray array) {
        this.array = array;
        this.numRows = numRows;
    }

    @Override
    public ColumnType getType() {
        return ColumnType.STRING;
    }

    @Override
    public int numRows() {
        return numRows;
    }

    @Override
    public Object get(int row) {
        return getString(row);
    }

    @Override
    public double getDouble(int row) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getString(int row) {
        Preconditions.checkPositionIndex(row, numRows);
        
        JsonElement jsonElement = array.get(row);
        if(jsonElement.isJsonNull()) {
            return null;
        } else {
            return jsonElement.getAsString();
        }
    }

    @Override
    public Extents getExtents(int row) {
        return null;
    }

    @Override
    public int getBoolean(int row) {
        throw new UnsupportedOperationException();
    }


    @Override
    public String toString() {
        return array.toString();
    }
}
