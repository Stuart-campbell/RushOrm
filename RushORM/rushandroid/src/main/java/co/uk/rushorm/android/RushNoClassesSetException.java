package co.uk.rushorm.android;

/**
 * Created by stuart on 26/06/2016.
 */

public class RushNoClassesSetException extends RuntimeException {

    public RushNoClassesSetException() {
        super("No classes set on AndroidInitializeConfig, add all classes you require to the AndroidInitializeConfig otherwise they will not be saved. See www.rushorm.com setup for more details.");
    }

}
