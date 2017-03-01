package co.uk.rushorm.android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import co.uk.rushorm.core.RushFile;

/**
 * Created by Stuart on 19/01/15.
 */
public class RushBitmapFile extends RushFile {

    public RushBitmapFile(String directory) {
        super(directory);
    }

    public RushBitmapFile(){}

    @Override
    public String fileExtension() {
        return "png";
    }

    public void setImage(Bitmap bitmap) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        writeToFile(stream.toByteArray());
    }

    public Bitmap getImage() throws IOException {
        return BitmapFactory.decodeStream(readFormFile());
    }
}
