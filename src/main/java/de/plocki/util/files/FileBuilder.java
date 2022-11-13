package de.plocki.util.files;

import java.io.File;
import java.io.IOException;

public class FileBuilder {

    // Creating a new File object and assigning it to the `file` variable.
    // Creating a new instance of the YamlConfiguration class, and then assigning it to the `yml` variable.
    private final File file;
    private final YamlConfiguration yml;

    public FileBuilder(String path) {
        file = new File(path);
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        yml = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * It returns the YamlConfiguration object that is used to store the configuration
     *
     * @return The yml object.
     */
    public YamlConfiguration getYaml() {
        return yml;
    }

    /**
     * Returns the file that was used to create the object
     *
     * @return The file object.
     */
    public File getFile() {
        return file;
    }

    /**
     * It saves the yml file
     */
    public void save() {
        try {
            yml.save(file);
        } catch (IOException ignored) {
        }
    }

}
