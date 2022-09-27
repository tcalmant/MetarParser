package io.github.mivek.command;

import static java.util.stream.StreamSupport.stream;

import java.util.ServiceLoader;

import io.github.mivek.model.Airport;
import io.github.mivek.provider.airport.AirportProvider;
import io.github.mivek.provider.airport.impl.DefaultAirportProvider;

/**
 * @author mivek
 */
public final class AirportSupplier implements Supplier<Airport> {
    /** The service loader for the airport provider. */
    private final ServiceLoader<AirportProvider> airportLoader;

    /**
     * Constructor.
     */
    public AirportSupplier() {
        airportLoader = ServiceLoader.load(AirportProvider.class);
    }

    @Override
    public Airport get(final String string) {
        AirportProvider provider = stream(airportLoader.spliterator(), false).findFirst().orElseGet(DefaultAirportProvider::new);
        return provider.getAirports().get(string);
    }
}

