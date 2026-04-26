package com.tramites.backend.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;

@Slf4j
@Service
public class NotificacionService {

    private FirebaseMessaging messaging;

    @PostConstruct
    public void inicializar() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                FileInputStream serviceAccount = new FileInputStream(
                    System.getenv().getOrDefault("FIREBASE_CREDENTIALS_PATH",
                    "src/main/resources/firebase-service-account.json"));
                FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
                FirebaseApp.initializeApp(options);
            }
            this.messaging = FirebaseMessaging.getInstance();
        } catch (Exception e) {
            log.error("Firebase no configurado: {}", e.getMessage());
        }
    }

    public void enviarNotificacion(String fcmToken, String titulo, String cuerpo) {
        log.info("FCM - token: {}, titulo: {}", fcmToken, titulo);
        if (messaging == null) {
            log.error("Firebase messaging es NULL - no inicializado");
            return;
        }
        if (fcmToken == null || fcmToken.isEmpty()) {
            log.warn("FCM token vacío para usuario");
            return;
        }
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
            log.error("Error enviando notificacion FCM: {}", e.getMessage());
        }
    }
}
