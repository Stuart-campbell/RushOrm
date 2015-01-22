package co.uk.rushorm.android;

import android.database.DatabaseUtils;

import co.uk.rushorm.core.RushStringSanitizer;

/**
 * Created by Stuart on 15/12/14.
 */
public class AndroidRushStringSanitizer implements RushStringSanitizer {
    @Override
    public String sanitize(String string) {
        if(string != null) {
            return DatabaseUtils.sqlEscapeString(string);
        } else {
            return "'" + string + "'";
        }
    }
}
