package unq.api.controller;

import com.google.gson.reflect.TypeToken;
import org.junit.*;
import spark.Spark;
import unq.api.model.Comision;
import unq.api.model.Director;
import unq.api.model.Division;
import unq.api.model.Subject;
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
import static utils.Utils.request;

public class DirectorControllerTest {

    private static String directorToken;
    private static ArrayList<Subject> subjectsToDelete = new ArrayList<>();

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
        dropData(directorToken, subjectsToDelete);
        Spark.stop();
    }

    @Test
    public void saveSubjectOKWithToken(){
        Subject subject = createSubject("TAdP", "201301");
        String subjectJson = GsonFactory.toJson(subject);
        Utils.TestResponse res1 = request("POST", "/director/subject", subjectJson, setUpSecurityHeaders(SecurityHeaders.X_DIRECTOR_TOKEN, directorToken));
        assertEquals(200, res1.status);
        Utils.TestResponse res = request("GET", "/public/subjects/"+subject.getSchoolYear(),new HashMap<>());
        Type listType = new TypeToken<List<SubjectOptions>>() {}.getType();
        List<SubjectOptions> subs = GsonFactory.fromJson(res.body, listType);
        subjectsToDelete.add(subject);
        Assert.assertTrue(subjectExists(subject, subs));
    }

    private boolean subjectExists(Subject s, List<SubjectOptions> subs) {
        List<String> names = subs.stream().map(sub->sub.getSubjectName()).collect(Collectors.toList());
        return names.contains(s.getName());
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

    private Subject saveSubject(String name, String directorToken){
        Subject subject = createSubject(name, "201301");
        String subjectJson = GsonFactory.toJson(subject);
        Utils.TestResponse res = request("POST", "/director/subject", subjectJson, setUpSecurityHeaders(SecurityHeaders.X_DIRECTOR_TOKEN, directorToken));
        //student.setAuthToken(res.body);
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
        subject.setGroup("avanzadas");
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

    private static void dropData(String directorToDelete, ArrayList<Subject> subjectsToDelete){
        MongoDBDAO mongoDAO = new MongoDBDAO();

        mongoDAO.deleteDirector(directorToDelete);
        for (int i = 0; i<subjectsToDelete.size(); i++){
            mongoDAO.deleteSubject(subjectsToDelete.get(i).getName(), subjectsToDelete.get(i).getSchoolYear());
        }
    }



}