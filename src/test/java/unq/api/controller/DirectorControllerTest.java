package unq.api.controller;

import com.google.gson.reflect.TypeToken;
import com.sun.media.jfxmedia.logging.Logger;
import org.junit.*;
import spark.Spark;
import unq.api.model.*;
import unq.api.model.catalogs.SubjectOptions;
import unq.api.security.SecurityHeaders;
import unq.api.service.ApiService;
import unq.repository.MongoDBDAO;
import unq.utils.GsonFactory;
import utils.Utils;
import utils.Utils.TestResponse;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static unq.utils.EnvConfiguration.LOGGER;
import static utils.Utils.request;

public class DirectorControllerTest {

    private static String directorToken;
    private static ArrayList<Subject> subjectsToDelete = new ArrayList<>();
    private static ArrayList<Student> studentsToDelete = new ArrayList<>();
    private static String studentName = "MArcia";
    private static String legajo = "22222";
    private static String hashedDirectorToken;

    @Before
    public void setUp() throws Exception {

    }
    @BeforeClass
    public static void beforeClass() {
        ApiService.main(null);
        directorToken = singUpDummyDirector();
        //student = saveStudent(legajo,directorToken);
    }


    @AfterClass
    public static void dropData(){
        dropData(hashedDirectorToken, subjectsToDelete, studentsToDelete);
        Spark.stop();
    }

    @Test
    public void saveSubjectOKWithToken(){
        Subject subject = createSubject("TAdP", "201302");
        String subjectJson = GsonFactory.toJson(subject);
        Utils.TestResponse res1 = request("POST", "/director/subject", subjectJson, setUpSecurityHeaders(SecurityHeaders.X_DIRECTOR_TOKEN, directorToken));
        Assert.assertEquals(200, res1.status);
        Utils.TestResponse res = request("GET", "/public/subjects/"+subject.getSchoolYear(),new HashMap<>());
        Type listType = new TypeToken<List<SubjectOptions>>() {}.getType();
        List<SubjectOptions> subs = GsonFactory.fromJson(res.body, listType);
        subjectsToDelete.add(subject);
        Assert.assertTrue(subjectExists(subject, subs));
    }



   @Test
    public void saveSubjectWithoutToken(){
       Subject subject = createSubject("Arq", "201301");
        String subjectJson = GsonFactory.toJson(subject);
        Utils.TestResponse res = request("POST", "/director/subject", subjectJson, new HashMap<>());
        TestResponse res2 = request("GET", "/public/subjects/"+subject.getSchoolYear(),new HashMap<>());
        Type listType = new TypeToken<List<SubjectOptions>>() {}.getType();
        List<SubjectOptions> subs = GsonFactory.fromJson(res2.body, listType);
        Assert.assertFalse(subjectExists(subject,subs));
        Assert.assertEquals(res.status, 401);
    }


    @Test
    public void saveSubjectWithStudentToken(){
        Student stu = saveStudent(legajo, directorToken);
        Subject subject = createSubject("MAte3", "201501");
        String subjectJson = GsonFactory.toJson(subject);
        Utils.TestResponse res = request("POST", "/director/subject", subjectJson,
                setUpSecurityHeaders(SecurityHeaders.X_STUDENT_TOKEN, stu.getAuthToken()));
        TestResponse res2 = request("GET", "/public/subjects/"+subject.getSchoolYear(),new HashMap<>());
        Type listType = new TypeToken<List<SubjectOptions>>() {}.getType();
        List<SubjectOptions> subs = GsonFactory.fromJson(res2.body, listType);
        studentsToDelete.add(stu);
        Assert.assertFalse(subjectExists(subject,subs));
        Assert.assertEquals(res.status, 401);
    }


    //Los siguientes tests solamente verifican que se puede accerder al endpoint sin seguridad y que la requeste devolverá estado 200 (OK)
    //más allá de si hay info o no para mostrar

    @Test
    public void getAllSubjectsByYear(){
        TestResponse res = request("GET", "/public/subjects/201701",new HashMap<>());
        Assert.assertEquals(res.status, 200);
    }


    @Test
    public void getSurveysCompletnessWithToken(){
        Utils.TestResponse res = request("GET", "/director/surveysCompleteness/2017", setUpSecurityHeaders(SecurityHeaders.X_DIRECTOR_TOKEN, directorToken));
        Assert.assertEquals(200, res.status);
    }
    @Test
    public void getSurveysCompletnessWithOutToken(){
        Utils.TestResponse res = request("GET", "/director/surveysCompleteness/2017", new HashMap<>());
        Assert.assertEquals(401, res.status);
    }

    @Test
    public void getsubjectsOccupationWithToken(){
        Utils.TestResponse res = request("GET", "/director/subjectsOccupation/2017", setUpSecurityHeaders(SecurityHeaders.X_DIRECTOR_TOKEN, directorToken));
        Assert.assertEquals(200, res.status);
    }
    @Test
    public void getsubjectsOccupationWithOutToken(){
        Utils.TestResponse res = request("GET", "/director/subjectsOccupation/2017", new HashMap<>());
        Assert.assertEquals(401, res.status);
    }


    @Test
    public void getSurveyByStudentByYearWithToken(){
        Utils.TestResponse res = request("GET", "/director/survey/"+legajo+"/201701", setUpSecurityHeaders(SecurityHeaders.X_DIRECTOR_TOKEN, directorToken));
        Assert.assertEquals(200, res.status);
    }
    @Test
    public void getSurveyByStudentByYearWithOutToken(){
        Utils.TestResponse res = request("GET", "/director/survey/"+legajo+"/201701", new HashMap<>());
        Assert.assertEquals(401, res.status);
    }


