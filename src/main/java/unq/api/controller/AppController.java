package unq.api.controller;

import com.typesafe.config.ConfigFactory;
import unq.utils.GsonFactory;

import java.util.Optional;

import static spark.Spark.get;

/**
 * Created by mrivero on 23/4/17.
 */
public class AppController {

    public static void initEndpoints() {
        get("/private/version", (request, response) -> {
            response.type("application/json");
            return GsonFactory.toJson(resolveAppVersion());
        });
    }

    private static String resolveAppVersion(){
        return Optional.ofNullable(ConfigFactory.load().getString("version")).orElse("Not defined");

    }
}
