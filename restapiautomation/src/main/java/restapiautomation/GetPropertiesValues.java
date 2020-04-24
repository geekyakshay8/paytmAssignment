package restapiautomation;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class GetPropertiesValues {

    public static Properties properties;


    public static void configFileReader() throws IOException {
        try {
            FileReader reader = new FileReader("./src/main/resources/config.properties");
            properties = new Properties();
            properties.load(reader);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}