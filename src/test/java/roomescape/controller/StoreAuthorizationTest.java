package roomescape.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.util.Map;

import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Sql(scripts = "/testData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Import(FixedClockConfig.class)
public class StoreAuthorizationTest {

    @Test
    @DisplayName("매니저는 자기 매장 예약만 조회한다.")
    void managerListsOwnStoreReservations() {
        // manager_1: 강남점(테마 1~6)
        String token = getToken("manager_1", "1234");

        // testData.sql: 강남점 테마(1-6)에 29건, 홍대점(7-11)에 10건
        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + token)
                .when().get("/admin/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(29));
    }

    @Test
    @DisplayName("ADMIN은 모든 매장의 예약을 조회한다.")
    void adminListsAllReservations() {
        String token = getToken("user_a", "1234");

        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + token)
                .when().get("/admin/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(39));
    }

    @Test
    @DisplayName("매니저가 다른 매장의 예약을 삭제하면 403을 반환한다.")
    void managerCannotDeleteOtherStoreReservation() {
        // manager_2(홍대점)가 강남점의 예약(id=1, 테마1=강남점)을 삭제 시도
        String token = getToken("manager_2", "1234");

        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + token)
                .when().delete("/admin/reservations/1")
                .then().log().all()
                .statusCode(403);
    }

    @Test
    @DisplayName("매니저는 자기 매장 예약을 삭제할 수 있다.")
    void managerCanDeleteOwnStoreReservation() {
        // manager_1(강남점)이 강남점 예약(id=1) 삭제
        String token = getToken("manager_1", "1234");

        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + token)
                .when().delete("/admin/reservations/1")
                .then().log().all()
                .statusCode(204);
    }

    @Test
    @DisplayName("매니저는 테마 추가 권한이 없어 403을 반환한다.")
    void managerCannotCreateTheme() {
        String token = getToken("manager_1", "1234");

        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "name", "신규테마",
                        "thumbnailUrl", "url",
                        "description", "설명",
                        "storeId", 1
                ))
                .when().post("/admin/themes")
                .then().log().all()
                .statusCode(403);
    }

    @Test
    @DisplayName("매니저는 시간 추가 권한이 없어 403을 반환한다.")
    void managerCannotCreateTime() {
        String token = getToken("manager_1", "1234");

        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(Map.of("startAt", "17:00"))
                .when().post("/admin/times")
                .then().log().all()
                .statusCode(403);
    }

    @Test
    @DisplayName("일반 사용자가 다른 사용자의 예약을 취소하면 403을 반환한다.")
    void userCannotCancelOthersReservation() {
        // user_b가 user_a의 예약(id=1)을 취소 시도
        String token = getToken("user_b", "1234");

        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + token)
                .when().delete("/reservations/1")
                .then().log().all()
                .statusCode(403);
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
