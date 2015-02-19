package co.uk.rushorm.core.exceptions;

/**
 * Created by Stuart on 22/01/15.
 */
public class RushTableMissingEmptyConstructorException extends RuntimeException {

    public RushTableMissingEmptyConstructorException(Class clazz) {
        super(clazz.getName() + " Implement: " + clazz.getSimpleName() + "(){}");
    }
}
