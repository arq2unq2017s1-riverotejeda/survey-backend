package unq.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unq.api.exceptions.InvalidTokenException;
import unq.api.model.Student;
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
public class StudentController {

    private static SurveyService surveyService = new SurveyServiceImpl();
    public static Logger LOGGER = LoggerFactory.getLogger(StudentController.class);



    public static void initStudentEndpoints(){
        LOGGER.info("Student endpoints configured");
        /**
         * Get student information
         */
        get("/student/:legajo", (request, response) -> {
            response.type("application/json");
            Student student = surveyService.getStudentByID(request.params("legajo"));
            return GsonFactory.toJson(student);
        });

        /**
         * Get offer by year
         */
        get("/public/subjects/:year", (request, response) -> {
            response.type("application/json");
            return GsonFactory.toJson(surveyService.getAllSubjects(request.params("year")));
        });

        /**
         * Save or modify survey
         */
        post("/student/survey", (request, response) -> {
            response.type("application/json");
            try {
                Survey survey = GsonFactory.fromJson(request.body(), Survey.class);
                surveyService.saveSurvey(survey);
            } catch (InvalidTokenException i) {
                LOGGER.error("Invalid token error trying to save a survey", i);
                return HttpServletResponse.SC_NOT_ACCEPTABLE;

            } catch (Exception e) {
                LOGGER.error("Error while trying to save a survey", e);
                return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            }
            return HttpServletResponse.SC_OK;
        });

        /**
         * Get survey by student
         */
        get("/student/survey/:studentID/:year", (request, response) -> {
            response.type("application/json");
            return GsonFactory.toJson(surveyService.getSurveyByStudent(request.params("studentID") ,request.params("year")));
        });


        /* Get survey model by student. This was necessary for frontend app.
        get("/student/subjects/:token/:year", (request, response) -> {
            response.type("application/json");
            return GsonFactory.toJson(surveyService.getSurveyModel(request.params("token"), request.params("year")));
        });*/

    }
}
