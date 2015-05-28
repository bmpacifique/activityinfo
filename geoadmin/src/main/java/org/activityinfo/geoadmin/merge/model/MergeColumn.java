package org.activityinfo.geoadmin.merge.model;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.model.query.ColumnView;

import java.util.HashSet;
import java.util.Set;


public class MergeColumn {
    private FormTree.Node field;
    private ColumnView columnView;

    public MergeColumn(FormTree.Node field, ColumnView columnView) {
        Preconditions.checkNotNull(columnView, field.getField().getLabel());
        this.field = field;
        this.columnView = columnView;
    }
    
    public Set<String> uniqueValues() {
        Set<String> set = new HashSet<>();
        for(int i=0;i<columnView.numRows();++i) {
            String value = columnView.getString(i);
            if(!Strings.isNullOrEmpty(value)) {
                set.add(value.toUpperCase());
            }
        }
        return set;
    }
    
    public String getLabel() {
        if(field.isRoot()) {
            return field.getField().getLabel();
        } else {
            return field.getDefiningFormClass().getLabel() + " " + field.getField().getLabel();
        }
    }

    @Override
    public String toString() {
        return "MergeColumn{" + getLabel() + "}";
    }

    public ColumnView getView() {
        return columnView;
    }
}