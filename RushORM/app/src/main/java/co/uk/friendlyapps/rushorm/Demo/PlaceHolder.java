package co.uk.friendlyapps.rushorm.demo;

import java.util.List;

import co.uk.rushorm.core.RushObject;
import co.uk.rushorm.core.annotations.RushList;
import co.uk.rushorm.core.annotations.RushTableAnnotation;

/**
 * Created by Stuart on 31/01/15.
 */
@RushTableAnnotation
public class PlaceHolder extends RushObject {

    @RushList(classname = "co.uk.friendlyapps.rushorm.demo.Car")
    public List<Car> cars;

}
