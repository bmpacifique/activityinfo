package org.activityinfo.model.lock;
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

import com.google.common.collect.Lists;
import org.activityinfo.model.resource.IsRecord;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.ResourceId;

import java.util.List;

/**
 * @author yuriyz on 10/05/2015.
 */
public class ResourceLock implements IsRecord { // it's not Resource because we are going to keep lock inside payload of FormClass

    private ResourceId id;
    private ResourceId ownerId;

    private String name;
    private boolean enabled;
    private String expression;

    public ResourceLock() {
    }

    public ResourceId getId() {
        return id;
    }

    public void setId(ResourceId id) {
        this.id = id;
    }

    public ResourceId getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(ResourceId ownerId) {
        this.ownerId = ownerId;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public static ResourceLock fromRecord(Record record) {
        ResourceLock lock = new ResourceLock();
        lock.id = record.getResourceId("id");
        lock.ownerId = record.getResourceId("ownerId");
        lock.name = record.getString("name");
        lock.expression = record.getString("expression");
        lock.enabled = record.getBoolean("enabled");
        return lock;
    }

    public static List<ResourceLock> fromRecords(List<Record> records) {
        List<ResourceLock> locks = Lists.newArrayList();
        for (Record record : records) {
            locks.add(fromRecord(record));
        }
        return locks;
    }

    @Override
    public Record asRecord() {
        Record record = new Record();
        record.set("id", id);
        record.set("ownerId", ownerId);
        record.set("name", name);
        record.set("expression", expression);
        record.set("enabled", enabled);

        return record;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResourceLock that = (ResourceLock) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ResourceLock{" +
                "id=" + id +
                ", ownerId=" + ownerId +
                ", name='" + name + '\'' +
                ", expression='" + expression + '\'' +
                ", enabled='" + enabled + '\'' +
                '}';
    }
}
