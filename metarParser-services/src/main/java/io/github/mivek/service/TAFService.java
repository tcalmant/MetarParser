package io.github.mivek.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.mivek.exception.ErrorCodes;
import io.github.mivek.exception.ParseException;
import io.github.mivek.model.TAF;
import io.github.mivek.parser.TAFParser;

/**
 * Facade for TAF.
 *
 * @author mivek
 */
public final class TAFService extends AbstractWeatherCodeService<TAF> {
    /** URL to retrieve the TAF from. */
    private static final String NOAA_TAF_URL = "https://tgftp.nws.noaa.gov/data/forecasts/taf/stations/";
    /** The instance of the service. */
    private static final TAFService INSTANCE = new TAFService();

    /**
     * Constructor.
     */
    private TAFService() {
        super(TAFParser.getInstance());
    }

    @Override
    public TAF decode(final String code) throws ParseException {
        return getParser().parse(code);
    }

    @Override
    public TAF retrieveFromAirport(final String icao) throws IOException, ParseException, URISyntaxException, InterruptedException {
        checkIcao(icao);
        String website = NOAA_TAF_URL + icao.toUpperCase()
                + ".TXT";
        
        StringBuilder sb = new StringBuilder();
        try(BufferedReader br = buildRequest(website)) {
        	// Throw the first line since it is not part of the TAF event.
        	br.lines().skip(1).forEach(currentLine -> sb.append(currentLine.replaceAll("\\s{2,}", "")).append("\n"));
        }
        
        return getParser().parse(format(sb.toString()));
    }

    /**
     * Reformat the first line of the code.
     *
     * @param code the first line of the TAF event.
     * @return the formated taf code.
     * @throws ParseException when an error occurs.
     */
    String format(final String code) throws ParseException {
        String[] lines = code.split("\n");
        if (!TAFParser.TAF.equals(lines[0].trim())) {
            return code;
        }
        if ("AMD TAF".equals(lines[1].trim())) {
            List<String> list = new ArrayList<>(Arrays.asList(lines));
            list.remove(1);
            lines = list.toArray(new String[0]);
        }
        // Case of TAF AMD, the 2 first lines must be merged.
        return Arrays.stream(lines).reduce((x, y) -> x + y + "\n").orElseThrow(() -> new ParseException(ErrorCodes.ERROR_CODE_INVALID_MESSAGE));
    }

    /**
     * @return the instance of the service.
     */
    public static TAFService getInstance() {
        return INSTANCE;
    }
}
