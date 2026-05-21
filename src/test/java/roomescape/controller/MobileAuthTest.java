package roomescape.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Map;

import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Sql(scripts = "/testReservationData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class MobileAuthTest {

    @Test
    @DisplayName("모바일 로그인에 성공하면 토큰을 반환한다.")
    void mobileLoginSuccess() {
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(Map.of("loginId", "user_a", "password", "1234"))
                .when().post("/mobile/login")
                .then().log().all()
                .statusCode(200)
                .body("token", notNullValue());
    }

    @Test
    @DisplayName("잘못된 비밀번호로 모바일 로그인하면 401을 반환한다.")
    void mobileLoginFail() {
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(Map.of("loginId", "user_a", "password", "wrong"))
                .when().post("/mobile/login")
                .then().log().all()
                .statusCode(401);
    }

    @Test
    @DisplayName("발급받은 토큰으로 인증이 필요한 API를 호출할 수 있다.")
    void accessWithToken() {
        String token = getToken("user_a", "1234");

        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + token)
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("토큰 없이 인증이 필요한 API를 호출하면 401을 반환한다.")
    void accessWithoutToken() {
        RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(401);
    }

    @Test
    @DisplayName("유효하지 않은 토큰으로 API를 호출하면 401을 반환한다.")
    void accessWithInvalidToken() {
        RestAssured.given().log().all()
                .header("Authorization", "Bearer invalid.token.value")
                .when().get("/reservations")
                .then().log().all()
                .statusCode(401);
    }

    @Test
    @DisplayName("ADMIN 토큰으로 관리자 API를 호출할 수 있다.")
    void adminAccessWithAdminToken() {
        String token = getToken("user_a", "1234"); // user_a는 ADMIN

        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + token)
                .when().get("/admin/reservations")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("USER 토큰으로 관리자 API를 호출하면 401을 반환한다.")
    void adminAccessWithUserToken() {
        String token = getToken("user_b", "1234"); // user_b는 USER

        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + token)
                .when().get("/admin/reservations")
                .then().log().all()
                .statusCode(401);
    }

    private String getToken(String loginId, String password) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.of("loginId", loginId, "password", password))
                .when().post("/mobile/login")
                .then().statusCode(200)
                .extract().path("token");
    }
}
