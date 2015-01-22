package co.uk.rushorm.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import co.uk.rushorm.core.annotations.RushTableAnnotation;

/**
 * Created by Stuart on 19/01/15.
 */
@RushTableAnnotation
public class RushTextFile extends RushFile {

    public RushTextFile(String directory) {
        super(directory);
    }

    public RushTextFile(){}

    @Override
    public String fileExtension() {
        return "txt";
    }

    public void setText(String text) throws IOException {
        writeToFile(text.getBytes());
    }



    public String getText() throws IOException {
        InputStream inputStream = readFormFile();

        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String receiveString;
            StringBuilder stringBuilder = new StringBuilder();

            while ((receiveString = bufferedReader.readLine()) != null) {
                stringBuilder.append(receiveString);
            }

            inputStream.close();
            return stringBuilder.toString();
        }
        return null;
    }
}
