package com.springboot.auth;

import com.springboot.job.dao.UserDAO;
import com.springboot.job.dto.LoginRequest;
import com.springboot.job.entity.User;
import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc; // Spring Boot 4
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = com.springboot.job.JobTrackerBackendApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {


  @Autowired MockMvc mvc;
  @Autowired ObjectMapper om;
  @Autowired UserDAO userDAO;

  private String json(String email, String password) throws Exception {
    return om.writeValueAsString(Map.of("email", email, "password", password));
  }

@BeforeEach
  void cleanDb() {
      userDAO.deleteAll();
  }

  @Test
  void register_success_user_saved_and_password_hashed() throws Exception {
    String email = "reg@test.com";
    String password = "Passw0rd!";

    mvc.perform(post("/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json(email, password)))
      .andExpect(status().isOk());

    User user = userDAO.findByEmail(email);
    assertThat(user).isNotNull();

    assertThat(user.getPassword()).isNotEqualTo(password);
    assertThat(user.getPassword()).startsWith("$2"); // BCrypt Hash beginnt meist mit $2a/$2b/$2y
  }

  @Test
  void register_duplicate_email_should_fail_and_not_create_second_user() throws Exception {
      String email = "dup@test.com";
      String password = "Passw0rd!";

     
      mvc.perform(post("/auth/register")
              .contentType(MediaType.APPLICATION_JSON)
              .content(json(email, password)))
          .andExpect(status().isOk());

      assertThat(userDAO.count()).isEqualTo(1);

     
      mvc.perform(post("/auth/register")
              .contentType(MediaType.APPLICATION_JSON)
              .content(json(email, password)))
          .andExpect(status().isConflict()); 

      assertThat(userDAO.count()).isEqualTo(1);
  }

  @Test
  void login_success_returns_jwt_string() throws Exception {
    String email = "login@test.com";
    String password = "Passw0rd!";

    // register
    mvc.perform(post("/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json(email, password)))
      .andExpect(status().isOk());

    // login
    String token = mvc.perform(post("/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json(email, password)))
      .andExpect(status().isOk())
      .andReturn()
      .getResponse()
      .getContentAsString();

    assertThat(token).isNotBlank();
    assertThat(token).isNotEqualTo("failed");
    // JWT Format: header.payload.signature
    assertThat(token.split("\\.")).hasSize(3);
  }

  @Test
  void login_wrong_password_should_fail() throws Exception {
    String email = "bad@test.com";

    mvc.perform(post("/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json(email, "Passw0rd!")))
      .andExpect(status().isOk());

    mvc.perform(post("/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json(email, "wrong")))
      .andExpect(status().is4xxClientError());
  }
}