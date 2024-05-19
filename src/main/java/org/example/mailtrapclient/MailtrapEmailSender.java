package org.example.mailtrapclient;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.List;

public class MailtrapEmailSender extends AbstractEmailSender {

    public MailtrapEmailSender(String senderName, String senderEmail,
                               String text, String html) {
        super(senderName, senderEmail, text, html);
    }

    public MailtrapEmailSender(String senderName, String mail, String recipientName, String mail1, String subject,
                               String textContent, String htmlContent, List<String> list) {
        super(senderName, mail, recipientName, mail1, subject, textContent, htmlContent, list);
    }


    @Override
    public void send(Session session) {
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject(subject);

            Multipart multipart = getMultipart();

            message.setContent(multipart);

            Transport.send(message);
        } catch (MessagingException | IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Multipart getMultipart() throws MessagingException, IOException {
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(text);

        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(html, "text/html");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);
        multipart.addBodyPart(htmlPart);

        if (attachments != null) {
            for (String attachment : attachments) {
                MimeBodyPart attachmentPart = new MimeBodyPart();
                attachmentPart.attachFile(attachment);
                multipart.addBodyPart(attachmentPart);
            }
        }
        return multipart;
    }
}
