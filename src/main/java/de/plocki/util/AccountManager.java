package de.plocki.util;

import com.mattmalec.pterodactyl4j.application.managers.UserAction;
import org.apache.commons.lang3.RandomStringUtils;

public class AccountManager {

    public String addAccount(String email, String first, String last, long id) {
        UserAction action = new Hooks().getPteroApplication().getUserManager().createUser();
        action.setEmail(email);
        action.setFirstName(first);
        action.setLastName(last);
        action.setUserName(id + "");
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~!@#$%^&()-_=+[{]}\\|;:\",<.>/?";
        String password = RandomStringUtils.random(32, chars);
        action.setPassword(password);
        try {
            action.execute();
        } catch (Exception ignored) {}
        return password;
    }

    public boolean hasAccount(long id) {
        return !new Hooks().getPteroApplication().retrieveUsersByUsername(id + "", true).execute().isEmpty();
    }

}
