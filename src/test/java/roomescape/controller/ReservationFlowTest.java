package roomescape.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Sql(scripts = "/testReservationData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Import(FixedClockConfig.class)
class ReservationFlowTest extends AuthenticatedTest {

    @Test
    @DisplayName("예약 가능 시간 조회 → 예약 생성 → 다시 조회 시 빠짐")
    void reservationFlow() {
        given().log().all()
                .queryParam("date", "2026-04-28")
                .queryParam("themeId", 2L)
                .when().get("/times")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(3));

        Map<String, Object> params = new HashMap<>();
        params.put("userId", 2L);
        params.put("date", "2026-06-28");
        params.put("timeId", 1L);
        params.put("themeId", 2L);

        given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201);

        given().log().all()
                .queryParam("date", "2026-06-28")
                .queryParam("themeId", 2L)
                .when().get("/times")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(2));
    }
}
