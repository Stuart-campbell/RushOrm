package co.uk.rushorm.android;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import co.uk.rushorm.core.RushTextFile;

/**
 * Created by Stuart on 19/01/15.
 */
public class RushJSONFile extends RushTextFile {

    public RushJSONFile(String directory) {
        super(directory);
    }

    public RushJSONFile(){}

    @Override
    public String fileExtension() {
        return null;
    }

    public void setJSON(JSONObject json) throws IOException {
        setText(json.toString());
    }

    public JSONObject getJSON() throws IOException, JSONException {
        return new JSONObject(getText());
    }
}
