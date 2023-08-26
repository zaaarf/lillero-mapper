package ftbsc.lll.mapper;

import ftbsc.lll.exceptions.InvalidResourceException;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The main class of the mapper library. It loads all the
 * valid {@link IMapper}s and gets information from them.
 */
public class MapperProvider {
	private static MapperProvider INSTANCE = null;

	private static MapperProvider getInstance() {
		return INSTANCE == null ? (INSTANCE = new MapperProvider()) : INSTANCE;
	}

	private Set<IMapper> loadedMappers = null;

	private void loadMappers() {
		this.loadedMappers = new HashSet<>();
		for(IMapper mapper: ServiceLoader.load(IMapper.class))
			this.loadedMappers.add(mapper);
		if(this.loadedMappers.isEmpty())
			throw new RuntimeException("Something went wrong: no mapper types were loaded successfully!");
	}

	/**
	 * Loads all valid parsers available in the classpath (via the Java Service API),
	 * attempts to load the resource at given location and to populate a mapper with
	 * its data.
	 * @param data the file as a list of strings
	 * @return a {@link IMapper} (populating it is left to the user)
	 */
	public static IMapper getMapper(List<String> data) {
		if(getInstance().loadedMappers == null)
			getInstance().loadMappers();
		return getInstance().loadedMappers.stream()
			.filter(m -> m.claim(data))
			.max(Comparator.comparingInt(IMapper::priority))
			.orElseThrow(InvalidResourceException::new);
	}

	/**
	 * Gets a resource and parses it into a {@link List} of {@link String}s.
	 * @param location either a URL or a local path
	 * @return a {@link List} containing the lines of the resource
	 * @throws InvalidResourceException if provided an invalid resource
	 */
	public static List<String> fetchFromLocalOrRemote(String location) {
		InputStream targetStream;
		try {
			URI target = new URI(location);
			targetStream = target.toURL().openStream();
		} catch(URISyntaxException | IOException e) {
			//may be a local file path
			File f = new File(location);
			try {
				targetStream = new FileInputStream(f);
			} catch(FileNotFoundException ex) {
				throw new InvalidResourceException(location);
			}
		}

		return new BufferedReader(new InputStreamReader(targetStream,
			StandardCharsets.UTF_8)).lines().collect(Collectors.toList());
	}
}