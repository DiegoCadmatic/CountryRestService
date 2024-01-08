package backend.restprovider;

import com.vaadin.example.rest.Application;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = Application.class)
public class CountryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    public void getCountries_ReturnsCountrySummaryList() throws Exception {
        // Mocking the response from the external API
        CountrySummary[] countrySummaries = {
                new CountrySummary("Country1", "CC1"),
                new CountrySummary("Country2", "CC2")
        };
        when(restTemplate.getForEntity("https://restcountries.com/v2/all?fields=name,alpha2Code", CountrySummary[].class))
                .thenReturn(ResponseEntity.ok(countrySummaries));

        // Perform GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/countries")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Country1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].country_code").value("CC1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("Country2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].country_code").value("CC2"));
    }

    @Test
    public void getCountries_ReturnsEmptyCountrySummaryList() throws Exception {
        // Mocking the response from the external API with an empty array
        CountrySummary[] emptyCountrySummaries = {};
        when(restTemplate.getForEntity("https://restcountries.com/v2/all?fields=name,alpha2Code", CountrySummary[].class))
                .thenReturn(ResponseEntity.ok(emptyCountrySummaries));

        // Perform GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/countries")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]"));
    }

    @Test
    void getCountryDetails_ReturnsCountryDetails() throws Exception {
        // Mocking the response from the external API for a successful case
        CountryDetails[] countryDetailsArray = {new CountryDetails("Country1", "CC1", "Capital1", 1000000, "Flag1")};
        ResponseEntity<CountryDetails[]> successfulResponseEntity = ResponseEntity.ok(countryDetailsArray);
        when(restTemplate.getForEntity("https://restcountries.com/v2/name/Country1?fields=name,alpha2Code,capital,population,flag", CountryDetails[].class))
                .thenReturn(successfulResponseEntity);

        // Perform GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/countries/Country1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Country1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.country_code").value("CC1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.capital").value("Capital1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.population").value(1000000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag_file_url").value("Flag1"));
    }

    @Test
    void getCountryDetails_ReturnsNotFound() throws Exception {
        // Mocking the response from the external API for a case when the country is not found
        ResponseEntity<CountryDetails[]> notFoundResponseEntity = ResponseEntity.notFound().build();
        when(restTemplate.getForEntity("https://restcountries.com/v2/name/NonExistingCountry?fields=name,alpha2Code,capital,population,flag", CountryDetails[].class))
                .thenReturn(notFoundResponseEntity);

        // Perform GET request for a non-existing country
        mockMvc.perform(MockMvcRequestBuilders.get("/countries/NonExistingCountry")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getCountryDetails_ReturnsInternalServerError() throws Exception {
        // Mocking the response from the external API for a case when an internal server error occurs
        when(restTemplate.getForEntity(anyString(), any())).thenThrow(new RestClientException("Internal Server Error"));

        // Perform GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/countries/AnyCountry")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string(""));
    }
}