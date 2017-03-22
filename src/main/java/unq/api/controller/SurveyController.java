package unq.api.controller;

import static spark.Spark.get;
import static spark.Spark.post;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import unq.api.exceptions.InvalidTokenException;
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

	private static SurveyService surveyService = new SurveyServiceImpl();

	public static void initSurveyEndopints() {

		get("/student/:legajo", (request, response) -> {
			response.type("application/json");
			Student student = surveyService.getStudentByID(request.params("legajo"));
			return GsonFactory.toJson(student);
		});

		post("/student", (request, response) -> {
			response.type("application/json");
			try {
				Student student = GsonFactory.fromJson(request.body(), Student.class);
				surveyService.saveStudent(student);
			} catch (Exception e) {
				LOGGER.error("Error while trying to save student", e);
				return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

			}
			return HttpServletResponse.SC_OK;
		});

		get("/subjects/:year", (request, response) -> {
			response.type("application/json");
			return GsonFactory.toJson(surveyService.getAllSubjects(request.params("year")));
		});

		get("/subjects/:token/:year", (request, response) -> {
			response.type("application/json");
			return GsonFactory.toJson(surveyService.getSurveyModel(request.params("token"), request.params("year")));
		});

		get("/subjectsOccupation/:year", (request, response) -> {
			response.type("application/json");
			return GsonFactory.toJson(surveyService.getClassOccupation(request.params("year")));
		});

		post("/subject", (request, response) -> {
			response.type("application/json");
			try {
				Subject subject = GsonFactory.fromJson(request.body(), Subject.class);
				surveyService.saveSubject(subject);
			} catch (Exception e) {
				LOGGER.error("Error while trying to save student", e);
				return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

			}
			return HttpServletResponse.SC_OK;
		});

		get("/survey/:studentID/:year", (request, response) -> {
			response.type("application/json");
			return GsonFactory.toJson(surveyService.getSurveyByStudent(request.params("studentID") ,request.params("year")));
		});

		post("/survey", (request, response) -> {
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

		get("/surveysCompletition/:year", (request, response) -> {
			response.type("application/json");
			return GsonFactory.toJson(surveyService.getSurveyStudentData(request.params("year")));
		});


		get("/getLastActiveYear", (request, response) -> {
			response.type("application/json");
			return GsonFactory.toJson(surveyService.getLastActiveYear());
		});

		get("/getAllYears", (request, response) -> {
			response.type("application/json");
			return GsonFactory.toJson(surveyService.getAllYears());
		});
	}
}
