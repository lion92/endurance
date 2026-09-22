package fr.endurance.workout;

import java.net.URI;

import fr.endurance.common.PageResponse;
import fr.endurance.user.CurrentUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {

    private final WorkoutService service;

    public WorkoutController(WorkoutService service) {
        this.service = service;
    }

    @GetMapping
    public PageResponse<WorkoutResponse> list(@AuthenticationPrincipal Jwt jwt,
                                              @RequestParam(defaultValue = "0") @Min(0) int page,
                                              @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return PageResponse.of(service.list(CurrentUser.idOf(jwt), PageRequest.of(page, size)),
                WorkoutResponse::from);
    }

    @PostMapping
    public ResponseEntity<WorkoutResponse> create(@AuthenticationPrincipal Jwt jwt,
                                                  @Valid @RequestBody WorkoutDetails details) {
        Workout workout = service.create(CurrentUser.idOf(jwt), details);
        return ResponseEntity.created(URI.create("/api/workouts/" + workout.getId()))
                .body(WorkoutResponse.from(workout));
    }

    @GetMapping("/{id}")
    public WorkoutResponse get(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return WorkoutResponse.from(service.get(CurrentUser.idOf(jwt), id));
    }

    @PutMapping("/{id}")
    public WorkoutResponse update(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id,
                                  @Valid @RequestBody WorkoutDetails details) {
        return WorkoutResponse.from(service.update(CurrentUser.idOf(jwt), id, details));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        service.delete(CurrentUser.idOf(jwt), id);
    }
}
