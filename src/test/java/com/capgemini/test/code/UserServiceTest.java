package com.capgemini.test.code;

import com.capgemini.test.code.clients.CheckDniRequest;
import com.capgemini.test.code.clients.DniClient;
import com.capgemini.test.code.dto.CreateUserRequest;
import com.capgemini.test.code.exception.ValidationException;
import com.capgemini.test.code.model.Role;
import com.capgemini.test.code.model.Room;
import com.capgemini.test.code.model.User;
import com.capgemini.test.code.repository.RoomRepository;
import com.capgemini.test.code.repository.UserRepository;
import com.capgemini.test.code.service.NotificationStrategy;
import com.capgemini.test.code.service.UserService;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RoomRepository roomRepository;
    @Mock private DniClient dniClient;
    @Mock private NotificationStrategy emailStrategy;
    @Mock private NotificationStrategy smsStrategy;

    private UserService userService;

    @BeforeEach
    void setUp() {
        when(emailStrategy.getSupportedRole()).thenReturn(Role.ADMIN);
        when(smsStrategy.getSupportedRole()).thenReturn(Role.SUPERADMIN);
        userService = new UserService(
                userRepository, roomRepository, dniClient, List.of(emailStrategy, smsStrategy)
        );
    }

    @Test
    void createUser_SuccessAdmin_SendsEmail() {
        CreateUserRequest req = new CreateUserRequest("pablo", "email@email.com", "677998899", "admin", "23454234W");
        Room room = new Room("Sala 1");
        User savedUser = new User(room, "pablo", "email@email.com", "677998899", Role.ADMIN, "23454234W");

        when(userRepository.existsByEmail(req.email())).thenReturn(false);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        userService.createUser(1, req);

        verify(emailStrategy).sendNotification(savedUser, "usuario guardado");
        verify(smsStrategy, never()).sendNotification(any(), any());
    }

    @Test
    void createUser_NameTooLong_ThrowsValidationException() {
        CreateUserRequest req = new CreateUserRequest("pablosky", "email@email.com", "677998899", "admin", "23454234W");

        ValidationException ex = assertThrows(ValidationException.class, () -> userService.createUser(1, req));
        assertEquals("error validation userName", ex.getFormattedMessage());
    }

    @Test
    void createUser_InvalidDni_ThrowsValidationException() {
        CreateUserRequest req = new CreateUserRequest("pablo", "email@email.com", "677998899", "admin", "99999999w");

        when(userRepository.existsByEmail(req.email())).thenReturn(false);
        doThrow(FeignException.Conflict.class).when(dniClient).check(any(CheckDniRequest.class));

        ValidationException ex = assertThrows(ValidationException.class, () -> userService.createUser(1, req));
        assertEquals("error validation dni", ex.getFormattedMessage());
    }
}
