package co.uk.rushorm.android;

/**
 * Created by stuart on 26/06/2016.
 */

public class RushPackageRootNotSetException extends RuntimeException {

    public RushPackageRootNotSetException() {
        super("No package root set. Please add this tag to your manifest <meta-data android:name=\"Rush_classes_package\" android:value=\"co.uk.rushexample\" /> " +
                "/n Alternatively add multiple packages by using the addPackageRoot on AndroidInitializeConfig.");
    }

}
