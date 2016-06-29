package co.uk.rushorm.android.testobjects;

import co.uk.rushorm.core.RushObject;
import co.uk.rushorm.core.annotations.RushTableAnnotation;

/**
 * Created by Stuart on 12/02/15.
 */
@RushTableAnnotation
public class TestSelfReference extends RushObject {
    public TestSelfReference child;
}
