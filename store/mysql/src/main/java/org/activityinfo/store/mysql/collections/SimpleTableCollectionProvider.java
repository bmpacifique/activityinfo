package org.activityinfo.store.mysql.collections;

import com.google.common.base.Optional;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.store.CollectionPermissions;
import org.activityinfo.service.store.ResourceCollection;
import org.activityinfo.store.mysql.cursor.QueryExecutor;
import org.activityinfo.store.mysql.mapping.SimpleTable;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class SimpleTableCollectionProvider implements CollectionProvider {
    
    private final SimpleTable table;
    private final Authorizer authorizer;

    public SimpleTableCollectionProvider(SimpleTable table, CollectionPermissions permissions) {
        this.table = table;
        this.authorizer = new ConstantAuthorizer(permissions);
    }

    @Override
    public boolean accept(ResourceId formClassId) {
        return table.accept(formClassId);
    }

    @Override
    public ResourceCollection openCollection(QueryExecutor executor, ResourceId formClassId) throws SQLException {
        return new SimpleTableCollection(table.getMapping(executor, formClassId), authorizer, executor);
    }

    @Override
    public Optional<ResourceId> lookupCollection(QueryExecutor executor, ResourceId resourceId) throws SQLException {
        return table.lookupCollection(executor, resourceId);
    }

    @Override
    public Map<ResourceId, ResourceCollection> openCollections(QueryExecutor executor, Set<ResourceId> collectionIds) throws SQLException {
        Map<ResourceId, ResourceCollection> map = new HashMap<>();
        for (ResourceId collectionId : collectionIds) {
            if(table.accept(collectionId)) {
                map.put(collectionId, openCollection(executor, collectionId));
            }
        }
        return map;
    }

}
