package unq.utils;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Created by mrivero on 22/10/16.
 */
public class EnvConfiguration {

	public static Logger LOGGER = LoggerFactory.getLogger(EnvConfiguration.class);

	public static Config configuration = loadConfiguration();

	private static Config loadConfiguration() {
		Config defaultConfig = ConfigFactory.load();

		String environment = Optional.ofNullable(System.getProperty("env")).orElse(defaultConfig.getString("env"));

		Config envConfiguration = defaultConfig.getConfig(environment).withFallback(defaultConfig);

		LOGGER.info("Configuration for env " + environment + " loads OK");

		return envConfiguration.resolve();
	}
}
