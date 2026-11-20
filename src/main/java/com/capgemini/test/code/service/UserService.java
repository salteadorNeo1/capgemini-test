package com.capgemini.test.code.service;

import com.capgemini.test.code.clients.CheckDniRequest;
import com.capgemini.test.code.clients.DniClient;
import com.capgemini.test.code.dto.CreateUserRequest;
import com.capgemini.test.code.dto.UserResponse;
import com.capgemini.test.code.exception.UserNotFoundException;
import com.capgemini.test.code.exception.ValidationException;
import com.capgemini.test.code.model.Role;
import com.capgemini.test.code.model.Room;
import com.capgemini.test.code.model.User;
import com.capgemini.test.code.repository.RoomRepository;
import com.capgemini.test.code.repository.UserRepository;
import feign.FeignException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final DniClient dniClient;
    private final Map<Role, NotificationStrategy> notificationStrategies;

    public UserService(
            UserRepository userRepository,
            RoomRepository roomRepository,
            DniClient dniClient,
            List<NotificationStrategy> strategies) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.dniClient = dniClient;
        this.notificationStrategies = strategies.stream()
                .collect(Collectors.toMap(NotificationStrategy::getSupportedRole, Function.identity()));
    }

    @Transactional
    public String createUser(Integer roomId, CreateUserRequest request) {
        validateBasicFields(request);

        Role role = Role.fromString(request.rol());

        if (userRepository.existsByEmail(request.email())) {
            throw new ValidationException("email");
        }
        if (userRepository.existsByDni(request.dni())) {
            throw new ValidationException("dni");
        }
        if (userRepository.existsByPhone(request.phone())) {
            throw new ValidationException("phone");
        }

        validateExternalDni(request.dni());

        Room room = roomRepository.findById(Long.valueOf(roomId))
                .orElseGet(() -> roomRepository.save(new Room("Sala " + roomId)));

        User user = new User(room, request.name(), request.email(), request.phone(), role, request.dni());
        User savedUser = userRepository.save(user);

        NotificationStrategy strategy = notificationStrategies.get(role);
        if (strategy != null) {
            strategy.sendNotification(savedUser, "usuario guardado");
        }

        return String.valueOf(savedUser.getId());
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Integer roomId, Long userId) {
        User user = userRepository.findByIdAndRoomId(userId, roomId)
                .orElseThrow(() -> new UserNotFoundException("User not found in room " + roomId));

        return new UserResponse(
                user.getId(),
                user.getRoom().getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole().getValue(),
                user.getDni()
        );
    }

    private void validateBasicFields(CreateUserRequest request) {
        if (request.name() == null || request.name().isBlank() || request.name().length() > 6) {
            throw new ValidationException("userName");
        }
        if (request.email() == null || !request.email().contains("@") || !request.email().contains(".")) {
            throw new ValidationException("email");
        }
        if (request.phone() == null || request.phone().isBlank()) {
            throw new ValidationException("phone");
        }
    }

    private void validateExternalDni(String dni) {
        if (dni == null || dni.isBlank()) {
            throw new ValidationException("dni");
        }
        try {
            dniClient.check(new CheckDniRequest(dni));
        } catch (FeignException.Conflict e) {
            throw new ValidationException("dni");
        }
    }
}
