package co.uk.rushexample.demo;

import java.util.List;

import co.uk.rushorm.core.RushObject;
import co.uk.rushorm.core.annotations.RushList;
import co.uk.rushorm.core.annotations.RushTableAnnotation;

/**
 * Created by Stuart on 31/01/15.
 */
@RushTableAnnotation
public class Car extends RushObject {

    public String color;
    public Engine engine;

    @RushList(classType = Wheel.class)
    public List<Wheel> wheels;

    public Car(){
        /* Empty constructor required */
    }

    public Car(String color, Engine engine){
        this.color = color;
        this.engine = engine;
    }
}
