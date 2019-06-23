package com.guilherme.recordphonecall;

/**
 * Created by dell on 27/01/2019.
 */
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class SMTPAuthenticator extends Authenticator {

    String username_;
    String password_;
    public SMTPAuthenticator(String username, String password ) {
        super();
        username_ = username;
        password_ = password;

    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {

        if ((username_ != null) && (username_.length() > 0) && (password_ != null)
                && (password_.length() > 0)) {

            return new PasswordAuthentication(username_, password_);
        }

        return null;
    }
}