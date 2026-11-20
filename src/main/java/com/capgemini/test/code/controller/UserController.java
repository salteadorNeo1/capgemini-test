package com.capgemini.test.code.controller;

import com.capgemini.test.code.dto.CreateUserRequest;
import com.capgemini.test.code.dto.UserResponse;
import com.capgemini.test.code.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/rooms/{roomId}/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createUser(
            @PathVariable Long roomId,
            @RequestBody CreateUserRequest request) {
        String createdId = userService.createUser(Math.toIntExact(roomId), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("id", createdId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(
            @PathVariable Integer roomId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUser(roomId, userId));
    }
}
