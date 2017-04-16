package unq.api.security;

import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unq.api.service.SecurityService;
import unq.api.service.impl.SecurityServiceImpl;
import unq.utils.EnvConfiguration;

import static spark.Spark.before;
import static spark.Spark.halt;

/**
 * Created by mrivero on 25/3/17.
 */
public class SecurityFilter implements SecurityHeaders{

    public static Logger LOGGER = LoggerFactory.getLogger(SecurityFilter.class);
    private static SecurityService securityService = SecurityServiceImpl.getInstance();

    public static void initSecurityFilter(){
        LOGGER.info("Validating security headers");

        Config secureKey = EnvConfiguration.configuration.getConfig("secureKey");

        before((request, response) -> {
                String appSecureKey = secureKey.getString(request.headers(X_APP_NAME));
            if(appSecureKey==null){
                halt(401, "secure headers are missing");
            }else if(!appSecureKey.equals(request.headers(X_SECURE_KEY))){
                halt(401, "secure headers are missing or wrong");
            }
        });

        before("/director/*", (request, response) -> {
            String directorToken = request.headers(X_DIRECTOR_TOKEN);
            if(directorToken==null){
                halt(401, "secure director token header is missing");
            }
            String encryptedToken = HMACEncrypter.encrypt(directorToken, EnvConfiguration.configuration.getString("encryption-key"));
            String validToken = securityService.getDirectorToken(encryptedToken); // get director token to cache/mongo
            if(!encryptedToken.equals(validToken)){
                halt(401, "secure director token header is wrong");
            }
        });

        before("/student/*", (request, response) -> {
            String studentToken = request.headers(X_STUDENT_TOKEN);
            if(studentToken==null){
                halt(401, "secure student token header is missing");
            }
            String validToken = securityService.getStudentToken(studentToken); // get student token to cache/mongo
            if(!studentToken.equals(validToken)){
                halt(401, "secure student token header is wrong");
            }
        });
    }
}
