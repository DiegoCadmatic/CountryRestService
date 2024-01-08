package backend.restprovider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;

/**
 * Implements the following REST API with spring-boot:
 *      GET /countries/
 *      GET /countries/{name}
 *
 */
@RestController
@RequestMapping("/countries")
public class CountryController {

    @Autowired
    private RestTemplate restTemplate;

    private static final String COUNTRIES_API_URL = "https://restcountries.com/v2/";

    private static final Logger LOGGER = LoggerFactory.getLogger(CountryController.class);

    @GetMapping({"", "/"})
    public ResponseEntity<?> getCountries() {
        try {
            ResponseEntity<CountrySummary[]> responseEntity = restTemplate.getForEntity(
                    COUNTRIES_API_URL + "all?fields=name,alpha2Code",
                    CountrySummary[].class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok(Arrays.asList(responseEntity.getBody()));
            } else {
                // Handle errors here, return an empty list
                return ResponseEntity.status(responseEntity.getStatusCode()).body(Collections.emptyList());
            }
        } catch (RestClientException ex) {
            LOGGER.error("Error occurred while fetching countries: {}", ex.getMessage());
            // Handle RestClientException, return a suitable response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching countries.");
        }
    }

    @GetMapping({"/{name}", "/{name}/"})
    public ResponseEntity<?> getCountryDetails(@PathVariable String name) {
        try {
            ResponseEntity<CountryDetails[]> responseEntity = restTemplate.getForEntity(
                    COUNTRIES_API_URL + "name/" + name + "?fields=name,alpha2Code,capital,population,flag",
                    CountryDetails[].class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                CountryDetails[] countryDetails = responseEntity.getBody();
                if (countryDetails != null && countryDetails.length > 0) {
                    return ResponseEntity.ok(countryDetails[0]);
                }
            }

            // Handle the case when the country is not found or an error occurs
            return ResponseEntity.notFound().build();
        } catch (RestClientException ex) {
            LOGGER.error("Error occurred while fetching country details for {}: {}", name, ex.getMessage());
            // Handle RestClientException, log the error or return a suitable response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching country details.");
        }
    }
}
