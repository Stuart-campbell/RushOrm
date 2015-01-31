package co.uk.friendlyapps.rushorm.demo;

import co.uk.rushorm.core.RushObject;
import co.uk.rushorm.core.annotations.RushTableAnnotation;

/**
 * Created by Stuart on 31/01/15.
 */
@RushTableAnnotation
public class Wheel extends RushObject {

    public Wheel(){
         /* Empty constructor required by SugarORM */
    }

    public String make;

    public Wheel(String make){
        this.make = make;
    }

}
