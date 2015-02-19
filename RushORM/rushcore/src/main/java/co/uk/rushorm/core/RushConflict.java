package co.uk.rushorm.core;

/**
 * Created by Stuart on 17/02/15.
 */
public class RushConflict<T extends Rush> {

    private final T inDataBase;
    private final T toBeSaved;

    public RushConflict(T inDataBase, T toBeSaved) {
        this.inDataBase = inDataBase;
        this.toBeSaved = toBeSaved;
    }

    public T getInDataBase(){
        return inDataBase;
    }

    public T getToBeSaved(){
        return toBeSaved;
    }

}
