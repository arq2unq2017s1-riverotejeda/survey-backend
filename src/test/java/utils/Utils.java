package utils;

import com.google.gson.Gson;
import com.typesafe.config.Config;
import spark.utils.IOUtils;
import unq.api.controller.DirectorControllerTest;
import unq.utils.EnvConfiguration;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import static org.junit.Assert.fail;

/**
 * Created by mar on 06/04/17.
 */
public class Utils {



    //TODO: ver cómo acceder a los security headers
    public static TestResponse request(String method, String path) {
        String addr = EnvConfiguration.configuration.getString("appDomain");
        Config secureConfig = EnvConfiguration.configuration.getConfig("secureKey");
        String appName = secureConfig.getString("appName");
        String key = secureConfig.getString(appName);

        try {
            URL url = new URL(addr + path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setDoOutput(true);
            connection.setRequestProperty("X-App-Name", appName);
            connection.setRequestProperty("X-Secure-Key", key);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();
            String body = IOUtils.toString(connection.getInputStream());
            return new TestResponse(connection.getResponseCode(), body);
        } catch (IOException e) {
            e.printStackTrace();
            fail("Sending request failed: " + e.getMessage());
            return null;
        }
    }

    public static class TestResponse {

        public final String body;
        public final int status;

        public TestResponse(int status, String body) {
            this.status = status;
            this.body = body;
        }

        public Map<String,String> json() {
            return new Gson().fromJson(body, HashMap.class);
        }
    }

}
