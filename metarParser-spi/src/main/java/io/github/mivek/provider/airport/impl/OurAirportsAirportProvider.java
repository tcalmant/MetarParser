package io.github.mivek.provider.airport.impl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.math.NumberUtils;

import io.github.mivek.model.Airport;
import io.github.mivek.model.Country;
import io.github.mivek.provider.airport.AirportProvider;

/**
 * Implementation of the AirportProvider based on ourAirports.
 * To use this provider make sure you are connected to internet.
 *
 * @author mivek
 */
public final class OurAirportsAirportProvider implements AirportProvider {
    /** URI to retrieve the list of countries. */
    private static final String COUNTRIES_URI = "https://ourairports.com/data/countries.csv";
    /** URI to retrieve the list of airports. */
    private static final String AIRPORT_URI = "https://ourairports.com/data/airports.csv";
    /** Map of countries. */
    private final Map<String, Country> countries;
    /** Map of airports. */
    private final Map<String, Airport> airports;
    /** Common CSV format. */
    private final CSVFormat csvFormat;

    /**
     * Default constructor.
     *
     * @throws IOException            when network error
     * @throws URISyntaxException     when the URI is invalid
     */
    public OurAirportsAirportProvider() throws IOException, URISyntaxException, InterruptedException {
        countries = new HashMap<>();
        airports = new HashMap<>();
        csvFormat = CSVFormat.DEFAULT.withHeader();
        buildCountries();
        buildAirport();
    }

    /**
     * Connects to the countries list and build a map of {@link Country} with the name as key.
     *
     * @throws IOException            when network error
     * @throws URISyntaxException     when the URI is invalid
     */
    public void buildCountries() throws URISyntaxException, IOException, InterruptedException {
    	URL url = new URI(COUNTRIES_URI).toURL();
        
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    	conn.setConnectTimeout(5000);
        try (CSVParser reader = csvFormat.parse(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
        	for(CSVRecord record : reader) {
        		Country c = new Country();
        		c.setName(record.get("name"));
        		countries.put(record.get("code"), c);
        	}
        }
    }

    /**
     * Connects to the airports list and build a map of {@link Airport} with the name as key.
     *
     * @throws IOException            when network error
     * @throws URISyntaxException     when the URI is invalid
     */
    public void buildAirport() throws URISyntaxException, IOException, InterruptedException {
URL url = new URI(AIRPORT_URI).toURL();
        
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    	conn.setConnectTimeout(5000);
        
        try (CSVParser reader = csvFormat.parse(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
        	for(CSVRecord record : reader) {
                Airport airport = new Airport();
                airport.setIcao(record.get("ident"));
                airport.setName(record.get("name"));
                airport.setLatitude(NumberUtils.toDouble(record.get("latitude_deg"), 0));
                airport.setLongitude(NumberUtils.toDouble(record.get("longitude_deg"), 0));
                airport.setAltitude(NumberUtils.toInt(record.get("elevation_ft"), 0));
                airport.setCountry(countries.get(record.get("iso_country")));
                airport.setCity(record.get("municipality"));
                airport.setIata(record.get("iata_code"));
                airports.put(airport.getIcao(), airport);
            }
        }
    }

    @Override
    public Map<String, Airport> getAirports() {
        return airports;
    }
}

