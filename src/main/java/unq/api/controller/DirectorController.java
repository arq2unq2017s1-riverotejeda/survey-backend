package unq.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unq.api.exceptions.InvalidTokenException;
import unq.api.model.Director;
import unq.api.model.Student;
import unq.api.model.Subject;
import unq.api.model.Survey;
import unq.api.service.SurveyService;
import unq.api.service.impl.SurveyServiceImpl;
import unq.utils.GsonFactory;

import javax.servlet.http.HttpServletResponse;

import static spark.Spark.get;
import static spark.Spark.post;

/**
 * Created by mrivero on 2/4/17.
 */
public class DirectorController {

    private static SurveyService surveyService = new SurveyServiceImpl();
    public static Logger LOGGER = LoggerFactory.getLogger(DirectorController.class);


    public static void initDirectorEndpoints() {
        LOGGER.info("Director endpoints configured");

        /**
         * Sign up a student to the system
         */
        post("/director/student", (request, response) -> {
            response.type("application/json");
            String token = null;
            try {
                Student student = GsonFactory.fromJson(request.body(), Student.class);
                token = surveyService.saveStudent(student);
            } catch (Exception e) {
                LOGGER.error("Error while trying to save student", e);
                return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

            }
            return token;
        });

        /**
         * Get survey by student and year
         */
        get("/director/survey/:studentID/:year", (request, response) -> {
            response.type("application/json");
            return GsonFactory.toJson(surveyService.getSurveyByStudent(request.params("studentID") ,request.params("year")));
        });


        /**
         * Get subject occupation
         */
        get("/director/subjectsOccupation/:year", (request, response) -> {
            response.type("application/json");
            return GsonFactory.toJson(surveyService.getClassOccupation(request.params("year")));
        });

        /**
         * Save subject to an offer
         */
        post("/director/subject", (request, response) -> {
            response.type("application/json");
            try {
                Subject subject = GsonFactory.fromJson(request.body(), Subject.class);
                surveyService.saveSubject(subject);
            } catch (Exception e) {
                LOGGER.error("Error while trying to save subject", e);
                return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

            }
            return HttpServletResponse.SC_OK;
        });

        /**
         * Get completeness by year
         */
        get("/director/surveysCompleteness/:year", (request, response) -> {
            response.type("application/json");
            return GsonFactory.toJson(surveyService.getSurveyStudentData(request.params("year")));
        });

        /**
         * Sign up a director
         */
        post("/private/director", (request, response) -> {
            response.type("application/json");
            String token = null;
            try {
                Director director = GsonFactory.fromJson(request.body(), Director.class);
                token = surveyService.saveDirector(director);
            } catch (Exception e) {
                LOGGER.error("Error while trying to save director", e);
                return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

            }
            return token;
        });


        /* Frontend endpoints -- not necessary
        get("/getLastActiveYear", (request, response) -> {
            response.type("application/json");
            return GsonFactory.toJson(surveyService.getLastActiveYear());
        });

        get("/getAllYears", (request, response) -> {
            response.type("application/json");
            return GsonFactory.toJson(surveyService.getAllYears());
        });
        */

    }
}
