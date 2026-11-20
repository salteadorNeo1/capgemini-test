package com.capgemini.test.code.service;

import com.capgemini.test.code.clients.EmailRequest;
import com.capgemini.test.code.clients.NotificationClient;
import com.capgemini.test.code.model.Role;
import com.capgemini.test.code.model.User;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationStrategy implements NotificationStrategy {

    private final NotificationClient notificationClient;

    public EmailNotificationStrategy(NotificationClient notificationClient) {
        this.notificationClient = notificationClient;
    }

    @Override
    public Role getSupportedRole() {
        return Role.ADMIN;
    }

    @Override
    public void sendNotification(User user, String message) {
        notificationClient.sendEmail(new EmailRequest(user.getEmail(), message));
    }
}
