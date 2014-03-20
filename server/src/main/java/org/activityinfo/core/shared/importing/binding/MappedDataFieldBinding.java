package org.activityinfo.core.shared.importing.binding;

import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.form.FormField;
import org.activityinfo.core.shared.importing.SourceRow;
import org.activityinfo.core.shared.type.converter.Converter;

/**
 * Imports a data (non-reference) field mapped to a single source column
 */
public class MappedDataFieldBinding implements FieldBinding {

    private final FormField field;
    private final Converter converter;
    private final int sourceColumn;
    private boolean newField;

    public MappedDataFieldBinding(FormField field, int sourceColumn, Converter converter) {
        this.field = field;
        this.sourceColumn = sourceColumn;
        this.converter = converter;
    }

    @Override
    public Cuid getFieldId() {
        return field.getId();
    }

    @Override
    public FormField getField() {
        return field;
    }

    public int getSourceColumn() {
        return sourceColumn;
    }

    @Override
    public Object getFieldValue(SourceRow row) {
        String importedValue = getImportedValue(row);
        if(importedValue == null) {
            return null;
        } else {
            try {
                return converter.convert(importedValue);
            } catch(Exception e) {
                return null;
            }
        }
    }

    public String getImportedValue(SourceRow row) {
        return row.getColumnValue(sourceColumn);
    }

    public boolean isNewField() {
        return newField;
    }

    public void setNewField(boolean newField) {
        this.newField = newField;
    }

    @Override
    public void accept(FieldBindingColumnVisitor visitor) {
        visitor.visitMappedColumn(this);
    }

    public Object convert(String importedValue) {
        return converter.convert(importedValue);
    }
}
