package co.uk.rushorm.core;

/**
 * Created by Stuart on 24/01/15.
 */
public abstract class RushTable implements Rush {

    @Override
    public void save() {
        RushCore.getInstance().save(this);
    }

    @Override
    public void save(RushCallback callback) {
        RushCore.getInstance().save(this, callback);
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
    public long getId() {
        return RushCore.getInstance().getId(this);
    }

}
