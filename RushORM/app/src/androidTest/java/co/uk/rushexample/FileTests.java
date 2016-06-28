package co.uk.rushexample;

import android.app.Application;
import android.graphics.Bitmap;
import android.test.ApplicationTestCase;

import org.json.JSONObject;

import co.uk.rushorm.android.RushBitmapFile;
import co.uk.rushorm.android.RushJSONFile;
import co.uk.rushorm.core.RushSearch;
import co.uk.rushorm.core.RushTextFile;

/**
 * Created by Stuart on 19/01/15.
 */
public class FileTests extends ApplicationTestCase<Application> {

    public FileTests() {
        super(Application.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        Utils.setUp(getContext());
    }

    public void testSaveText() throws Exception {

        RushTextFile file = new RushTextFile(getContext().getFilesDir().getAbsolutePath());
        file.setText("Hello world");
        String id = file.getId();

        RushTextFile loadedFile = new RushSearch().whereId(id).findSingle(RushTextFile.class);
        assertTrue(loadedFile.getText().equals("Hello world"));
        file.delete();
    }

    public void testDeleteText() throws Exception {

        RushTextFile file = new RushTextFile(getContext().getFilesDir().getAbsolutePath());
        file.setText("Hello world");
        String id = file.getId();

        RushTextFile loadedFile = new RushSearch().whereId(id).findSingle(RushTextFile.class);
        loadedFile.delete();

        assertFalse(loadedFile.file().exists());
    }

    public void testRushBitmapFile() throws Exception {

        int w = 500;
        int h = 500;

        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        RushBitmapFile file = new RushBitmapFile(getContext().getFilesDir().getAbsolutePath());
        file.setImage(bitmap);
        String id = file.getId();

        RushBitmapFile loadedFile = new RushSearch().whereId(id).findSingle(RushBitmapFile.class);

        assertNotNull(loadedFile.getImage());

        loadedFile.delete();
    }

    public void testRushJsonFile() throws Exception {

        JSONObject object = new JSONObject();
        object.put("String", "Hello world");

        RushJSONFile file = new RushJSONFile(getContext().getFilesDir().getAbsolutePath());
        file.setJSON(object);
        String id = file.getId();

        RushJSONFile loadedFile = new RushSearch().whereId(id).findSingle(RushJSONFile.class);

        assertEquals(loadedFile.getJSON().get("String"), "Hello world");

        loadedFile.delete();
    }
}
