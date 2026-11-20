package com.capgemini.test.code;

import com.capgemini.test.code.clients.DniClient;
import com.capgemini.test.code.clients.NotificationClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DniClient dniClient;

    @MockBean
    private NotificationClient notificationClient;

    @Test
    void fullFlow_CreateAndGetUser_Returns201And200() throws Exception {
        String jsonRequest = """
            {
              "name": "testu",
              "email": "test.integration@test.com",
              "phone": "600998877",
              "rol": "admin",
              "dni": "55443322K"
            }
            """;

        MvcResult result = mockMvc.perform(post("/rooms/1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String createdId = jsonNode.get("id").asText();

        mockMvc.perform(get("/rooms/1/users/" + createdId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("testu"))
                .andExpect(jsonPath("$.rol").value("admin"));
    }

    @Test
    void createUser_InvalidDni_Returns409() throws Exception {
        doThrow(FeignException.Conflict.class).when(dniClient).check(any());

        String jsonRequest = """
            {
              "name": "juan",
              "email": "juan.test@test.com",
              "phone": "600112233",
              "rol": "superadmin",
              "dni": "99999999w"
            }
            """;

        mockMvc.perform(post("/rooms/1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.message").value("error validation dni"));
    }
}
