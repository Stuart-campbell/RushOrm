package co.uk.rushexample.testobjects;

import co.uk.rushorm.core.RushObject;
import co.uk.rushorm.core.annotations.RushTableAnnotation;

/**
 * Created by Stuart on 14/10/15.
 */

@RushTableAnnotation
public class Bug78 extends RushObject {

    public String field1 = "";
    public String field2 = "";

    @Override
    public int hashCode() {
        int result = field1.hashCode();
        result = 31 * result + field2.hashCode();

        return result;
    }
}
