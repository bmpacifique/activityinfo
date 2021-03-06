package org.activityinfo.geoadmin.source;

import com.google.common.base.Optional;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceUpdate;
import org.activityinfo.service.store.CollectionPermissions;
import org.activityinfo.service.store.ColumnQueryBuilder;
import org.activityinfo.service.store.ResourceCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.PropertyDescriptor;

import java.util.List;


public class FeatureSourceCollection implements ResourceCollection {

    public static final String FIELD_ID_PREFIX = "ATTRIB";
    
    private final ResourceId resourceId;
    private final SimpleFeatureSource featureSource;

    public FeatureSourceCollection(ResourceId resourceId, SimpleFeatureSource featureSource) {
        this.resourceId = resourceId;
        this.featureSource = featureSource;
    }


    @Override
    public CollectionPermissions getPermissions(int userId) {
        return CollectionPermissions.full();
    }

    @Override
    public Optional<Resource> get(ResourceId resourceId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FormClass getFormClass() {
        FormClass formClass = new FormClass(resourceId);
        formClass.setLabel(featureSource.getName().getLocalPart());
        List<AttributeDescriptor> attributes = featureSource.getSchema().getAttributeDescriptors();
        for (int i = 0; i < attributes.size(); i++) {
            AttributeDescriptor attribute = attributes.get(i);
            formClass.addElement(toField(i, attribute));
        }
        return formClass;
    }

    @Override
    public void update(ResourceUpdate update) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(ResourceUpdate update) {
        throw new UnsupportedOperationException();
    }


    private FormField toField(int i, PropertyDescriptor descriptor) {
        FormField field = new FormField(ResourceId.valueOf(FIELD_ID_PREFIX + i));
        field.setLabel(descriptor.getName().getLocalPart());
        field.setCode(descriptor.getName().getLocalPart());
        field.setType(AttributeTypeAdapters.of(descriptor).createType());
        return field;
    }


    @Override
    public ColumnQueryBuilder newColumnQuery() {
        return new FeatureQueryBuilder(featureSource);
    }

    @Override
    public long cacheVersion() {
        return 0;
    }


}
