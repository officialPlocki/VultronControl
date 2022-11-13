package de.plocki.util.files;

import java.util.Map;
import java.util.Objects;

public class MemoryConfiguration extends MemorySection implements Configuration {
    protected Configuration defaults;
    protected MemoryConfigurationOptions options;

    public MemoryConfiguration() {}

    @Override
    public void addDefault(String path, Object value) {
        Objects.requireNonNull(path, "Path may not be null");

        if (defaults == null) {
            defaults = new MemoryConfiguration();
        }

        defaults.set(path, value);
    }

    public void addDefaults(Map<String, Object> defaults) {
        Objects.requireNonNull(defaults, "Defaults may not be null");

        for (Map.Entry<String, Object> entry : defaults.entrySet()) {
            addDefault(entry.getKey(), entry.getValue());
        }
    }

    public void addDefaults(Configuration defaults) {
        Objects.requireNonNull(defaults, "Defaults may not be null");

        addDefaults(defaults.getValues(true));
    }

    public void setDefaults(Configuration defaults) {
        Objects.requireNonNull(defaults, "Defaults may not be null");

        this.defaults = defaults;
    }

    public Configuration getDefaults() {
        return defaults;
    }

    @Override
    public ConfigurationSection getParent() {
        return null;
    }

    public MemoryConfigurationOptions options() {
        if (options == null) {
            options = new MemoryConfigurationOptions(this);
        }

        return options;
    }
}