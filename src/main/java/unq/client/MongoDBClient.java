package unq.client;

import java.net.UnknownHostException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.despegar.integration.mongo.connector.MongoCollectionFactory;
import com.despegar.integration.mongo.connector.MongoConnectorV2;
import com.despegar.integration.mongo.connector.MongoDBConnection;
import com.typesafe.config.Config;

import unq.utils.EnvConfiguration;

/**
 * Created by mrivero on 21/9/16.
 */
public class MongoDBClient {

	public static Logger LOGGER = LoggerFactory.getLogger(MongoDBClient.class);

	public static MongoCollectionFactory init() {
		Config databaseConfig = EnvConfiguration.configuration.getConfig("database");
		MongoDBConnection connection;
		try {
			Boolean enableAuth = Optional.ofNullable(databaseConfig.getBoolean("enableAuth")).orElse(Boolean.FALSE);
			if (enableAuth) {
				LOGGER.info("Using Auth to connect to MongoDB");

				connection = new MongoConnectorV2(databaseConfig.getString("name"),
						databaseConfig.getString("replicaSet"), databaseConfig.getString("credentials"));
			} else {
				LOGGER.info("Disable Auth to connect to MongoDB");

				connection = new MongoDBConnection(databaseConfig.getString("name"),
						databaseConfig.getString("replicaSet"));
			}

			LOGGER.info("Successfully connected to the database");
		} catch (UnknownHostException e) {
			LOGGER.error("Error trying to connect to MongoDB");
			throw new RuntimeException("Error trying to connect to Mongo", e);
		}

		MongoCollectionFactory mongoCollectionFactory = new MongoCollectionFactory(connection);
		return mongoCollectionFactory;
	}

}
