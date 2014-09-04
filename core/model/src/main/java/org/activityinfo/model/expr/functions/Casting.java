package org.activityinfo.model.expr.functions;

import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.ReferenceValue;
import org.activityinfo.model.type.enumerated.EnumFieldValue;
import org.activityinfo.model.type.number.Quantity;
import org.activityinfo.model.type.primitive.BooleanFieldValue;

import java.util.Set;

public class Casting {
    public static Quantity toQuantity(FieldValue fieldValue) {
        if(fieldValue instanceof Quantity) {
            return (Quantity) fieldValue;
        } else if(fieldValue == null) {
            return new Quantity(Double.NaN);
        } else {
            throw new RuntimeException("Cannot cast " + fieldValue + " to quantity");
        }
    }

    public static boolean toBoolean(FieldValue value) {
        if(value == BooleanFieldValue.TRUE) {
            return true;
        } else if(value == BooleanFieldValue.FALSE) {
            return false;
        } else {
            throw new RuntimeException("Cannot cast [" + value + "] to boolean");
        }
    }

    public static Set<ResourceId> toSet(FieldValue value) {
        if (value instanceof EnumFieldValue) {
            return ((EnumFieldValue) value).getValueIds();
        } else if (value instanceof ReferenceValue) {
            return ((ReferenceValue) value).getResourceIds();
        } else {
            throw new RuntimeException("Cannot cast [" + value + "] to Set<ResourceId>");
        }
    }
}
