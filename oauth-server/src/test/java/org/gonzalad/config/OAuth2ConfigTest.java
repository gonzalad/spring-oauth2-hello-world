package org.gonzalad.config;

import static com.jayway.restassured.RestAssured.*;
import static com.jayway.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;


import org.gonzalad.AuthorizationServerApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jayway.restassured.filter.session.SessionFilter;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { AuthorizationServerApplication.class })
@WebIntegrationTest
public class OAuth2ConfigTest {
	@Value("${local.server.port}")
	protected int port;

	@Test
	public void testOAuthServer() throws Exception {
		
		SessionFilter sessionFilter = new SessionFilter();
		
		String redirectUrl = "http://example.com";

		// Vérification que l'URL retournée est celle du formulaire d'autorisation (OAuth Approval)
		//	given : l'utilisateur a été redirigé par la webapp vers le serveur d'authentification auth qui lui demande
		//          si il  permet à la webapp d'effectuer les actions XXX.
		//  when : le fomulaire HTTP est retourné à l'utilisateur
		given()
			.accept(ContentType.HTML).auth().basic("user", "password")
			.filter(sessionFilter)
		.when()
			.get("http://localhost:9999/uaa/oauth/authorize?response_type=code&client_id=acme&redirect_uri=" + redirectUrl)
		.then()
			.assertThat().body(containsString("OAuth Approval"));

		// Vérification qu'un code OAuth est bien retourné après acceptation
		//	given : l'utilisateur accepte
		//  when : le serveur OAuth retourne un code OAuth à la webapp d'origine via un redirect
		Response response = given()
			.accept(ContentType.HTML).auth().basic("user", "password")
			.formParam("user_oauth_approval", "true")
			.formParam("scope.openid", "true")
		 	.formParam("authorize", "Authorize")
		 	.filter(sessionFilter)
		 .when()
			.post("http://localhost:9999/uaa/oauth/authorize");
		response
			.then().assertThat()
					.statusCode(302)
					.and().header("Location", containsString(redirectUrl + "?code="));
		String locationHeader = response.header("Location");
		String oauthCode = locationHeader.substring(locationHeader.lastIndexOf("code=") + "code=".length(), locationHeader.length());

		// Vérification qu'un code OAuth est bien retourné après acceptation
		//	given : la webapp a récupéré le code OAuth, elle appelle le serveur OAuth pour l'échanger contre un token OAuth
		//  when : le serveur OAuth lui retourne le token
		given()
			.accept(ContentType.JSON).auth().preemptive().basic("acme", "acmesecret")
			.formParam("grant_type", "authorization_code")
			.formParam("client_id", "acme")
		 	.formParam("redirect_uri", redirectUrl)
		 	.formParam("code", oauthCode)
	 	//.filter(sessionFilter)
		 .when()
			.post("http://localhost:9999/uaa/oauth/token")
		.then().assertThat()
				.statusCode(200)
				.and().body("access_token", not(isEmptyOrNullString()))
				.and().body("token_type", equalTo("bearer"))
				.and().body("refresh_token", not(isEmptyOrNullString()))
				.and().body("expires_in", not(isEmptyOrNullString()))
				.and().body("scope", equalTo("openid"));
		
//
		// final String dataSetId = createDataset("dataset/dataset.csv",
		// "testDataset", "text/csv");
		//
		// // when it's updated
		// given().body(IOUtils.toString(PreparationAPITest.class.getResourceAsStream("t-shirt_100.csv")))
		// .queryParam("Content-Type", "text/csv").when().put("/api/datasets/" +
		// dataSetId + "?name=testDataset")
		// .asString();
		//
		// // then, the content is updated
		// String dataSetContent = when().get("/api/datasets/" + dataSetId +
		// "?metadata=true").asString();
		// final String expectedContent = IOUtils
		// .toString(this.getClass().getResourceAsStream("t-shirt_100.csv.expected.json"));
		// assertThat(dataSetContent,
		// sameJSONAs(expectedContent).allowingExtraUnexpectedFields());
	}
}
