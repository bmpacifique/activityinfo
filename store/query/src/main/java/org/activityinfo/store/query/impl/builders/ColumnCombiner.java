package org.activityinfo.store.query.impl.builders;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import org.activityinfo.model.query.BooleanColumnView;
import org.activityinfo.model.query.ColumnType;
import org.activityinfo.model.query.ColumnView;
import org.activityinfo.model.query.DoubleArrayColumnView;
import org.activityinfo.store.query.impl.Slot;
import org.activityinfo.store.query.impl.views.StringArrayColumnView;

import java.util.List;

/**
 * Supplies a Column that is combined from several source columns.
 *
 */
public class ColumnCombiner implements Slot<ColumnView> {

    private List<Slot<ColumnView>> sources;

    private ColumnView result;

    public ColumnCombiner(List<Slot<ColumnView>> sources) {
        Preconditions.checkArgument(sources.size() > 1, "source.size() > 1");
        this.sources = sources;
    }

    @Override
    public ColumnView get() {
        if(result == null) {
            result = combine();
        }
        return result;
    }

    private ColumnView combine() {
        ColumnView[] cols = new ColumnView[sources.size()];
        for(int j=0;j<cols.length;++j) {
            cols[j] = sources.get(j).get();
        }
        ColumnType columnType = sources.get(0).get().getType();

        switch(columnType) {

            case STRING:
                return combineString(cols);
            case NUMBER:
                return combineDouble(cols);
            case BOOLEAN:
                return combineBoolean(cols);
        }
        throw new UnsupportedOperationException();
    }


    private ColumnView combineString(ColumnView[] cols) {
        int numRows = cols[0].numRows();
        int numCols = cols.length;

        String[] values = new String[numRows];

        for(int i=0;i!=numRows;++i) {
            for(int j=0;j!=numCols;++j) {
                String value = cols[j].getString(i);
                if(value != null) {
                    values[i] = value;
                    break;
                }
            }
        }

        return new StringArrayColumnView(values);
    }

    @VisibleForTesting
    static ColumnView combineDouble(ColumnView[] cols) {
        int numRows = cols[0].numRows();
        int numCols = cols.length;

        double[] values = new double[numRows];

        for(int i=0;i!=numRows;++i) {
            values[i] = Double.NaN;
            for(int j=0;j!=numCols;++j) {
                double value = cols[j].getDouble(i);
                if(!Double.isNaN(value)) {
                    values[i] = value;
                    break;
                }
            }
        }
        return new DoubleArrayColumnView(values);
    }


    private ColumnView combineBoolean(ColumnView[] cols) {
        int numRows = cols[0].numRows();
        int numCols = cols.length;

        int[] values = new int[numRows];


        for(int i=0;i!=numRows;++i) {
            values[i] = ColumnView.NA;
            for(int j=0;j!=numCols;++j) {
                int value = cols[j].getBoolean(j);
                if(value != ColumnView.NA) {
                    values[i] = value;
                    break;
                }
            }
        }
        return new BooleanColumnView(values);
    }
}