    //TODO: ver cómo borrar la encuesta
    @Test
    public void saveAndGetSurveyByStudentByYear(){
        Student s = saveStudent("121212", directorToken);
        studentsToDelete.add(s);
        String year = "201402";
        //Guardo 5 materias para el semestre 201402
        Subject s1 = saveSubject("s1", year, directorToken);
        Subject s2 = saveSubject("s2", year, directorToken);
        Subject s3 = saveSubject("s3", year, directorToken);
        Subject s4 = saveSubject("s4", year, directorToken);
        Subject s5 = saveSubject("s5", year, directorToken);

        subjectsToDelete.add(s1);
        subjectsToDelete.add(s2);
        subjectsToDelete.add(s3);
        subjectsToDelete.add(s4);
        subjectsToDelete.add(s5);

        TestResponse res1 = request("GET", "/public/subjects/"+year,new HashMap<>());
        Type listType = new TypeToken<List<SubjectOptions>>() {}.getType();
        List<SubjectOptions> options = GsonFactory.fromJson(res1.body, listType);
        Survey survey = getSurvey("121212", year, studentName, s.getAuthToken(), options);

        TestResponse res2 = request("POST", "/student/survey", GsonFactory.toJson(survey),
                setUpSecurityHeaders(SecurityHeaders.X_STUDENT_TOKEN, s.getAuthToken()));

        //guardo OK la encuesta
        assertEquals(200, res2.status);


        //traigo la encuesta para ese semestre
        Utils.TestResponse res3 = request("GET", "/director/survey/"+s.getLegajo()+"/"+year,
                setUpSecurityHeaders(SecurityHeaders.X_DIRECTOR_TOKEN, directorToken));

        Survey surveyR = GsonFactory.fromJson(res3.body, Survey.class);
        int cantSubjects = surveyR.getSelectedSubjects().size();
        assertEquals(surveyR.getSchoolYear(), survey.getSchoolYear());
        assertEquals(cantSubjects, 5);
        assertEquals(surveyR.getLegajo(), s.getLegajo());
        deleteSuvey(s.getName(), year);
    }


    private static void deleteSuvey(String name, String schoolYear){
        MongoDBDAO mongoDAO = new MongoDBDAO();
        mongoDAO.deleteSurvey(name, schoolYear);
    }


    private Survey getSurvey(String legajo, String schoolYear, String studentName, String token, List<SubjectOptions> options) {
        Survey survey = new Survey();
        survey.setLegajo(legajo);
        survey.setSchoolYear(schoolYear);
        survey.setStudentName(studentName);
        survey.setToken(token);
        List<SelectedSubject> selected = new ArrayList<>();
        for(int i = 0; i<options.size(); i++){
            SelectedSubject s = new SelectedSubject();
            s.setStatus("APPROVED");
            s.setSubject(options.get(i).getSubjectName());
            selected.add(s);
        }
        survey.setSelectedSubjects(selected);
        return survey;
    }


    private Subject saveSubject(String name, String schoolYear, String directorToken){
        Subject subject = createSubject(name, schoolYear);
        String subjectJson = GsonFactory.toJson(subject);
        Utils.TestResponse res = request("POST", "/director/subject", subjectJson, setUpSecurityHeaders(SecurityHeaders.X_DIRECTOR_TOKEN, directorToken));
        subjectsToDelete.add(subject);
        return subject;
    }


    private Map<String, String> setUpSecurityHeaders(String headerName, String value){
        Map<String, String> items = new HashMap<>();
        items.put(headerName, value);
        return items;
    }

    private static String singUpDummyDirector(){
        Director director = new Director("Mar  ", "Te", "mt@unq.com", "quilmes2017");
        String directorJson = GsonFactory.toJson(director);

        Utils.TestResponse res = request("POST", "/private/director", directorJson, new HashMap<>());
        hashedDirectorToken = res.body;
        return "quilmes2017";
    }

    private Subject createSubject(String name, String schoolYear){
        Subject subject = new Subject();
        subject.setGroup("Avanzadas");
        subject.setName(name);
        subject.setSchoolYear(schoolYear);
        Division d = new Division();
        d.setComision(Comision.C1);
        d.setQuota(35L);
        List<String> wd = new ArrayList<>();
        wd.add("Lunes de 15 a 18");
        d.setWeekdays(wd);
        List<Division> divs = new ArrayList<>();
        divs.add(d);
        subject.setDivisions(divs);
        return subject;
    }

    private static void dropData(String directorToDelete, ArrayList<Subject> subjectsToDelete, ArrayList<Student> studentsToDelete){
        MongoDBDAO mongoDAO = new MongoDBDAO();

        mongoDAO.deleteDirector(directorToDelete);
        for (int i = 0; i<subjectsToDelete.size(); i++){
            mongoDAO.deleteSubject(subjectsToDelete.get(i).getName(), subjectsToDelete.get(i).getSchoolYear());
        }
        for (int i = 0; i<studentsToDelete.size(); i++){
            mongoDAO.deleteStudent(studentsToDelete.get(i).getLegajo());
        }


    }

    private boolean subjectExists(Subject s, List<SubjectOptions> subs) {
        List<String> names = subs.stream().map(sub->sub.getSubjectName()).collect(Collectors.toList());
        return names.contains(s.getName());
    }

    private Student saveStudent(String legajo, String directorToken){
        Student student = new Student();
        student.setEmail("mar@unq.com");
        student.setLegajo(legajo);
        student.setName(studentName);
        String studentJson = GsonFactory.toJson(student);
        Utils.TestResponse res = request("POST", "/director/student", studentJson, setUpSecurityHeaders(SecurityHeaders.X_DIRECTOR_TOKEN, directorToken));
        student.setAuthToken(res.body);
        return student;

    }

}