package co.uk.rushorm.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Stuart on 19/01/15.
 */
public abstract class RushFile extends RushObject {

    public abstract String fileExtension();

    private String directory;

    public RushFile(String directory) {
        this.directory = directory;
    }

    public RushFile(){

    }

    private String filename() {
        return directory + getId() + "." + fileExtension();
    }

    public File file() {
        return new File(filename());
    }

    public boolean hasFile() {
        return !(getId() == null) && file().exists();
    }

    public void writeToFile(byte[] bytes) throws IOException {

        if(getId() == null) {
            save();
        }

        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file());
            stream.write(bytes);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    public InputStream readFormFile() throws IOException {
        return new FileInputStream(filename());
    }

    @Override
    public void delete() {
        if(hasFile()) {
            file().delete();
        }
        super.delete();
    }

    @Override
    public void delete(RushCallback callback) {
        if(hasFile()) {
            file().delete();
        }
        super.delete(callback);
    }
}
