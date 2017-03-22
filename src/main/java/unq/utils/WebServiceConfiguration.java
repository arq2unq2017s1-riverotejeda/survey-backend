package unq.utils;

import static spark.Spark.before;
import static spark.Spark.options;
import static spark.Spark.port;

import java.util.Optional;

/**
 * Created by mrivero on 21/9/16.
 */
public class WebServiceConfiguration {

	private static WebServiceConfiguration ourInstance = new WebServiceConfiguration();

	public static WebServiceConfiguration getInstance() {
		return ourInstance;
	}

	public void initConfiguration() {
		Integer port = Integer.valueOf(Optional.ofNullable(System.getenv().get("PORT"))
				.orElse(EnvConfiguration.configuration.getString("port")));
		port(port);

		// find a way to set a base url

		options("/*", (request, response) -> {

			String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
			if (accessControlRequestHeaders != null) {
				response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
			}

			String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
			if (accessControlRequestMethod != null) {
				response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
			}

			return "OK";
		});

		before((request, response) -> {
			response.header("Access-Control-Allow-Origin", "*");
		});

	}

}
