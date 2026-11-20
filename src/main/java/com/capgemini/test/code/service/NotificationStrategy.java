package com.capgemini.test.code.service;

import com.capgemini.test.code.model.Role;
import com.capgemini.test.code.model.User;

public interface NotificationStrategy {
    Role getSupportedRole();
    void sendNotification(User user, String message);
}
