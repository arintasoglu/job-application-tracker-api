import com.jayway.jsonpath.JsonPath;
import com.springboot.job.dao.ApplicationDAO;
import com.springboot.job.dao.UserDAO;
import com.springboot.job.entity.Job;
import com.springboot.job.entity.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest(classes = com.springboot.job.JobTrackerBackendApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CoreTest {

	  @Autowired MockMvc mvc;
	  @Autowired UserDAO userDAO;
	  @Autowired ApplicationDAO applicationDAO;

	  @BeforeEach
	  void cleanDb() {
	    applicationDAO.deleteAll();
	    userDAO.deleteAll();
	  }

	
	  private String bearer(String token) {
	    return "Bearer " + token;
	  }

	  private String loginJson(String email, String password) {
	    return """
	      {"email":"%s","password":"%s"}
	    """.formatted(email, password);
	  }

	  private void register(String email, String password) throws Exception {
	    mvc.perform(post("/auth/register")
	        .contentType(MediaType.APPLICATION_JSON)
	        .content(loginJson(email, password)))
	      .andExpect(status().isOk());
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

	  private String createJobJson(String company, String status, int priority, String applicationDateIso) {
	    return """
	      {
	        "companyName":"%s",
	        "jobTitle":"Junior Java Dev",
	        "location":"Germany",
	        "applicationDate":"%s",
	        "status":"%s",
	        "priority":%d,
	        "salary":50000,
	        "notes":"test",
	        "jobDescription":"desc"
	      }
	    """.formatted(company, applicationDateIso, status, priority);
	  }

	  private int createJobAndGetId(String token, String company, String status, int priority, LocalDate appDate) throws Exception {
	    String body = mvc.perform(post("/api/jobs/create")
	        .header("Authorization", bearer(token))
	        .contentType(MediaType.APPLICATION_JSON)
	        .content(createJobJson(company, status, priority, appDate.toString())))
	      .andExpect(status().isOk())
	      .andExpect(jsonPath("$.id").exists())
	      .andReturn()
	      .getResponse()
	      .getContentAsString();

	    return JsonPath.read(body, "$.id");
	  }

	
	  @Test
	  void create_application_then_get_by_id_returns_same_values() throws Exception {
	    String email = "a@test.com";
	    String pw = "Passw0rd!";
	    register(email, pw);
	    String token = loginAndGetToken(email, pw);

	    int id = createJobAndGetId(token, "ACME", "APPLIED", 3, LocalDate.of(2026, 2, 1));

	    mvc.perform(get("/api/jobs/applications/" + id)
	       .header("Authorization", bearer(token)))
	       .andExpect(status().isOk())
	       .andExpect(jsonPath("$.id").value(id))
	       .andExpect(jsonPath("$.companyName").value("ACME"))
	       .andExpect(jsonPath("$.status").value("APPLIED"))
	       .andExpect(jsonPath("$.priority").value(3));
	  }

	  @Test
	  void update_status_sets_lastUpdated_and_creates_history_entry() throws Exception {
	    String email = "a2@test.com";
	    String pw = "Passw0rd!";
	    register(email, pw);
	    String token = loginAndGetToken(email, pw);

	    int id = createJobAndGetId(token, "Globex", "APPLIED", 2, LocalDate.of(2026, 2, 1));

	    
	    Job before = applicationDAO.findById(id).orElseThrow();
	    LocalDate lastBefore = before.getLastUpdated();

	    
	    mvc.perform(post("/api/jobs/applications/" + id + "/status")
	        .header("Authorization", bearer(token))
	        .contentType(MediaType.APPLICATION_JSON)
	        .content("\"INTERVIEWING\""))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$.status").value("INTERVIEWING"));

	   
	    Job after = applicationDAO.findById(id).orElseThrow();
	    LocalDate lastAfter = after.getLastUpdated();

	   
	    assertThat(lastAfter).isNotNull();
	    assertThat(lastAfter).isEqualTo(LocalDate.now());

	  
	    mvc.perform(get("/api/jobs/" + id + "/history")
	        .header("Authorization", bearer(token)))
	      .andExpect(status().isOk())
	      .andExpect(jsonPath("$").isArray())
	      // history enthält mindestens einen Eintrag
	      .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(1)));
	  }
	  @Test
	  void followups_returns_only_old_applied_applications() throws Exception {
	    String email = "follow@test.com";
	    String pw = "Passw0rd!";
	    register(email, pw);
	    String token = loginAndGetToken(email, pw);

	   
	    int oldId = createJobAndGetId(token, "OldCo", "APPLIED", 3, LocalDate.now().minusDays(20));

	   
	    int newId = createJobAndGetId(token, "NewCo", "APPLIED", 3, LocalDate.now().minusDays(3));

	    
	    createJobAndGetId(token, "InterviewCo", "INTERVIEWING", 3, LocalDate.now().minusDays(30));

	    String body = mvc.perform(get("/api/jobs/applications/followups")
	        .header("Authorization", bearer(token)))
	      .andExpect(status().isOk())
	      .andExpect(jsonPath("$").isArray())
	      .andReturn()
	      .getResponse()
	      .getContentAsString();

	  
	    int size = JsonPath.read(body, "$.length()");
	    assertThat(size).isGreaterThanOrEqualTo(1);

	   
	    List<String> companies = JsonPath.read(body, "$..companyName");
	    assertThat(companies).contains("OldCo");
	    assertThat(companies).doesNotContain("NewCo");
	  }

	  @Test
	  void stats_count_only_own_data() throws Exception {
	    
	    register("statsA@test.com", "Passw0rd!");
	    String tokenA = loginAndGetToken("statsA@test.com", "Passw0rd!");
	    createJobAndGetId(tokenA, "A1", "APPLIED", 2, LocalDate.now().minusDays(1));
	    createJobAndGetId(tokenA, "A2", "APPLIED", 2, LocalDate.now().minusDays(2));

	    register("statsB@test.com", "Passw0rd!");
	    String tokenB = loginAndGetToken("statsB@test.com", "Passw0rd!");
	    createJobAndGetId(tokenB, "B1", "APPLIED", 2, LocalDate.now().minusDays(1));

	    String statsAJson = mvc.perform(get("/api/jobs/stats")
	        .header("Authorization", bearer(tokenA)))
	      .andExpect(status().isOk())
	      .andExpect(jsonPath("$.totalApplications").exists())
	      .andReturn().getResponse().getContentAsString();

	    int totalA = JsonPath.read(statsAJson, "$.totalApplications");
	    assertThat(totalA).isEqualTo(2);

	 
	    String statsBJson = mvc.perform(get("/api/jobs/stats")
	        .header("Authorization", bearer(tokenB)))
	      .andExpect(status().isOk())
	      .andExpect(jsonPath("$.totalApplications").exists())
	      .andReturn().getResponse().getContentAsString();

	    int totalB = JsonPath.read(statsBJson, "$.totalApplications");
	    assertThat(totalB).isEqualTo(1);
	  }
	}

	