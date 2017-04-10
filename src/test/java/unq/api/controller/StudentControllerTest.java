package unq.api.controller;

import org.junit.*;
import spark.Spark;
import unq.api.model.Director;
import unq.api.model.SelectedSubject;
import unq.api.model.Student;
import unq.api.model.Survey;
import unq.api.security.SecurityHeaders;
import unq.api.service.ApiService;
import unq.repository.MongoDBDAO;
import unq.utils.GsonFactory;
import utils.Utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static utils.Utils.request;

/**
 * Created by mar on 06/04/17.
 */
public class StudentControllerTest {

    private static String legajo = "11111";
    private static String schoolYear = "201401";
    private static String studentName = "Marina";
    private static String directorToken;
    private static Student studentToSave;



    @BeforeClass
    public static void beforeClass() {
        ApiService.main(null);
        directorToken = singUpDummyDirector();
        studentToSave = saveStudent(legajo, directorToken);

    }

    @AfterClass
    public static void dropData(){
        dropData(legajo, directorToken, studentName, schoolYear );
        Spark.stop();
    }

    @Test
    public void getStudent_ok(){
        Utils.TestResponse res = request("GET", "/student/" + legajo, setUpSecurityHeaders(SecurityHeaders.X_STUDENT_TOKEN, studentToSave.getAuthToken()));
        Student savedStudent = GsonFactory.fromJson(res.body, Student.class);
        assertEquals(200, res.status);
        assertEquals(studentToSave.getAuthToken(), savedStudent.getAuthToken());
    }

    @Test
    public void getStudent_noTokenValid_fail(){
        Utils.TestResponse res = request("GET", "/student/" + legajo, setUpSecurityHeaders(SecurityHeaders.X_STUDENT_TOKEN, "dummyToken"));
        assertEquals(401, res.status);
    }

    @Test
    public void saveSurvey_getSurvey_ok(){
        Survey defaultSurvey = getSurvey(studentToSave.getName(), studentToSave.getAuthToken());
        Utils.TestResponse res = request("POST", "/student/survey", GsonFactory.toJson(defaultSurvey), setUpSecurityHeaders(SecurityHeaders.X_STUDENT_TOKEN, studentToSave.getAuthToken()));

        assertEquals(200, res.status);

        Utils.TestResponse response = request("GET", "/student/survey/"+legajo+"/"+schoolYear, setUpSecurityHeaders(SecurityHeaders.X_STUDENT_TOKEN, studentToSave.getAuthToken()));

        assertEquals(200, response.status);
        Survey survey = GsonFactory.fromJson(response.body, Survey.class);
        assertEquals(defaultSurvey.getSchoolYear(), survey.getSchoolYear());
        assertEquals(defaultSurvey.getToken(), survey.getToken());

    }


    @Test
    public void getSurvey_modify_ok(){
        Utils.TestResponse response = request("GET", "/student/survey/"+legajo+"/"+schoolYear, setUpSecurityHeaders(SecurityHeaders.X_STUDENT_TOKEN, studentToSave.getAuthToken()));

        assertEquals(200, response.status);
        Survey survey = GsonFactory.fromJson(response.body, Survey.class);

        assertNotNull(survey);
        assertEquals(survey.getSelectedSubjects().size(), 0);

        SelectedSubject selectedSubject = new SelectedSubject();
        selectedSubject.setStatus("OK");
        selectedSubject.setSubject("MATH");
        survey.setSelectedSubjects(Arrays.asList(selectedSubject));

        response = request("POST", "/student/survey", GsonFactory.toJson(survey), setUpSecurityHeaders(SecurityHeaders.X_STUDENT_TOKEN, studentToSave.getAuthToken()));

        assertEquals(200, response.status);

        response = request("GET", "/student/survey/"+legajo+"/"+schoolYear, setUpSecurityHeaders(SecurityHeaders.X_STUDENT_TOKEN, studentToSave.getAuthToken()));

        Survey newSurvey = GsonFactory.fromJson(response.body, Survey.class);

        assertEquals(newSurvey.getSchoolYear(), survey.getSchoolYear());
        assertEquals(newSurvey.getToken(), survey.getToken());
        assertEquals(newSurvey.getSelectedSubjects().size(), 1);
    }

    @Test
    public void saveSurvey_getSurvey_noValidToken_ok(){
        Survey defaultSurvey = getSurvey("Dummy", "Dummy");
        Utils.TestResponse res = request("POST", "/student/survey", GsonFactory.toJson(defaultSurvey), setUpSecurityHeaders(SecurityHeaders.X_STUDENT_TOKEN, "Dummy"));

        assertEquals(401, res.status);

        Utils.TestResponse response = request("GET", "/student/survey/"+legajo+"/"+schoolYear, setUpSecurityHeaders(SecurityHeaders.X_STUDENT_TOKEN, studentToSave.getAuthToken()));

        assertEquals(200, response.status);
        Survey survey = GsonFactory.fromJson(response.body, Survey.class);
        assertNull(survey);
    }

    private Survey getSurvey(String studentName, String token) {
        Survey survey = new Survey();
        survey.setLegajo(legajo);
        survey.setSchoolYear(schoolYear);
        survey.setStudentName(studentName);
        survey.setToken(token);
        survey.setSelectedSubjects(Collections.emptyList());
        return survey;
    }

    private static Student saveStudent(String legajo, String directorToken){
        Student student = new Student();
        student.setEmail("marina@unq.com");
        student.setLegajo(legajo);
        student.setName(studentName);
        String studentJson = GsonFactory.toJson(student);
        Utils.TestResponse res = request("POST", "/director/student", studentJson, setUpSecurityHeaders(SecurityHeaders.X_DIRECTOR_TOKEN, directorToken));
        student.setAuthToken(res.body);
        return student;

    }

    private static String singUpDummyDirector(){
        Director director = new Director("Pato", "Cueck", "pato@unq.com", "unq2017");
        String directorJson = GsonFactory.toJson(director);

        Utils.TestResponse res = request("POST", "/private/director", directorJson, new HashMap<>());

        return res.body;
    }

    private static Map<String, String> setUpSecurityHeaders(String headerName, String value){
        Map<String, String> items = new HashMap<>();
        items.put(headerName, value);
        return items;
    }

    private static void dropData(String studentToDelete, String directorToDelete, String studentName, String schoolYear){
        MongoDBDAO mongoDAO = new MongoDBDAO();

        mongoDAO.deleteDirector(directorToDelete);
        mongoDAO.deleteStudent(studentToDelete);
        mongoDAO.deleteSurvey(studentName, schoolYear);
    }

}