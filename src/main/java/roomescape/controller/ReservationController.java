package roomescape.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import roomescape.auth.LoginUser;

import roomescape.domain.User;
import roomescape.dto.request.ReservationRequest;
import roomescape.dto.request.ReservationUpdateRequest;
import roomescape.dto.response.ReservationResponse;
import roomescape.service.ReservationCommandService;
import roomescape.service.ReservationQueryService;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationCommandService reservationCommandService;
    private final ReservationQueryService reservationQueryService;

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getMyReservations(@LoginUser User user) {
        List<ReservationResponse> responses = reservationQueryService.getByUserId(user.id())
                .stream()
                .map(ReservationResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@LoginUser User user,
                                                                  @Valid @RequestBody ReservationRequest request) {
        ReservationResponse reservationResponse = ReservationResponse.from(
                reservationCommandService.create(user.id(), request.date(), request.timeId(), request.themeId()));

        Long savedId = reservationResponse.id();

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedId)
                .toUri();

        return ResponseEntity.created(location).body(reservationResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelReservation(@LoginUser User user, @PathVariable long id) {
        reservationCommandService.cancel(id, user);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ReservationResponse> updateReservation(
            @LoginUser User user,
            @PathVariable long id,
            @Valid @RequestBody ReservationUpdateRequest request) {
        ReservationResponse response = ReservationResponse.from(
                reservationCommandService.update(id, request.date(), request.timeId(), user));
        return ResponseEntity.ok(response);
    }
}
