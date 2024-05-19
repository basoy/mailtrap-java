package org.example.mailtrapclient;

import java.util.List;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

public abstract class AbstractEmailSender {

    protected String senderName;
    protected String senderEmail;
    protected String recipientName;
    protected String recipientEmail;
    protected String subject;
    protected String text;
    protected String html;
    protected List<String> attachments;

    public AbstractEmailSender(
            String senderName, String senderEmail,
            String recipientName, String recipientEmail,
            String subject, String text, String html, List<String> attachments
    ) {
        if (senderEmail == null || recipientEmail == null) {
            throw new IllegalArgumentException("Sender and recipient email must not be null");
        }
        this.senderName = senderName;
        this.senderEmail = senderEmail;
        this.recipientName = recipientName;
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.text = text;
        this.html = html;
        this.attachments = attachments;
    }

    public AbstractEmailSender(
            String senderEmail,
            String recipientEmail,
            String text,
            String html
    ) {
        this(null, senderEmail, null, recipientEmail, null, text, html, null);
    }

    public Session getSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", "sandbox.smtp.mailtrap.io");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("fda4b210aee996", "9e2785e7291686");
            }
        };

        return Session.getInstance(props, authenticator);
    }

    public abstract void send(Session session);
}

