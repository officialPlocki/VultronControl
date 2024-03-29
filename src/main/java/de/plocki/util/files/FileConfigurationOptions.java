package de.plocki.util.files;

public class FileConfigurationOptions extends MemoryConfigurationOptions {
    private String header = null;
    private boolean copyHeader = true;

    protected FileConfigurationOptions(FileConfiguration configuration) {
        super(configuration);
    }

    @Override
    public FileConfiguration configuration() {
        return (FileConfiguration) super.configuration();
    }

    @Override
    public void copyDefaults(boolean value) {
        super.copyDefaults(value);
    }

    @Override
    public void pathSeparator(char value) {
        super.pathSeparator(value);
    }

    /**
     * Gets the header that will be applied to the top of the saved output.
     * <p>
     * This header will be commented out and applied directly at the top of
     * the generated output of the {@link FileConfiguration}. It is not
     * required to include a newline at the end of the header as it will
     * automatically be applied, but you may include one if you wish for extra
     * spacing.
     * <p>
     * Null is a valid value which will indicate that no header is to be
     * applied. The default value is null.
     *
     * @return Header
     */
    public String header() {
        return header;
    }

    /**
     * Sets the header that will be applied to the top of the saved output.
     * <p>
     * This header will be commented out and applied directly at the top of
     * the generated output of the {@link FileConfiguration}. It is not
     * required to include a newline at the end of the header as it will
     * automatically be applied, but you may include one if you wish for extra
     * spacing.
     * <p>
     * Null is a valid value which will indicate that no header is to be
     * applied.
     *
     * @param value New header
     */
    public void header(String value) {
        this.header = value;
    }

    /**
     * Gets whether or not the header should be copied from a default source.
     * <p>
     * If this is true, if a default {@link FileConfiguration} is passed to
     *
     * then upon saving it will use the header from that config, instead of
     * the one provided here.
     * <p>
     * If no default is set on the configuration, or the default is not of
     * type FileConfiguration, or that config has no header ({@link #header()}
     * returns null) then the header specified in this configuration will be
     * used.
     * <p>
     * Defaults to true.
     *
     * @return Whether or not to copy the header
     */
    public boolean copyHeader() {
        return copyHeader;
    }

    /**
     * Sets whether or not the header should be copied from a default source.
     * <p>
     * If this is true, if a default {@link FileConfiguration} is passed to
     *
     * then upon saving it will use the header from that config, instead of
     * the one provided here.
     * <p>
     * If no default is set on the configuration, or the default is not of
     * type FileConfiguration, or that config has no header ({@link #header()}
     * returns null) then the header specified in this configuration will be
     * used.
     * <p>
     * Defaults to true.
     *
     * @param value Whether or not to copy the header
     */
    public void copyHeader(boolean value) {
        copyHeader = value;

    }
}