package org.activityinfo.store.mysql.collections;

import org.activityinfo.model.form.FormField;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.expr.CalculatedFieldType;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.store.mysql.mapping.*;

public class ActivityField {
    
    int id;
    private final String category;
    private final FormField formField;

    public ActivityField(int id, String category, FormField formField) {
        this.id = id;
        this.category = category;
        this.formField = formField;
    }

    public int getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public FormField getFormField() {
        return formField;
    }

    public boolean isIndicator() {
        return !isAttributeGroup();
    }

    public boolean isAttributeGroup() {
        return formField.getType() instanceof EnumType;
    }
    
    public String getColumnName() {
        if(isAttributeGroup()) {
            return "attributeId";
        } else {
            if(formField.getType() instanceof QuantityType) {
                return "Value";
            } else {
                return "TextValue";
            }
        }
    }

    public FieldValueConverter getConverter() {
        if(isAttributeGroup()) {
            return new ReferenceConverter(CuidAdapter.ATTRIBUTE_DOMAIN);
        } else if(formField.getType() instanceof QuantityType) {
            QuantityType type = (QuantityType) formField.getType();
            return new QuantityConverter(type.getUnits());
        } else {
            return TextConverter.INSTANCE;
        }
    }

    public boolean isCalculated() {
        return formField.getType() instanceof CalculatedFieldType;
    }

    public ResourceId getResourceId() {
        return formField.getId();
    }

    //
//    public Join getJoin() {
//
//        String tableId = "I" + resourceId;
//        Join join = new Join(periodJoin, tableId,
//                ("LEFT JOIN indicatorvalue {table} ON (period.reportingPeriodId={table}.reportingPeriodId AND " +
//                        "{table}.indicatorId={indicatorId})")
//                        .replace("{table}", tableId)
//                        .replace("{indicatorId}", Integer.toString(id)));
//
//
//    }
}
