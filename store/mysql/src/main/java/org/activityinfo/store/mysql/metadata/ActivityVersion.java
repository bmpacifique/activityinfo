package org.activityinfo.store.mysql.metadata;


class ActivityVersion {
    private int id;
    private long schemaVersion;
    private long siteVersion;

    public ActivityVersion(int id, long schemaVersion, long siteVersion) {
        this.id = id;
        this.schemaVersion = schemaVersion;
        this.siteVersion = siteVersion;
    }

    public String getSchemaCacheKey() {
        return "activity:metadata:" + id + "@" + schemaVersion;
    }

    public int getId() {
        return id;
    }

    public long getSchemaVersion() {
        return schemaVersion;
    }

    public long getSiteVersion() {
        return siteVersion;
    }
}
