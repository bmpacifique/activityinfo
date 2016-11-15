package org.activityinfo.server.endpoint.odk;

import com.google.inject.ImplementedBy;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.formTree.FormClassProvider;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.lookup.ReferenceChoice;

import java.util.List;
import java.util.Set;

@ImplementedBy(ResourceLocatorSyncImpl.class)
public interface ResourceLocatorSync extends FormClassProvider {
    
    void persist(FormInstance formInstance);

    FormClass getFormClass(ResourceId resourceId);

    List<ReferenceChoice> getReferenceChoices(Set<ResourceId> range);

    void persist(FormClass formClass);
}