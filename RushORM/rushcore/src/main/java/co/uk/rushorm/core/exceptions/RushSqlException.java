package co.uk.rushorm.core.exceptions;

/**
 * Created by Stuart on 15/02/15.
 */
public class RushSqlException extends RuntimeException {

    public RushSqlException() {
        super("This is most likely caused by a change in data structure or new RushObject.\n" +
                "This issue should be resolved by a database migration.\n" +
                "This can be done by updating db version number in the manifest or setting Rush in debug mode.\n" +
                "To set Rush in debug mode add this to your manifest file - <meta-data android:name=\"Rush_debug\" android:value=\"true\" />");
    }

}
