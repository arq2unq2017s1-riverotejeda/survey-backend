package unq.api.security;

import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unq.utils.EnvConfiguration;

import static spark.Spark.before;
import static spark.Spark.halt;

/**
 * Created by mrivero on 25/3/17.
 */
public class SecurityFilter implements SecurityHeaders{

    public static Logger LOGGER = LoggerFactory.getLogger(SecurityFilter.class);

    public static void initSecurityFilter(){
        LOGGER.info("Validating security headers");

        Config secureKey = EnvConfiguration.configuration.getConfig("database");

        before((request, response) -> {
            String appSecureKey = secureKey.getString(request.headers(X_APP_NAME));
            if(appSecureKey==null){
                halt(401, "secure headers are missing");
            }else if(!appSecureKey.equals(request.headers(X_SECURE_KEY))){
                halt(401, "secure headers are missing or wrong");
            }
        });
    }
}
