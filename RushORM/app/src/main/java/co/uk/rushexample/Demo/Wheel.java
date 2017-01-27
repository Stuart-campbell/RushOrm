package co.uk.rushexample.demo;

import co.uk.rushorm.core.RushObject;

/**
 * Created by Stuart on 31/01/15.
 */
public class Wheel extends RushObject {

    public Wheel(){
         /* Empty constructor required */
    }

    public String make;

    public Wheel(String make){
        this.make = make;
    }

}
