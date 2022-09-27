package io.github.mivek.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.stream.Collectors;

import io.github.mivek.exception.ParseException;
import io.github.mivek.model.Metar;
import io.github.mivek.parser.MetarParser;

/**
 * Class representing the service for metar.
 *
 * @author mivek
 */
public final class MetarService extends AbstractWeatherCodeService<Metar> {
    /** URL to retrieve the metar from. */
    private static final String NOAA_METAR_URL = "https://tgftp.nws.noaa.gov/data/observations/metar/stations/";
    /** Instance. */
    private static final MetarService INSTANCE = new MetarService();

    /**
     * Private constructor.
     */
    private MetarService() {
        super(MetarParser.getInstance());
    }

    @Override
    public Metar decode(final String code) throws ParseException {
        return getParser().parse(code);
    }

    @Override
    public Metar retrieveFromAirport(final String icao) throws ParseException, IOException, URISyntaxException, InterruptedException {
        checkIcao(icao);
        String website = NOAA_METAR_URL + icao.toUpperCase()
                + ".TXT";
       try(BufferedReader br = buildRequest(website)) {
    	   return getParser().parse(br.lines().skip(1).collect(Collectors.joining()));
       }
    }

    /**
     * Returns a instance of the class.
     *
     * @return the instance of the class.
     */
    public static MetarService getInstance() {
        return INSTANCE;
    }
}
