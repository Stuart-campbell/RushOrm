package co.uk.rushorm.android;

import java.util.ArrayList;
import java.util.List;

import co.uk.rushorm.core.RushQue;
import co.uk.rushorm.core.RushQueProvider;

/**
 * Created by stuartc on 11/12/14.
 */
public class AndroidRushQueProvider implements RushQueProvider {

    private List<RushQue> ques = new ArrayList<>();
    private final Object syncToken = new Object();

    public AndroidRushQueProvider() {
        ques.add(new AndroidRushQue());
    }

    @Override
    public RushQue blockForNextQue() {
        synchronized (syncToken) {
            while (ques.size() < 1) {
                    try {
                        syncToken.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

            }
            return ques.remove(0);
        }
    }

    @Override
    public void waitForNextQue(final RushQueCallback rushQueCallback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                rushQueCallback.callback(blockForNextQue());

            }
        }).start();
    }

    @Override
    public void queComplete(RushQue que) {
        synchronized (syncToken) {
            ques.add(que);
            syncToken.notify();
        }
    }
}
