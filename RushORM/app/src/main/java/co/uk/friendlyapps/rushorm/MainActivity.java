package co.uk.friendlyapps.rushorm;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

import co.uk.rushorm.core.RushCallback;
import co.uk.friendlyapps.rushorm.TestObjects.TestChildObject;
import co.uk.friendlyapps.rushorm.TestObjects.TestObject;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(listener);
    }


    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            TestObject testObject = new TestObject();
            testObject.children = new ArrayList<>();
            for(int i = 0; i < 1000; i ++) {
                testObject.children.add(new TestChildObject());
            }

            view.setEnabled(false);
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

            testObject.save(callback);
        }
    };

    private RushCallback callback = new RushCallback() {
        @Override
        public void complete() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    findViewById(R.id.button).setEnabled(true);
                    findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
                }
            });
        }
    };

}
