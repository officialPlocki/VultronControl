package de.plocki.util.files;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Map;
import java.util.Objects;

public class YamlConfiguration extends FileConfiguration {
    // This is a constant that is used to denote a comment in the YAML file.
    protected static final String COMMENT_PREFIX = "# ";
    // This is a constant that is used to denote a blank configuration.
    protected static final String BLANK_CONFIG = "{}\n";
    // This is initializing the `DumperOptions` object.
    private final DumperOptions yamlOptions = new DumperOptions();
    // This is a class that is used to represent the configuration in a YAML format.
    private final Representer yamlRepresenter = new YamlRepresenter();
    // This is initializing the `Yaml` object.
    private final Yaml yaml = new Yaml(new YamlConstructor(), yamlRepresenter, yamlOptions);

    /**
     * This function is responsible for converting the configuration object into a string.
     *
     * @return The header and the dump of the values.
     */
    @Override
    public String saveToString() {
        yamlOptions.setIndent(options().indent());
        yamlOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        yamlOptions.setAllowUnicode(true);
        yamlRepresenter.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        String header = buildHeader();
        String dump = yaml.dump(getValues(false));

        if (dump.equals(BLANK_CONFIG)) {
            dump = "";
        }

        return header + dump;
    }

    /**
     * Loads the contents of a YAML file into a Section
     *
     * @param contents The contents of the file to load.
     */
    @Override
    public void loadFromString(String contents) {
        Objects.requireNonNull(contents, "Contents cannot be null");

        Map<?, ?> input = null;
        try {
            input = yaml.load(contents);
        } catch (Exception ignored) {}

        String header = parseHeader(contents);
        if (header.length() > 0) {
            options().header(header);
        }

        if (input != null) {
            convertMapsToSections(input, this);
        }
    }

    /**
     * Convert a map to a section
     *
     * @param input The map to convert.
     * @param section The section to add the new section to.
     */
    protected void convertMapsToSections(Map<?, ?> input, ConfigurationSection section) {
        for (Map.Entry<?, ?> entry : input.entrySet()) {
            String key = entry.getKey().toString();
            Object value = entry.getValue();

            if (value instanceof Map) {
                convertMapsToSections((Map<?, ?>) value, section.createSection(key));
            } else {
                section.set(key, value);
            }
        }
    }

    /**
     * This function parses the header of a file and returns the header as a string
     *
     * @param input The input string to be parsed.
     * @return The header.
     */
    protected String parseHeader(String input) {
        String[] lines = input.split("\r?\n", -1);
        StringBuilder result = new StringBuilder();
        boolean readingHeader = true;
        boolean foundHeader = false;

        for (int i = 0; (i < lines.length) && (readingHeader); i++) {
            String line = lines[i];

            if (line.startsWith(COMMENT_PREFIX)) {
                if (i > 0) {
                    result.append("\n");
                }

                if (line.length() > COMMENT_PREFIX.length()) {
                    result.append(line.substring(COMMENT_PREFIX.length()));
                }

                foundHeader = true;
            } else if ((foundHeader) && (line.length() == 0)) {
                result.append("\n");
            } else if (foundHeader) {
                readingHeader = false;
            }
        }

        return result.toString();
    }

    /**
     * If the header is set, it will be used. If the header is not set, but the copyHeader option is set, the header from
     * the defaults will be used. If the copyHeader option is not set, the header will be empty
     *
     * @return The header.
     */
    @Override
    public String buildHeader() {
        String header = options().header();

        if (options().copyHeader()) {
            Configuration def = getDefaults();

            if ((def instanceof FileConfiguration)) {
                FileConfiguration fileDefaults = (FileConfiguration) def;
                String defaultsHeader = fileDefaults.buildHeader();

                if ((defaultsHeader != null) && (defaultsHeader.length() > 0)) {
                    return defaultsHeader;
                }
            }
        }

        if (header == null) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        String[] lines = header.split("\r?\n", -1);
        boolean startedHeader = false;

        for (int i = lines.length - 1; i >= 0; i--) {
            builder.insert(0, "\n");

            if ((startedHeader) || (lines[i].length() != 0)) {
                builder.insert(0, lines[i]);
                builder.insert(0, COMMENT_PREFIX);
                startedHeader = true;
            }
        }

        return builder.toString();
    }

    /**
     * Returns the options for this configuration
     *
     * @return The options object.
     */
    @Override
    public YamlConfigurationOptions options() {
        if (options == null) {
            options = new YamlConfigurationOptions(this);
        }

        return (YamlConfigurationOptions) options;
    }

    /**
     * Loads a YAML file into a YamlConfiguration object
     *
     * @param file The file to load the configuration from.
     * @return The configuration object.
     */
    public static YamlConfiguration loadConfiguration(File file) {
        Objects.requireNonNull(file, "File cannot be null");

        YamlConfiguration config = new YamlConfiguration();

        try {
            config.load(file);
        } catch (Exception ignored) {

        }

        return config;
    }

    /**
     * Loads a YAML configuration file from a stream
     *
     * @param stream The InputStream to load the configuration from.
     * @return Nothing.
     */
    @Deprecated
    public static YamlConfiguration loadConfiguration(InputStream stream) {
        Objects.requireNonNull(stream, "Stream cannot be null");

        YamlConfiguration config = new YamlConfiguration();

        try {
            config.load(stream);
        } catch (Exception ignored) {

        }

        return config;
    }

    /**
     * Loads a YAML configuration file from a reader and returns a YamlConfiguration object
     *
     * @param reader The reader to read from.
     * @return The configuration object.
     */
    @SuppressWarnings("unused")
    public static YamlConfiguration loadConfiguration(Reader reader) throws Exception {
        Objects.requireNonNull(reader, "Stream cannot be null");

        YamlConfiguration config = new YamlConfiguration();

        try {
            config.load(reader);
        } catch (IOException ignored) {

        }

        return config;
    }
}
