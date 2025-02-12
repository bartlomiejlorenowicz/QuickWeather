package com.quickweather.config;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Configuration for Gmail integration (OAuth2 authorization).
 * <p>
 * Handles loading credentials from <code>credentials.json</code>
 * and manages the OAuth2 token (refreshes if it’s expired).
 * <p>
 */
@Configuration
public class GmailConfig {

    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList("https://www.googleapis.com/auth/gmail.send");
    private static final String USER_ID = "user";
    private static final int LOCAL_SERVER_PORT = 8888;

    private final JacksonFactory jacksonFactory = JacksonFactory.getDefaultInstance();

    @Bean
    public Credential gmailCredential(NetHttpTransport httpTransport) throws IOException {
        GoogleClientSecrets clientSecrets = loadClientSecrets();

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport,
                jacksonFactory,
                clientSecrets,
                SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        Credential credential = flow.loadCredential(USER_ID);
        if (credential != null && credential.getExpiresInSeconds() != null && credential.getExpiresInSeconds() <= 0) {
            credential.refreshToken();
        }

        if (credential == null) {
            credential = new AuthorizationCodeInstalledApp(
                    flow,
                    new LocalServerReceiver.Builder().setPort(LOCAL_SERVER_PORT).build()
            ).authorize(USER_ID);
        }
        return credential;
    }

    private GoogleClientSecrets loadClientSecrets() throws IOException {
        try (InputStream in = Objects.requireNonNull(
                getClass().getResourceAsStream(CREDENTIALS_FILE_PATH),
                "Resource not found: " + CREDENTIALS_FILE_PATH);
             InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            return GoogleClientSecrets.load(jacksonFactory, reader);
        }
    }

    @Bean
    public NetHttpTransport netHttpTransport() throws GeneralSecurityException, IOException {
        // Tworzy zaufany transport HTTP (zależność google-api-client)
        return GoogleNetHttpTransport.newTrustedTransport();
    }

}
