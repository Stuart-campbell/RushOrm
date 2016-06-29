package co.uk.rushorm.android.testobjects;

import co.uk.rushorm.core.RushObject;
import co.uk.rushorm.core.RushPageList;
import co.uk.rushorm.core.annotations.RushList;
import co.uk.rushorm.core.annotations.RushTableAnnotation;

/**
 * Created by Stuart on 12/04/15.
 */
@RushTableAnnotation
public class TestPageListObject extends RushObject {

    public String name;

    @RushList(classType = TestChildObject.class, listType = RushPageList.class)
    public RushPageList<TestChildObject> children;

}
