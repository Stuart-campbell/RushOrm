package co.uk.rushorm.core;

/**
 * Created by Stuart on 10/12/14.
 */
public interface RushQueProvider {

    public interface RushQueCallback {
        public void callback(RushQue rushQue);
    }

    public RushQue blockForNextQue();
    public void waitForNextQue(RushQueCallback rushQueCallback);
    public void queComplete(RushQue que);
}
