package org.activityinfo.ui.client.component.formdesigner.skip;
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

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.model.expr.functions.*;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.Cardinality;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.model.type.attachment.AttachmentType;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.time.LocalDateType;
import org.activityinfo.ui.client.component.form.field.FieldWidgetMode;
import org.activityinfo.ui.client.component.form.field.FormFieldWidget;
import org.activityinfo.ui.client.component.form.field.FormFieldWidgetFactory;
import org.activityinfo.ui.client.component.formdesigner.container.FieldWidgetContainer;
import org.activityinfo.ui.client.util.GwtUtil;

import java.util.Collections;
import java.util.List;

/**
 * @author yuriyz on 7/24/14.
 */
public class RelevanceRowPresenter {

    private static final List<ComparisonOperator> EQUALITY_OPERATORS = Lists.newArrayList(
            EqualFunction.INSTANCE, NotEqualFunction.INSTANCE
    );

    public static final List<ComparisonOperator> COMPARISON_OPERATORS = Lists.newArrayList(
            EqualFunction.INSTANCE, NotEqualFunction.INSTANCE,
            BooleanFunctions.GREATER, BooleanFunctions.GREATER_OR_EQUAL,
            BooleanFunctions.LESS, BooleanFunctions.LESS_OR_EQUAL
    );

    public static final List<ExprFunction> SET_FUNCTIONS = Collections.unmodifiableList(Lists.newArrayList(
            ContainsAllFunction.INSTANCE, ContainsAnyFunction.INSTANCE, NotContainsAllFunction.INSTANCE, NotContainsAnyFunction.INSTANCE
    ));

    private final FieldWidgetContainer fieldWidgetContainer;
    private final RelevanceRow view = new RelevanceRow();
    private final FormFieldWidgetFactory widgetFactory;
    private final Label errorMessage = new Label(I18N.CONSTANTS.blankValueIsNotAllowed());

    private FormFieldWidget valueWidget = null;
    private FieldValue value;
    private RowData rowData;

    public RelevanceRowPresenter(final FieldWidgetContainer fieldWidgetContainer) {
        this.fieldWidgetContainer = fieldWidgetContainer;
        this.widgetFactory = new FormFieldWidgetFactory(fieldWidgetContainer.getFormDesigner().getResourceLocator(), FieldWidgetMode.NORMAL);

        initFormFieldBox();
        initFunction();
        initValueWidgetLater();
        initJoinFunction();

        errorMessage.addStyleName("help-block");
    }

    // depends on selected field type
    private void initValueWidgetLater() {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                initValueWidget();
            }
        });
    }

    private void initValueWidget() {
        view.getValueContainer().clear();

        ValueUpdater<FieldValue> valueUpdater = new ValueUpdater<FieldValue>() {
            @Override
            public void update(FieldValue value) {
                RelevanceRowPresenter.this.value = value;

                if (value == null) {
                    view.getValueContainer().addStyleName("has-error");
                    view.getValueContainer().add(errorMessage);
                } else {
                    view.getValueContainer().removeStyleName("has-error");
                    if (view.getValueContainer().getWidgetIndex(errorMessage) != -1) {
                        view.getValueContainer().remove(errorMessage);
                    }
                }
            }
        };

        widgetFactory.createWidget(new FormClass(ResourceId.generateId()), getSelectedFormField(), valueUpdater).then(new AsyncCallback<FormFieldWidget>() {
            @Override
            public void onFailure(Throwable caught) {
                caught.printStackTrace();
            }

            @Override
            public void onSuccess(FormFieldWidget widget) {
                valueWidget = widget;
                view.getValueContainer().clear();
                view.getValueContainer().add(widget);

                if (rowData != null) {
                    valueWidget.setValue(rowData.getValue());
                    RelevanceRowPresenter.this.value = rowData.getValue();
                }
            }
        });
    }


    private void initFormFieldBox() {
        view.getFormfield().clear();

        List<FormField> formFields = Lists.newArrayList(fieldWidgetContainer.getFormDesigner().getFormClass().getFields());
        formFields.remove(fieldWidgetContainer.getFormField()); // remove selected field

        for (FormField formField : formFields) {
            if (formField.getType() instanceof AttachmentType) {
                continue;
            }
            view.getFormfield().addItem(formField.getLabel(), formField.getId().asString());
        }

        GwtUtil.addTooltipToOptions(view.getFormfield().getElement(), 100, formFields);
        view.getFormfield().setTitle(getSelectedFormField().getLabel());

        view.getFormfield().addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                initFunction();
                initValueWidgetLater();
                view.getFormfield().setTitle(getSelectedFormField().getLabel());
            }
        });
    }

    public FormField getSelectedFormField() {
        String formFieldId = view.getFormfield().getValue(view.getFormfield().getSelectedIndex());
        return fieldWidgetContainer.getFormDesigner().getFormClass().getField(ResourceId.valueOf(formFieldId));
    }

    // depends on selected field type
    private void initFunction() {
        view.getFunction().clear();

        for(ExprFunction function : functionsForType(getSelectedFormField().getType())) {
            view.getFunction().addItem(function.getLabel(), function.getId());
        }
    }

    private List<? extends ExprFunction> functionsForType(FieldType type) {
        if(type instanceof EnumType) {
            if(((EnumType) type).getCardinality() == Cardinality.MULTIPLE) {
                return SET_FUNCTIONS;
            }
        } else if(type instanceof ReferenceType) {
            if(((ReferenceType) type).getCardinality() == Cardinality.MULTIPLE) {
                return SET_FUNCTIONS;
            }
        } else if (type instanceof QuantityType || type instanceof LocalDateType) {
            return COMPARISON_OPERATORS;
        }
        return EQUALITY_OPERATORS;
    }

    private void initJoinFunction() {
        view.getJoinFunction().addItem(I18N.CONSTANTS.and(), AndFunction.NAME);
        view.getJoinFunction().addItem(I18N.CONSTANTS.or(), OrFunction.NAME);
        view.getJoinFunction().setSelectedIndex(0);
    }

    public RelevanceRow getView() {
        return view;
    }

    public FieldValue getValue() {
        return value;
    }

    public void updateWith(final RowData rowData) {
        this.rowData = rowData;
        setSelectedValue(view.getJoinFunction(), rowData.getJoinFunction().getId());

        // formfield list must be initialized before function list
        setSelectedValue(view.getFormfield(), rowData.getFormField().getId().asString());

        initFunction(); // re-init function (funtions depends on formfield type)
        setSelectedValue(view.getFunction(), rowData.getFunction().getId());

        initValueWidgetLater();
    }

    private static void setSelectedValue(ListBox listBox, String value) {
        for (int i = 0; i < listBox.getItemCount(); i++) {
            String itemValue = listBox.getValue(i);
            if (!Strings.isNullOrEmpty(itemValue) && itemValue.equals(value)) {
                listBox.setSelectedIndex(i);
                listBox.setTitle(listBox.getItemText(i));
                return;
            }
        }
    }

    public FormFieldWidget getValueWidget() {
        return valueWidget;
    }
}
