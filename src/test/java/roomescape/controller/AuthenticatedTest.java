package roomescape.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;

import java.util.Map;

public abstract class AuthenticatedTest {

    protected String sessionId;

    @BeforeEach
    void login() {
        sessionId = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.of("loginId", "user_a", "password", "1234"))
                .when().post("/login")
                .then().statusCode(200)
                .extract().sessionId();
    }

    protected RequestSpecification given() {
        return RestAssured.given().sessionId(sessionId);
    }
}
