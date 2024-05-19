package org.example.mailtrapclient;

import java.util.Collections;

public class Main {
    public static void main(String[] args) {
        MailtrapEmailSender mailtrapSender = new MailtrapEmailSender(
                "mailtrap@demomailtrap.com",
                "basoy1988@example.com",
                "Simple text message.",
                "<b>Test HTML message</b>"
        );

        mailtrapSender.send(mailtrapSender.getSession());
    }
}