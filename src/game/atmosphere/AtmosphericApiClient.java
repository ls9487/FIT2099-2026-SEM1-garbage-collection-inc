package game.atmosphere;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Handles the API call for the REQ5 toxic atmosphere feature.
 * <p>
 * This class is responsible for building a dynamic OpenWeather Air Pollution
 * API request based on the monitor's map position, reading the
 * OPENWEATHER_API_KEY environment variable, sending the HTTP request, and
 * returning the raw JSON response.
 * </p>
 * <p>
 * Keeping this logic in one class keeps network code separate from parsing and
 * corruption logic. It also makes the rest of the feature easier to test.
 * </p>
 *
 * @author esoo0013
 */
public class AtmosphericApiClient {

    /**
     * Representative city coordinates used as atmospheric profiles.
     */
    private static final double[][] CITY_COORDS = {
            {39.9042, 116.4074},  // Beijing
            {28.7041, 77.1025},   // Delhi
            {34.0522, -118.2437}, // Los Angeles
            {-6.2088, 106.8456},  // Jakarta
            {19.0760, 72.8777}    // Mumbai
    };

    private final HttpClient httpClient;

    /**
     * Constructor that creates an API client using the default HTTP client.
     */
    public AtmosphericApiClient() {
        this(HttpClient.newHttpClient());
    }

    /**
     * Constructor that accepts an HTTP client.
     *
     * @param httpClient the HTTP client to use for sending requests
     */
    public AtmosphericApiClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Builds a dynamic request, calls the OpenWeather Air Pollution API, and
     * returns the raw JSON response.
     *
     * If the API key is missing, blank, or the request fails, this method
     * returns an empty JSON object string so the game can fail softly.
     *
     * @param actor the actor performing the scan
     * @param map the map the actor is currently on
     * @return the raw JSON API response, or "{}" if the request cannot be completed
     */
    public String fetch(Actor actor, GameMap map) {
        String apiKey = System.getenv("OPENWEATHER_API_KEY");
        if (apiKey == null) {
            return "{}";
        }

        apiKey = apiKey.trim();
        if (apiKey.isEmpty()) {
            // Fail soft if the key is effectively blank so the game can still run.
            return "{}";
        }

        Location location = map.locationOf(actor);
        int x = location.x();
        int y = location.y();

        int index = Math.floorMod(x + y, CITY_COORDS.length);
        double lat = CITY_COORDS[index][0];
        double lon = CITY_COORDS[index][1];

        String url = String.format(
                "https://api.openweathermap.org/data/2.5/air_pollution?lat=%f&lon=%f&appid=%s",
                lat, lon, apiKey
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            return "{}";
        }
    }
}
