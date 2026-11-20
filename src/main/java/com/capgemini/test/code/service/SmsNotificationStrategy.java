package com.capgemini.test.code.service;

import com.capgemini.test.code.clients.NotificationClient;
import com.capgemini.test.code.clients.SmsRequest;
import com.capgemini.test.code.model.Role;
import com.capgemini.test.code.model.User;
import org.springframework.stereotype.Component;

@Component
public class SmsNotificationStrategy implements NotificationStrategy {

    private final NotificationClient notificationClient;

    public SmsNotificationStrategy(NotificationClient notificationClient) {
        this.notificationClient = notificationClient;
    }

    @Override
    public Role getSupportedRole() {
        return Role.SUPERADMIN;
    }

    @Override
    public void sendNotification(User user, String message) {
        notificationClient.sendSms(new SmsRequest(user.getPhone(), message));
    }
}
