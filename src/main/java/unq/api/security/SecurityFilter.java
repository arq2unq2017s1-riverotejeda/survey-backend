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

        /*after((request, response) -> {

            if(response.status()==200){
                LOGGER.info("Posting measure to Librato");
                client.postMeasures(new Measures().add(new GaugeMeasure("router.status.2xx", 1)));
            }
            if(response.status()==401){
                LOGGER.info("Posting measure to Librato");
                client.postMeasures(new Measures().add(new GaugeMeasure("router.status.4xx", 1)));
            }
            if(response.status()==500){
                LOGGER.info("Posting measure to Librato");
                client.postMeasures(new Measures().add(new GaugeMeasure("router.status.5xx", 1)));
            }
        });*/

        before((request, response) -> {
            try {
                String appSecureKey = secureKey.getString(request.headers(X_APP_NAME));
                if (appSecureKey == null) {
                   // client.postMeasures(new Measures().add(new GaugeMeasure("router.status.4xx", 1)));
                    halt(401, "secure headers are missing");
                } else if (!appSecureKey.equals(request.headers(X_SECURE_KEY))) {
                   // client.postMeasures(new Measures().add(new GaugeMeasure("router.status.4xx", 1)));
                    halt(401, "secure headers are missing or wrong");
                }
            }
            catch (Exception e){
                LOGGER.error("Error trayendo header", e);
               // return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            }
        });

        before("/director/*", (request, response) -> {
            String directorToken = request.headers(X_DIRECTOR_TOKEN);
            if(directorToken==null){
                //client.postMeasures(new Measures().add(new GaugeMeasure("router.status.4xx", 1)));
                halt(401, "secure director token header is missing");
            }
            String encryptedToken = HMACEncrypter.encrypt(directorToken, EnvConfiguration.configuration.getString("encryption-key"));
            String validToken = securityService.getDirectorToken(encryptedToken); // get director token to cache/mongo
            if(!encryptedToken.equals(validToken)){
               // client.postMeasures(new Measures().add(new GaugeMeasure("router.status.4xx", 1)));
                halt(401, "secure director token header is wrong");
            }
        });

        before("/student/*", (request, response) -> {
            String studentToken = request.headers(X_STUDENT_TOKEN);
            if(studentToken==null){
                //client.postMeasures(new Measures().add(new GaugeMeasure("router.status.4xx", 1)));
                halt(401, "secure student token header is missing");
            }
            String validToken = securityService.getStudentToken(studentToken); // get student token to cache/mongo
            if(!studentToken.equals(validToken)){
                //client.postMeasures(new Measures().add(new GaugeMeasure("router.status.4xx", 1)));
                halt(401, "secure student token header is wrong");
            }
        });
    }
}
