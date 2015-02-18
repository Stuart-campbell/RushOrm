package co.uk.rushorm.core;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Stuart on 16/02/15.
 */
public class RushMetaData {

    private final String id;
    private final long created;
    private long updated;
    private long version;

    public RushMetaData() {
        id = UUID.randomUUID().toString();
        created = new Date().getTime();
        version = 0;
    }

    public RushMetaData(String id, long version) {
        this.id = id;
        this.version = version;
        created = new Date().getTime();
    }

    public RushMetaData(String id, long created, long updated, long version) {
        this.id = id;
        this.created = created;
        this.updated = updated;
        this.version = version;
    }

    public void save() {
        version++;
        updated = new Date().getTime();
    }

    public boolean isSaved() {
        return version > 0;
    }

    public String getId() {
        return id;
    }

    public long getCreated() {
        return created;
    }

    public long getUpdated() {
        return updated;
    }

    public long getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RushMetaData that = (RushMetaData) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);
    }

}
