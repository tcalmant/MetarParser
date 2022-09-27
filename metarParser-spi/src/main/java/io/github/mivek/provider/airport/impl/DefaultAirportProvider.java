package io.github.mivek.provider.airport.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import io.github.mivek.model.Airport;
import io.github.mivek.model.Country;
import io.github.mivek.provider.airport.AirportProvider;

/**
 * Default implementation of the AiportProvider using local files to build the airport map.
 *
 * @author mivek
 */
public final class DefaultAirportProvider implements AirportProvider {
    /** Path of airport file. */
    private final InputStream airportsFile = DefaultAirportProvider.class.getClassLoader().getResourceAsStream("data/airports.dat");
    /** Path of countries file. */
    private final InputStream countriesFile = DefaultAirportProvider.class.getClassLoader().getResourceAsStream("data/countries.dat");
    /** Map of countries. */
    private Map<String, Country> countries;
    /** Map of airports. */
    private Map<String, Airport> airports;
    /** Common CSV format. */
    private final CSVFormat csvFormat;

    /**
     * Default constructor.
     */
    public DefaultAirportProvider() {
    	csvFormat = CSVFormat.DEFAULT;
        initCountries();
        initAirports();
    }

    /**
     * Initiate countries map.
     */
    private void initCountries() {
        Objects.requireNonNull(countriesFile);
        countries = new HashMap<>();
        try (CSVParser reader = csvFormat.parse(new InputStreamReader(countriesFile, StandardCharsets.UTF_8))) {
        	for(CSVRecord record : reader) {
        		Country country = new Country();
        		country.setName(record.get(0));
        		countries.put(country.getName(), country);
        	}
        } catch (IOException exception) {
            throw new IllegalStateException(exception.getMessage());
        }
    }

    /**
     * Initiate airports map.
     */
    private void initAirports() {
        Objects.requireNonNull(airportsFile);
        airports = new HashMap<>();
        try (CSVParser reader = csvFormat.parse(new InputStreamReader(airportsFile, StandardCharsets.UTF_8))) {
        	for(CSVRecord record : reader) {
                Airport airport = new Airport();
                airport.setName(record.get(1));
                airport.setCity(record.get(2));
                airport.setCountry(countries.get(record.get(3)));
                airport.setIata(record.get(4));
                airport.setIcao(record.get(5));
                airport.setLatitude(Double.parseDouble(record.get(6)));
                airport.setLongitude(Double.parseDouble(record.get(7)));
                airport.setAltitude(Integer.parseInt(record.get(8)));
                airport.setTimezone(record.get(9));
                airport.setDst(record.get(10));
                airports.put(airport.getIcao(), airport);
            }
        } catch (IOException exception) {
            throw new IllegalStateException(exception.getMessage());
        }
    }

    @Override
    public Map<String, Airport> getAirports() {
        return airports;
    }
}

