package org.activityinfo.store.mysql.collections;

import com.google.common.base.Optional;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceUpdate;
import org.activityinfo.service.store.CollectionPermissions;
import org.activityinfo.service.store.ColumnQueryBuilder;
import org.activityinfo.service.store.ResourceCollection;
import org.activityinfo.store.mysql.cursor.QueryExecutor;
import org.activityinfo.store.mysql.mapping.TableMapping;
import org.activityinfo.store.mysql.metadata.DatabaseTargetForm;


public class TargetCollection implements ResourceCollection {
    
    private final QueryExecutor executor;
    private final DatabaseTargetForm target;
    private final TableMapping mapping;
    
    public TargetCollection(QueryExecutor executor, DatabaseTargetForm target) {
        this.target = target;
        this.executor = executor;
        this.mapping = target.buildMapping();
    }

    @Override
    public CollectionPermissions getPermissions(int userId) {
        return CollectionPermissions.readonly();
    }

    @Override
    public Optional<Resource> get(ResourceId resourceId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FormClass getFormClass() {
        return mapping.getFormClass();
    }

    @Override
    public void add(ResourceUpdate update) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(ResourceUpdate update) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ColumnQueryBuilder newColumnQuery() {
        return new TargetQueryBuilder(executor, target, mapping);
    }

    @Override
    public long cacheVersion() {
        return 0;
    }
}
