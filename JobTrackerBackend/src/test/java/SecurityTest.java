
import com.jayway.jsonpath.JsonPath;
import com.springboot.job.dao.ApplicationDAO;
import com.springboot.job.dao.UserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = com.springboot.job.JobTrackerBackendApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityTest {

	@Autowired
	MockMvc mvc;
	@Autowired
	UserDAO userDAO;
	@Autowired
	ApplicationDAO applicationDAO;

	@BeforeEach
	void cleanDb() {
		applicationDAO.deleteAll();
		userDAO.deleteAll();
	}

	private String loginJson(String email, String password) {
		return """
				  {"email":"%s","password":"%s"}
				""".formatted(email, password);
	}

	private String register(String email, String password) throws Exception {
		mvc.perform(post("/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(loginJson(email, password)))
				.andExpect(status().isOk());

		return email;
	}

	private String loginAndGetToken(String email, String password) throws Exception {
		return mvc.perform(post("/auth/login")
				  .contentType(MediaType.APPLICATION_JSON)
				  .content(loginJson(email, password)))
				  .andExpect(status().isOk())
				  .andReturn()
				  .getResponse()
				  .getContentAsString();
	}

	private String bearer(String token) {
		return "Bearer " + token;
	}

	private String createJobJson() {
		return """
				  {
				    "companyName":"ACME",
				    "jobTitle":"Junior Java Dev",
				    "location":"Germany",
				    "applicationDate":"2026-02-01",
				    "status":"APPLIED",
				    "priority":3,
				    "salary":50000,
				    "notes":"test",
				    "jobDescription":"desc"
				  }
				""";
	}

	private int createJobAndGetId(String token) throws Exception {
		String body = mvc.perform(post("/api/jobs/create")
				.header("Authorization", bearer(token))
				.contentType(MediaType.APPLICATION_JSON)
				.content(createJobJson()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").exists())
				.andReturn()
				.getResponse()
				.getContentAsString();

		return JsonPath.read(body, "$.id");
	}


	@Test
	void get_all_applications_without_token_should_be_unauthorized() throws Exception {
		mvc.perform(get("/api/jobs/applications")).andExpect(status().is4xxClientError());
	}

	@Test
	void userB_cannot_read_userA_application() throws Exception {
		
		register("a@test.com", "Passw0rd!");
		String tokenA = loginAndGetToken("a@test.com", "Passw0rd!");

		
		register("b@test.com", "Passw0rd!");
		String tokenB = loginAndGetToken("b@test.com", "Passw0rd!");

		int jobId = createJobAndGetId(tokenA);

		mvc.perform(get("/api/jobs/applications/" + jobId).header("Authorization", bearer(tokenB)))
			
				.andExpect(status().isNotFound());
	}


	@Test
	void userB_cannot_delete_userA_application() throws Exception {
		
		register("a2@test.com", "Passw0rd!");
		String tokenA = loginAndGetToken("a2@test.com", "Passw0rd!");

		
		register("b2@test.com", "Passw0rd!");
		String tokenB = loginAndGetToken("b2@test.com", "Passw0rd!");

		int jobId = createJobAndGetId(tokenA);

		
		mvc.perform(delete("/api/jobs/applications/" + jobId).header("Authorization", bearer(tokenB)))
				.andExpect(status().is4xxClientError());

		
		mvc.perform(get("/api/jobs/applications/" + jobId).header("Authorization", bearer(tokenA)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(jobId));
	}

}