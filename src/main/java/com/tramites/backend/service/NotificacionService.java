package com.tramites.backend.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;

@Service
public class NotificacionService {

    private FirebaseMessaging messaging;

    @PostConstruct
    public void inicializar() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                FileInputStream serviceAccount = new FileInputStream(
                    System.getenv().getOrDefault("FIREBASE_CREDENTIALS_PATH",
                    "firebase-credentials.json"));
                FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
                FirebaseApp.initializeApp(options);
            }
            this.messaging = FirebaseMessaging.getInstance();
        } catch (Exception e) {
            System.out.println("Firebase no configurado: " + e.getMessage());
        }
    }

    public void enviarNotificacion(String fcmToken, String titulo, String cuerpo) {
        if (messaging == null || fcmToken == null || fcmToken.isEmpty()) return;
        try {
            Message mensaje = Message.builder()
                .setToken(fcmToken)
                .setNotification(Notification.builder()
                    .setTitle(titulo)
                    .setBody(cuerpo)
                    .build())
                .build();
            messaging.send(mensaje);
        } catch (Exception e) {
            System.out.println("Error enviando notificacion: " + e.getMessage());
        }
    }
}
