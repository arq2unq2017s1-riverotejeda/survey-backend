package unq.api.controller;

import static spark.Spark.get;
import static spark.Spark.post;

import javax.servlet.http.HttpServletResponse;

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

/**
 * Created by mrivero on 17/9/16.
 */
public class SurveyController {

	public static Logger LOGGER = LoggerFactory.getLogger(SurveyController.class);

	public static void initSurveyEndpoints() {
        LOGGER.info("Starting app endpoints");
        AppController.initEndpoints();
        StudentController.initStudentEndpoints();
        DirectorController.initDirectorEndpoints();
	}
}
