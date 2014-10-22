package org.activityinfo.ui.component.form.field;
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

import com.google.common.collect.Iterables;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.table.InstanceLabelTable;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.ReferenceValue;
import org.activityinfo.promise.Promise;
import org.activityinfo.ui.component.form.field.suggest.InstanceSuggestOracle;
import org.activityinfo.ui.component.form.field.suggest.InstanceSuggestion;
import org.activityinfo.ui.widget.SuggestBox;

import java.util.List;
import java.util.Objects;

/**
 * @author yuriyz on 2/10/14.
 */
public class SuggestBoxWidget implements ReferenceFieldWidget {

    private final SuggestBox suggestBox;

    private ResourceId value;
    private InstanceLabelTable range;

    public SuggestBoxWidget(InstanceLabelTable range, final ValueUpdater<ReferenceValue> valueUpdater) {
        this.range = range;
        this.suggestBox = new SuggestBox(new InstanceSuggestOracle(range));
        this.suggestBox.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
            @Override
            public void onSelection(SelectionEvent<SuggestOracle.Suggestion> event) {
                InstanceSuggestion suggestion = (InstanceSuggestion) event.getSelectedItem();
                if(!Objects.equals(suggestion.getInstanceId(), value)) {
                    valueUpdater.update(new ReferenceValue(suggestion.getInstanceId()));
                }
            }
        });
    }

    public List<FormInstance> getRange() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        suggestBox.setEnabled(!readOnly);
    }

    @Override
    public Promise<Void> setValue(ReferenceValue value) {
        ResourceId newValue = Iterables.getFirst(value.getResourceIds(), null);
        if(!Objects.equals(newValue, this.value)) {
            this.value = newValue;
            if(newValue == null) {
                suggestBox.setValue(null);
            } else {
                suggestBox.setValue(findDisplayLabel(newValue));
            }
        }
        return Promise.done();
    }

    @Override
    public void clearValue() {
        suggestBox.setValue(null);
    }

    @Override
    public void setType(FieldType type) {

    }

    private String findDisplayLabel(ResourceId newValue) {
        for(int i=0;i!=range.getNumRows();++i) {
            if(range.getId(i).equals(newValue)) {
                return range.getLabel(i);
            }
        }
        return newValue.asString();
    }

    @Override
    public Widget asWidget() {
        return suggestBox;
    }
}