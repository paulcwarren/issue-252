package com.example.issue252;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;

import javax.persistence.EntityManager;

import java.io.ByteArrayInputStream;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.*;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.config.EncoderConfig.encoderConfig;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Ginkgo4jSpringRunner.class)
@SpringBootTest(classes = {Issue252Application.class}, webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class Issue252ApplicationTests {

	@LocalServerPort
	int port;

	@Autowired
	private ApplicationContext context;

	@Autowired
	private DocumentRepository repo;

	@Autowired
	private DocumentStore store;

	private Document doc;

	private JsonPath json;

	{
		Describe("Issue 252", () -> {
			BeforeEach(() -> {
				RestAssured.port = port;
			});

			It("should work!", () -> {

				JsonPath json = given()
						.config(RestAssured.config()
								.encoderConfig(encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)))
						.header("content-type", "application/hal+json")
						.body("{}")
						.post("/documents/")
						.then()
						.statusCode(HttpStatus.SC_CREATED)
						.extract().jsonPath();

				String document1Uri = json.get("_links.self.href");

				given()
						.config(RestAssured.config()
								.encoderConfig(encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)))
						.header("content-type", "text/plain")
						.body("issue-252")
						.post(document1Uri)
						.then()
						.statusCode(HttpStatus.SC_CREATED);

				String strContent = given()
						.config(RestAssured.config()
							.encoderConfig(encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)))
						.header("accept", "text/plain")
						.get(document1Uri)
						.then()
						.statusCode(HttpStatus.SC_OK)
						.extract().body().asString();

				assertThat(strContent, is("issue-252"));
			});
		});
	}

	@Test
	public void contextLoads() {
	}
}
