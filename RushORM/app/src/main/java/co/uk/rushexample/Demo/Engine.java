package co.uk.rushexample.demo;

import co.uk.rushorm.core.RushObject;
import co.uk.rushorm.core.annotations.RushTableAnnotation;

/**
 * Created by Stuart on 31/01/15.
 */
@RushTableAnnotation
public class Engine extends RushObject {

    public Engine(){
         /* Empty constructor required by SugarORM */
    }

    public int cylinders;

    public Engine(int cylinders){
        this.cylinders = cylinders;
    }
}
