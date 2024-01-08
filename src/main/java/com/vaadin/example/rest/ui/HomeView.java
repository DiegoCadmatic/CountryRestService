package com.vaadin.example.rest.ui;

import backend.restprovider.CountryDetails;
import backend.restprovider.CountrySummary;
import com.vaadin.example.rest.data.RestClientService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

/**
 * Web application which utilizes the created REST API and shows the relevant country information in a browser.
 */
@PageTitle("Vaadin REST Examples")
@Route(value = "")
public class HomeView extends Main {

	public HomeView(@Autowired RestClientService service) {

		final Grid<CountrySummary> countriesGrid = new Grid<CountrySummary>(CountrySummary.class, false);
		countriesGrid.addColumn(CountrySummary::getName).setHeader("Country name");
		countriesGrid.addColumn(CountrySummary::getCountryCode).setHeader("Country code");

		List<CountrySummary> countrySummaries = service.getAllCountriesSummaries();
		GridListDataView<CountrySummary> dataView = countriesGrid.setItems(countrySummaries);

		countriesGrid.setItems(countrySummaries);

		TextField searchField = new TextField();
		searchField.setWidth("50%");
		searchField.setPlaceholder("Search");
		searchField.setValueChangeMode(ValueChangeMode.EAGER);
		searchField.addValueChangeListener(e -> dataView.refreshAll());

		TextField countryNameField = new TextField("Country Name");
		TextField countryCodeField = new TextField("Country code");
		TextField capitalField = new TextField("Capital");
		TextField populationField = new TextField("Population");
		Image flagImage = new Image("", "Country flag");

		dataView.addFilter(countrySummary -> {
			String searchTerm = searchField.getValue().trim();

			if (searchTerm.isEmpty())
				return true;

			return countrySummary.getName().toLowerCase().contains(searchTerm.toLowerCase()) ||
					countrySummary.getCountryCode().toLowerCase().contains(searchTerm.toLowerCase());
		});

		countriesGrid.addSelectionListener(selectionEvent -> {
			Optional<CountrySummary> countrySummary = selectionEvent.getFirstSelectedItem();
			if (countrySummary.isPresent()) {
				CountryDetails countryDetails = service.getCountryDetails(countrySummary.get().getName());
				countryNameField.setValue(countryDetails.getName());
				countryCodeField.setValue(countryDetails.getCountryCode());
				capitalField.setValue(countryDetails.getCapital());
				populationField.setValue(String.valueOf(countryDetails.getPopulation()));
				flagImage.setSrc(countryDetails.getFlagFileUrl());
			} else {
				countryNameField.clear();
				countryCodeField.clear();
				capitalField.clear();
				populationField.clear();
				flagImage.setSrc("");
			}
		});

		VerticalLayout div = new VerticalLayout();
		div.add(countryNameField);
		div.add(countryCodeField);
		div.add(capitalField);
		div.add(populationField);

		add(searchField, countriesGrid, div, flagImage);
	}
}
