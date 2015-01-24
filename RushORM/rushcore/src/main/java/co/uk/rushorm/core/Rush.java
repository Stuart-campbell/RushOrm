package co.uk.rushorm.core;

public interface Rush {

    public abstract void save();

    public abstract void save(RushCallback callback);

    public abstract void delete();

    public abstract void delete(RushCallback callback);

    public abstract long getId();

}

