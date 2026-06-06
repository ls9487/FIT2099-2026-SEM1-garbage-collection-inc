package game.atmosphere;

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
            {39.9042, 116.4074},
            {28.7041, 77.1025},
            {34.0522, -118.2437},
            {-6.2088, 106.8456},
            {19.0760, 72.8777}
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
     * @param map the map containing the atmospheric monitor
     * @param anchorLocation the current location of the atmospheric monitor
     * @return the raw JSON API response, or "{}" if the request cannot be completed
     */
    public String fetch(GameMap map, Location anchorLocation) {
        String apiKey = System.getenv("OPENWEATHER_API_KEY");
        if (apiKey == null) {
            return "{}";
        }

        apiKey = apiKey.trim();
        if (apiKey.isEmpty()) {
            return "{}";
        }

        if (anchorLocation == null) {
            return "{}";
        }

        int x = anchorLocation.x();
        int y = anchorLocation.y();

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