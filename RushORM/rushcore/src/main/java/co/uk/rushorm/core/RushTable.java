package co.uk.rushorm.core;

public class RushTable {

    public void save() {
        RushCore.getInstance().save(this);
    }

    public void save(RushCallback callback) {
        RushCore.getInstance().save(this, callback);
    }

    public void delete() {
        RushCore.getInstance().delete(this);
    }

    public void delete(RushCallback callback) {
        RushCore.getInstance().delete(this, callback);
    }

    public long getId() {
        return RushCore.getInstance().getId(this);
    }

}

