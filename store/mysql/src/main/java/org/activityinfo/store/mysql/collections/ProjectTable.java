package org.activityinfo.store.mysql.collections;

import com.google.common.base.Optional;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.store.mysql.cursor.QueryExecutor;
import org.activityinfo.store.mysql.mapping.SimpleTable;
import org.activityinfo.store.mysql.mapping.TableMapping;
import org.activityinfo.store.mysql.mapping.TableMappingBuilder;

import java.sql.SQLException;

public class ProjectTable implements SimpleTable {
    @Override
    public boolean accept(ResourceId formClassId) {
        return formClassId.getDomain() == CuidAdapter.PROJECT_CLASS_DOMAIN;
    }

    @Override
    public TableMapping getMapping(QueryExecutor executor, ResourceId formClassId) throws SQLException {
        int databaseId = CuidAdapter.getLegacyIdFromCuid(formClassId);
        TableMappingBuilder mapping = TableMappingBuilder.newMapping(formClassId, "project");
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<ResourceId> lookupCollection(QueryExecutor queryExecutor, ResourceId id) throws SQLException {
        return Optional.absent();
    }
}