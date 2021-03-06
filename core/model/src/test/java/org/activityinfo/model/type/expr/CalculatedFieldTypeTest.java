package org.activityinfo.model.type.expr;

import net.lightoze.gwt.i18n.server.LocaleProxy;
import net.sf.cglib.core.Local;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.ResourceId;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

public class CalculatedFieldTypeTest {
    
    @BeforeClass
    public static void setupI18N() {
        LocaleProxy.initialize();
    }

    @Test
    public void serialization() {

        FormField field = new FormField(ResourceId.generateId());
        field.setType(new CalculatedFieldType("A+B"));

        Record record = field.asRecord();
        System.out.println(record);

        FormField read = FormField.fromRecord(record);
        assertThat(read.getType(), instanceOf(CalculatedFieldType.class));

        CalculatedFieldType readType = (CalculatedFieldType) read.getType();
        assertThat(readType.getExpression().getExpression(), equalTo("A+B"));
    }


    @Test
    public void emptySerialization() {

        FormField field = new FormField(ResourceId.generateId());
        field.setType(new CalculatedFieldType());

        Record record = field.asRecord();
        System.out.println(record);

        FormField read = FormField.fromRecord(record);
        assertThat(read.getType(), instanceOf(CalculatedFieldType.class));

        CalculatedFieldType readType = (CalculatedFieldType) read.getType();
        assertThat(readType.getExpression(), nullValue());
    }
}