package com.capgemini.test.code;

import com.capgemini.test.code.dto.CreateUserRequest;
import com.capgemini.test.code.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserAcceptanceTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void endToEndFlow_CreateAndRetrieveUserWithRealMockServer() {
        // Generamos datos únicos en cada ejecución para no chocar con la BD real
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 5);
        CreateUserRequest request = new CreateUserRequest(
                "e2e",
                "e2e_" + uniqueSuffix + "@test.com",
                "6" + (int)(Math.random() * 89999999 + 10000000),
                "admin",
                "9" + (int)(Math.random() * 8999999 + 1000000) + "A"
        );

        // 1. Petición HTTP real POST contra nuestra API + MockServer (/check-dni y /email)
        ResponseEntity<Map> postResponse = restTemplate.postForEntity(
                "/rooms/1/users", request, Map.class
        );

        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());
        assertNotNull(postResponse.getBody());
        String createdId = postResponse.getBody().get("id").toString();

        // 2. Petición HTTP real GET para verificar persistencia
        ResponseEntity<UserResponse> getResponse = restTemplate.getForEntity(
                "/rooms/1/users/" + createdId, UserResponse.class
        );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals("e2e", getResponse.getBody().name());
        assertEquals("admin", getResponse.getBody().rol());
    }

    @Test
    void endToEndFlow_InvalidDniRejectedByRealMockServer() {
        CreateUserRequest request = new CreateUserRequest(
                "fail",
                "fail_" + UUID.randomUUID().toString().substring(0, 5) + "@test.com",
                "6" + (int)(Math.random() * 89999999 + 10000000),
                "superadmin",
                "99999999w" // DNI que el MockServer real rechaza con 409
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/rooms/1/users", request, Map.class
        );

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().get("code"));
        assertEquals("error validation dni", response.getBody().get("message"));
    }
}
