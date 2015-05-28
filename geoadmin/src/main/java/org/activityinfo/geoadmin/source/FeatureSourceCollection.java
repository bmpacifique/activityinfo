package org.activityinfo.geoadmin.source;

import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.store.ResourceCollection;
import org.activityinfo.service.store.ColumnQueryBuilder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.PropertyDescriptor;


public class FeatureSourceCollection implements ResourceCollection {

    private final ResourceId resourceId;
    private final SimpleFeatureSource featureSource;

    public FeatureSourceCollection(ResourceId resourceId, SimpleFeatureSource featureSource) {
        this.resourceId = resourceId;
        this.featureSource = featureSource;
    }


    @Override
    public Resource get(ResourceId resourceId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FormClass getFormClass() {
        FormClass formClass = new FormClass(resourceId);
        formClass.setLabel(featureSource.getName().getLocalPart());
        for (AttributeDescriptor attribute : featureSource.getSchema().getAttributeDescriptors()) {
            formClass.addElement(toField(attribute));
        }
        return formClass;
    }


    private FormField toField(PropertyDescriptor descriptor) {
        FormField field = new FormField(ResourceId.valueOf(descriptor.getName().getURI()));
        field.setLabel(descriptor.getName().getLocalPart());
        field.setCode(descriptor.getName().getLocalPart());
        field.setType(AttributeTypeAdapters.of(descriptor).createType());
        return field;
    }


    @Override
    public ColumnQueryBuilder newColumnQuery() {
        return new FeatureQueryBuilder(featureSource);
    }

}