package com.quickweather.service.email;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.*;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

@Slf4j
@Service
public class GmailQuickstart {

    private static final String APPLICATION_NAME = "QuickWeather";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_SEND);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private static final String EMAIL_SENDER = "lorenowiczbartlomiej@gmail.com";

    /**
     * Creates a Credential object for authorization.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        InputStream in = GmailQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens"))) // Tokeny będą zapisane tutaj!
                .setAccessType("offline") // MUSI być offline, aby uzyskać refresh_token
                .build();

        Credential credential = flow.loadCredential("user");

        if (credential != null) {
            log.info("Loaded existing credentials.");
            return credential;
        }

        log.info("No existing credentials found, starting new authorization.");
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver.Builder().setPort(8888).build()).authorize("user");
    }

    /**
     * Returns an authorized Gmail service instance.
     */
    public Gmail getGmailService() throws Exception {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Sends an email using Gmail API.
     */
    public void sendEmail(Gmail service, String to, String subject, String bodyText) throws Exception {
        log.info("Sending email to: {}", to);

        MimeMessage email = createEmail(to, EMAIL_SENDER, subject, bodyText);
        sendMessage(service, "me", email);
    }

    /**
     * Creates an email as a MimeMessage.
     */
    private MimeMessage createEmail(String to, String from, String subject, String bodyText)
            throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(from));
        email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    }

    /**
     * Sends a MimeMessage email using Gmail API.
     */
    private void sendMessage(Gmail service, String userId, MimeMessage email)
            throws IOException, MessagingException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.getUrlEncoder().encodeToString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        service.users().messages().send(userId, message).execute();
    }

    /**
     * Example usage to list labels.
     */
    public static void main(String... args) throws Exception {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Gmail service = new GmailQuickstart().getGmailService();

        String user = "me";
        ListLabelsResponse listResponse = service.users().labels().list(user).execute();
        List<Label> labels = listResponse.getLabels();
        if (labels.isEmpty()) {
            log.info("No labels found.");
        } else {
            log.info("Labels:");
            for (Label label : labels) {
                log.info("- %s\n", label.getName());
            }
        }
    }
}
