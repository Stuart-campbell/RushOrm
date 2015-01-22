package co.uk.rushorm.core.exceptions;

/**
 * Created by Stuart on 22/01/15.
 */
public class RushTableMissingEmptyConstructor extends RuntimeException {

    public RushTableMissingEmptyConstructor(Class clazz) {
        super(clazz.getName() + " Implement: " + clazz.getSimpleName() + "(){}");
    }
}
