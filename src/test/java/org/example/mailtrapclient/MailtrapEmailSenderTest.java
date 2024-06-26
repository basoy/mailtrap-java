package org.example.mailtrapclient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class MailtrapEmailSenderTest {

    private MailtrapEmailSender emailSender;

    @BeforeEach
    void setUp() {
        emailSender = new MailtrapEmailSender(
                "Sender Name", "sender@example.com",
                "Recipient Name", "recipient@example.com",
                "Subject", "Text content",
                "HTML content", Arrays.asList("path/to/attachment1", "path/to/attachment2")
        );
    }

    @Test
    void testGetSession() {
        Session session = emailSender.getSession();
        assertNotNull(session);
        Properties props = session.getProperties();
        assertEquals("sandbox.smtp.mailtrap.io", props.getProperty("mail.smtp.host"));
        assertEquals("587", props.getProperty("mail.smtp.port"));
        assertEquals("true", props.getProperty("mail.smtp.auth"));
        assertEquals("true", props.getProperty("mail.smtp.starttls.enable"));
        assertEquals("TLSv1.2", props.getProperty("mail.smtp.ssl.protocols"));
    }

    @Test
    void testSendEmail() throws MessagingException, IOException {
        Session session = spy(emailSender.getSession());
        Transport transport = mock(Transport.class);
        doNothing().when(transport).sendMessage(any(Message.class), any(Address[].class));
        doReturn(transport).when(session).getTransport(anyString());

        emailSender.send(session);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(transport).sendMessage(messageCaptor.capture(), any(Address[].class));
        Message sentMessage = messageCaptor.getValue();

        assertEquals("sender@example.com", ((InternetAddress) sentMessage.getFrom()[0]).getAddress());
        assertEquals("recipient@example.com", ((InternetAddress) sentMessage.getAllRecipients()[0]).getAddress());
        assertEquals("Subject", sentMessage.getSubject());

        Multipart multipart = (MimeMultipart) sentMessage.getContent();
        assertEquals(4, multipart.getCount());

        BodyPart textPart = multipart.getBodyPart(0);
        assertEquals("Text content", textPart.getContent().toString());

        BodyPart htmlPart = multipart.getBodyPart(1);
        assertEquals("text/html; charset=us-ascii", htmlPart.getContentType());
        assertEquals("HTML content", htmlPart.getContent().toString());

        BodyPart attachmentPart = multipart.getBodyPart(2);
        assertNotNull(attachmentPart.getFileName());
    }

    @Test
    void testSendEmailWithNoAttachments() throws MessagingException, IOException {
        emailSender = new MailtrapEmailSender(
                "Sender Name", "sender@example.com",
                "Recipient Name", "recipient@example.com",
                "Subject", "Text content",
                "HTML content", List.of()
        );

        Session session = spy(emailSender.getSession());
        Transport transport = mock(Transport.class);
        doNothing().when(transport).sendMessage(any(Message.class), any(Address[].class));
        doReturn(transport).when(session).getTransport(anyString());

        emailSender.send(session);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(transport).sendMessage(messageCaptor.capture(), any(Address[].class));
        Message sentMessage = messageCaptor.getValue();

        assertEquals("sender@example.com", ((InternetAddress) sentMessage.getFrom()[0]).getAddress());
        assertEquals("recipient@example.com", ((InternetAddress) sentMessage.getAllRecipients()[0]).getAddress());
        assertEquals("Subject", sentMessage.getSubject());

        Multipart multipart = (MimeMultipart) sentMessage.getContent();
        assertEquals(2, multipart.getCount());

        BodyPart textPart = multipart.getBodyPart(0);
        assertEquals("Text content", textPart.getContent().toString());

        BodyPart htmlPart = multipart.getBodyPart(1);
        assertEquals("text/html; charset=us-ascii", htmlPart.getContentType());
        assertEquals("HTML content", htmlPart.getContent().toString());
    }
}

