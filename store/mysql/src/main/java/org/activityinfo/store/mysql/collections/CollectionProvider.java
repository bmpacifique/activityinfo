package org.activityinfo.store.mysql.collections;

import com.google.common.base.Optional;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.store.ResourceCollection;
import org.activityinfo.store.mysql.cursor.QueryExecutor;

import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

public interface CollectionProvider {

    boolean accept(ResourceId formClassId);

    ResourceCollection openCollection(QueryExecutor executor, ResourceId formClassId) throws SQLException;

    Optional<ResourceId> lookupCollection(QueryExecutor executor, ResourceId resourceId) throws SQLException;
    
    Map<ResourceId, ResourceCollection> openCollections(QueryExecutor executor, Set<ResourceId> resourceIds) throws SQLException;
}
