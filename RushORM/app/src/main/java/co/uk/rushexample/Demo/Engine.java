package co.uk.rushexample.demo;

import co.uk.rushorm.core.RushObject;

/**
 * Created by Stuart on 31/01/15.
 */
public class Engine extends RushObject {

    public Engine(){
         /* Empty constructor required */
    }

    public int cylinders;

    public Engine(int cylinders){
        this.cylinders = cylinders;
    }
}
