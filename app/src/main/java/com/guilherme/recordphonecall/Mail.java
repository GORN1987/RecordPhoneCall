package com.guilherme.recordphonecall;


import java.util.Date;
import java.util.Properties;
import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


/**
 * Created by dell on 27/01/2019.
 */



public class Mail extends javax.mail.Authenticator {

    private String _user;
    private String _pass;

    private String[] _to;
    private String _from;

    private String _port;
    private String _sport;

    private String _host;

    private String _subject;
    private String _body;

    private boolean _auth;

    private boolean _debuggable;

    private Multipart _multipart;

    private SMTPAuthenticator _authentication;

    javax.mail.Message _msg;

    Properties _props;

    public Mail() {
        _host = "smtp.gmail.com"; // default smtp server
        _port = "587"; // default smtp port
        _sport = "587"; // default socketfactory port

        _user = ""; // username
        _pass = ""; // password
        _from = ""; // email sent from
        _subject = ""; // email subject
        _body = ""; // email body

        _debuggable = false; // debug mode on or off - default off
        _auth = true; // smtp authentication - default on

        _multipart = new MimeMultipart();

// There is something wrong with MailCap, javamail can not find a handler for the multipart
// /mixed part, so this bit needs to be added.
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);
    }

    public Mail(String user, String pass,String host,String port ) {
        this();

        _user = user;
        _pass = pass;
        _host = host;
        _port = port;
         _props = _setProperties();
         _authentication = new SMTPAuthenticator(_user, _pass);
        _msg = new MimeMessage(Session
                .getDefaultInstance(_props, _authentication));

    }

    public void attachFile(String file_path, String file_name)
    {
        MimeBodyPart messageBodyPart2 = new MimeBodyPart();

        //Location of file to be attached
       // String filename = Environment.getExternalStorageDirectory().getPath()+"//01012019001941record.mp3";//change accordingly
        DataSource source = new FileDataSource(file_path );

        try {
            messageBodyPart2.setFileName(file_name);
            messageBodyPart2.setDataHandler(new DataHandler(source));
            _multipart.addBodyPart(messageBodyPart2);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

    public boolean send() throws Exception {
        //Properties props = _setProperties();
        final String pass = _pass;
        final String user = _user;

        if(!_user.equals("") && !_pass.equals("") && _to.length > 0 && !_from.equals("") &&
                !_subject.equals("") && !_body.equals("")) {

            Session session = Session.getInstance(_props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(user,pass);
                }
            });


            _msg.setFrom(new InternetAddress(_from));

            InternetAddress[] addressTo = new InternetAddress[_to.length];
            for (int i = 0; i < _to.length; i++) {
                addressTo[i] = new InternetAddress(_to[i]);
            }

            _msg.setRecipients(MimeMessage.RecipientType.TO, addressTo);

            _msg.setSubject(_subject);
            _msg.setSentDate(new Date());

// setup message body
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(_body);
            _multipart.addBodyPart(messageBodyPart);

// Put parts in message
            _msg.setContent(_multipart);

// send email
            String protocol = "smtp";
           _props.put("mail." + protocol + ".auth", "true");
            Transport t = session.getTransport(protocol);
            try {
                t.connect(_host,user,pass);
                t.sendMessage(_msg, _msg.getAllRecipients());
            } finally {
                t.close();
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(_user, _pass);
    }

    private Properties _setProperties() {
        Properties props = new Properties();

        props.put("mail.smtp.host", _host);

        if(_debuggable) {
            props.put("mail.debug", "true");
        }

        if(_auth) {
            props.put("mail.smtp.auth", "true");
        }

        props.put("mail.smtp.port", _port);
        props.put("mail.smtp.starttls.enable", "true");
        //props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.ssl.enable",true);

        return props;
    }

    // the getters and setters
    public String getBody() {
        return _body;
    }

    public void setBody(String _body) {
        this._body = _body;
    }
    public void setTo(String[] to) {
        this._to = to;
    }
    public void setFrom(String from) {
        this._from = from;
    }
    public void setSubject(String subject) {
        this._subject = subject;
    }

}
