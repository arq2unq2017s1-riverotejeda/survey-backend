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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static unq.utils.EnvConfiguration.LOGGER;
import static utils.Utils.request;

public class DirectorControllerTest {

    private static String directorToken;
    private static ArrayList<Subject> subjectsToDelete = new ArrayList<>();
    private static String studentName = "MArcia";
    private static String legajo = "22222";

    @Before
    public void setUp() throws Exception {

    }
    @BeforeClass
    public static void beforeClass() {
        ApiService.main(null);
        directorToken = singUpDummyDirector();
    }


    @AfterClass
    public static void dropData(){
        dropData(directorToken, subjectsToDelete, legajo);
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
        Assert.assertFalse(subjectExists(subject,subs));
        Assert.assertEquals(res.status, 401);
    }


    //Estos tests solamente verifican que se puede accerder al endpoint sin seguridad y que la requeste devolverá estado 200 (OK)
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

        return res.body;
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

    private static void dropData(String directorToDelete, ArrayList<Subject> subjectsToDelete, String studentToDelete){
        MongoDBDAO mongoDAO = new MongoDBDAO();

        mongoDAO.deleteDirector(directorToDelete);
        for (int i = 0; i<subjectsToDelete.size(); i++){
            mongoDAO.deleteSubject(subjectsToDelete.get(i).getName(), subjectsToDelete.get(i).getSchoolYear());
        }
        mongoDAO.deleteStudent(studentToDelete);
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