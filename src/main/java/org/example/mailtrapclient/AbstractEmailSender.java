package org.example.mailtrapclient;

import java.util.List;

public interface EmailSender {

    void send(String senderName, String senderEmail,
              String recipientName, String recipientEmail,
              String subject, String text, String html, List<String> attachments
    );
}

