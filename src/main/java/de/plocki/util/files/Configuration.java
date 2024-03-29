package de.plocki.util.files;

import java.util.Map;

@SuppressWarnings("unused")
public interface Configuration extends ConfigurationSection {
    void addDefault(String path, Object value);

    void addDefaults(Map<String, Object> defaults);

    void addDefaults(Configuration defaults);

    void setDefaults(Configuration defaults);

    Configuration getDefaults();

    ConfigurationOptions options();
}
