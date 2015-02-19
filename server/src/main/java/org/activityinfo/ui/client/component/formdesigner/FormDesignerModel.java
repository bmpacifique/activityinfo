package org.activityinfo.ui.client.component.formdesigner;
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

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormElement;
import org.activityinfo.model.form.FormElementContainer;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.Cardinality;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.period.PredefinedPeriods;
import org.activityinfo.model.type.subform.SubformConstants;

import java.util.List;
import java.util.Map;

/**
 * @author yuriyz on 01/21/2015.
 */
public class FormDesignerModel {

    private final BiMap<ResourceId, FormClass> formFieldToSubFormClass = HashBiMap.create();

    private final FormClass rootFormClass;

    public FormDesignerModel(FormClass rootFormClass) {
        Preconditions.checkNotNull(rootFormClass);
        this.rootFormClass = rootFormClass;
    }

    public FormClass getRootFormClass() {
        return rootFormClass;
    }

    public FormClass registerNewSubform(ResourceId formFieldId) {
        final FormClass formClass = new FormClass(ResourceId.generateId());
        formClass.setOwnerId(rootFormClass.getId());

        FormField type = formClass.addField(SubformConstants.TYPE_FIELD_ID);
        type.setVisible(false);
        type.setType(new ReferenceType()
                        .setCardinality(Cardinality.SINGLE)
                        .setRange(PredefinedPeriods.MONTHLY.getResourceId())
        );

        FormField tabCount = formClass.addField(SubformConstants.TAB_COUNT_FIELD_ID);
        tabCount.setVisible(false);
        tabCount.setType(new QuantityType().setUnits(Integer.toString(SubformConstants.DEFAULT_TAB_COUNT)));

        registerSubform(formFieldId, formClass);
        return formClass;
    }

    public List<FormClass> getSubforms() {
        return Lists.newArrayList(formFieldToSubFormClass.values());
    }

    public FormDesignerModel registerSubform(ResourceId formFieldId, FormClass formClass) {
        Preconditions.checkNotNull(formFieldId);
        Preconditions.checkNotNull(formClass);

        formFieldToSubFormClass.put(formFieldId, formClass);
        return this;
    }

    public FormClass getSubform(ResourceId formFieldId) {
        return formFieldToSubFormClass.get(formFieldId);
    }

    public FormClass getFormClass(ResourceId formClassId) {
        return (FormClass) getElementContainer(formClassId);
    }

    public FormElementContainer getElementContainer(ResourceId resourceId) {
        FormElementContainer fromRoot = getRootFormClass().getElementContainer(resourceId); // try root first
        if (fromRoot != null) {
            return fromRoot;
        }
        for (FormClass subform : formFieldToSubFormClass.values()) {
            if (subform.getId().equals(resourceId)) {
                return subform;
            }
            FormElementContainer fromSubform = subform.getElementContainer(resourceId);
            if (fromSubform != null) {
                return fromSubform;
            }
        }
        return null;
    }

    public void updateFieldOrder(FormDesignerPanel formDesignerPanel) {

        Map<ResourceId, FormField> fieldMap = Maps.newHashMap();
        for (FormField field : rootFormClass.getFields()) {
            fieldMap.put(field.getId(), field);
        }

        // update the order of the model
        List<FormElement> elements = Lists.newArrayList();
        FlowPanel panel = formDesignerPanel.getDropPanel();
        for (int i = 0; i != panel.getWidgetCount(); ++i) {
            Widget widget = panel.getWidget(i);
            String fieldId = widget.getElement().getAttribute(FormDesignerConstants.DATA_FIELD_ID);
            elements.add(fieldMap.get(ResourceId.valueOf(fieldId)));
        }

        rootFormClass.getElements().clear();
        rootFormClass.getElements().addAll(elements);
    }

    public void removeSubform(FormClass subForm) {
        ResourceId formFieldId = formFieldToSubFormClass.inverse().get(subForm);
        formFieldToSubFormClass.remove(formFieldId);
        rootFormClass.removeField(formFieldId);
    }

    public FormField getSubformOwnerField(FormClass subform) {
        ResourceId ownerFieldId = formFieldToSubFormClass.inverse().get(subform);
        return rootFormClass.getField(ownerFieldId);
    }

    /**
     * Returns formfields of root formclass and all subforms.
     *
     * @return formfields of root formclass and all subforms
     */
    public List<FormField> getAllFormsFields() {
        List<FormField> formFields = Lists.newArrayList(getRootFormClass().getFields());
        for (FormClass subForm : getSubforms()) {
            formFields.addAll(subForm.getFields());
        }
        return formFields;
    }
}