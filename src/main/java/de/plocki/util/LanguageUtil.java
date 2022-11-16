package de.plocki.util;

import de.plocki.util.files.FileBuilder;

public class LanguageUtil {

    public enum lang {
        DE, EN
    }

    public lang getUserLanguage(long id) {
        FileBuilder builder = new FileBuilder("languages.data");
        if(!builder.getYaml().isSet(id + "")) {
            builder.getYaml().set(id + "", lang.EN.name());
            builder.save();
            return lang.EN;
        } else {
            return lang.valueOf(builder.getYaml().getString(id + ""));
        }
    }

    public void setUserLanguage(long id, lang l) {
        FileBuilder builder = new FileBuilder("languages.data");
        builder.getYaml().set(id + "", l.name());
        builder.save();
    }

    public String getString(String id, lang l) {
        FileBuilder builder = new FileBuilder("languages.strings");
        return builder.getYaml().getString(l.name() + "." + id);
    }

    public void setString(String str, String id, lang l) {
        FileBuilder builder = new FileBuilder("languages.strings");
        if(!builder.getYaml().isSet(l.name() + "." + id)) {
            builder.getYaml().set(l.name() + "." + id, str);
            builder.save();
        }
    }

}
