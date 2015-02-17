package co.uk.rushorm.core;

import java.util.List;

/**
 * Created by Stuart on 24/01/15.
 */
public abstract class RushObject implements Rush {

    @Override
    public void save() {
        RushCore.getInstance().save(this);
    }

    @Override
    public void save(RushCallback callback) {
        RushCore.getInstance().save(this, callback);
    }

    public List<RushConflict> saveOnlyWithoutConflict() {
        return RushCore.getInstance().saveOnlyWithoutConflict(this);
    }

    public void saveOnlyWithoutConflict(RushConflictCallback callback) {
        RushCore.getInstance().saveOnlyWithoutConflict(this, callback);
    }

    @Override
    public void delete() {
        RushCore.getInstance().delete(this);
    }

    @Override
    public void delete(RushCallback callback) {
        RushCore.getInstance().delete(this, callback);
    }

    @Override
    public String getId() {
        return RushCore.getInstance().getId(this);
    }

}
