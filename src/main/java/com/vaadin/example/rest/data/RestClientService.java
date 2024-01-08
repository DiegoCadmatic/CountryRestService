package com.vaadin.example.rest.data;

import backend.restprovider.CountryController;
import backend.restprovider.CountryDetails;
import backend.restprovider.CountrySummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Example Spring service that connects to a REST API.
 */
@SuppressWarnings("serial")
@Service
public class RestClientService implements Serializable {

	/**
	 * The port changes depending on where we deploy the app
	 */
	@Value("${server.port}")
	private String serverPort;

	private static final Logger LOGGER = LoggerFactory.getLogger(CountryController.class);

	public List<CountrySummary> getAllCountriesSummaries() {

		LOGGER.info("Fetching all CountrySummary objects through REST...");

		// Other than that, this method is similar to #getAllComments().
		final String url = String.format("http://localhost:" + serverPort + "/countries");


		final RequestHeadersSpec<?> spec = WebClient.create().get().uri(url);
		final List<CountrySummary> countrySummaries = spec.retrieve().toEntityList(CountrySummary.class).block().getBody();

		if (Objects.isNull(countrySummaries)){
			return new ArrayList<>();
		}
		return countrySummaries;
	}

	public CountryDetails getCountryDetails(String countryName) {

		LOGGER.info("Fetching country details through REST..");

		final String url = String.format("http://localhost:" + serverPort + "/countries/" + countryName);

		final RequestHeadersSpec<?> spec = WebClient.create().get().uri(url);
        return spec.retrieve().toEntity(CountryDetails.class).block().getBody();
	}
}
