package fr.endurance.support;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;

import com.jayway.jsonpath.JsonPath;
import jakarta.servlet.http.Cookie;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

/** Enregistrer une séance par l'API, comme le ferait le site. */
public final class Workouts {

    private final MockMvcTester mvc;

    public Workouts(MockMvcTester mvc) {
        this.mvc = mvc;
    }

    public MvcTestResult create(Cookie athlete, String json) {
        return mvc.post().uri("/api/workouts").cookie(athlete).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(json)
                .exchange();
    }

    public MvcTestResult create(Cookie athlete, String sport, LocalDate date, int minutes, String km, int effort) {
        return create(athlete, json(sport, date, minutes, km, effort));
    }

    public static String json(String sport, LocalDate date, int minutes, String km, int effort) {
        return """
                {"sport": "%s", "date": "%s", "durationMinutes": %d, "distanceKm": %s,
                 "effort": %d, "notes": "séance de test"}
                """.formatted(sport, date, minutes, km, effort);
    }

    public static long idOf(MvcTestResult created) {
        try {
            return ((Number) JsonPath.read(created.getResponse().getContentAsString(), "$.id")).longValue();
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
}
