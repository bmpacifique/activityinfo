package org.activityinfo.ui.client.component.form.field;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.NarrativeValue;
import org.activityinfo.promise.Promise;
import org.activityinfo.ui.client.widget.TextArea;

public class NarrativeFieldWidget implements FormFieldWidget<NarrativeValue> {

    private final TextArea textArea;
    private final ValueUpdater<NarrativeValue> updater;

    public NarrativeFieldWidget(final ValueUpdater<NarrativeValue> updater) {
        this.updater = updater;
        this.textArea = new TextArea();
        this.textArea.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                fireValueChanged();
            }
        });
        this.textArea.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                fireValueChanged();
            }
        });
    }

    @Override
    public void fireValueChanged() {
        updater.update(NarrativeValue.valueOf(this.textArea.getValue()));
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        textArea.setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() {
        return textArea.isReadOnly();
    }

    @Override
    public Promise<Void> setValue(NarrativeValue value) {
        textArea.setValue(value.getText());
        return Promise.done();
    }

    @Override
    public void clearValue() {
        textArea.setValue(null);
    }

    @Override
    public void setType(FieldType type) {
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public Widget asWidget() {
        return textArea;
    }
}
