package com.quickweather.config;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Configuration
public class GmailQuickstartConfig {

    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList("https://www.googleapis.com/auth/gmail.send");

    public static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JacksonFactory.getDefaultInstance(),
                new InputStreamReader(Objects.requireNonNull(GmailQuickstartConfig.class.getResourceAsStream(CREDENTIALS_FILE_PATH)))
        );

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT,
                JacksonFactory.getDefaultInstance(),
                clientSecrets,
                SCOPES
        )
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        Credential credential = flow.loadCredential("user");

        // Jeśli token wygasł, odśwież go automatycznie
        if (credential != null && credential.getExpiresInSeconds() != null && credential.getExpiresInSeconds() <= 0) {
            credential.refreshToken();
        }

        return credential != null ? credential : new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver.Builder().setPort(8888).build()).authorize("user");
    }
}
