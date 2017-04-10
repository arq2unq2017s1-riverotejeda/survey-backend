package unq.api.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.*;
import spark.Spark;
import sun.security.x509.SubjectKeyIdentifierExtension;
import unq.api.model.*;
import unq.api.model.catalogs.SubjectOptions;
import unq.api.security.SecurityHeaders;
import unq.api.service.ApiService;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import unq.repository.MongoDBDAO;
import unq.utils.GsonFactory;
import utils.Utils;
import utils.Utils.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static utils.Utils.request;

public class DirectorControllerTest {

    private static String directorToken;

    @Before
    public void setUp() throws Exception {

    }
    @BeforeClass
    public static void beforeClass() {
        ApiService.main(null);
        directorToken = singUpDummyDirector();
    }
    @AfterClass
    public static void afterClass() {
        Spark.stop();
    }




    @Test
    public void saveSubjectOKWithToken(){
        Subject s = this.saveSubject("TAdP", directorToken);
        TestResponse res = request("GET", "/public/subjects/"+s.getSchoolYear(),new HashMap<>());
        Type listType = new TypeToken<List<SubjectOptions>>() {}.getType();
        List<SubjectOptions> subs = GsonFactory.fromJson(res.body, listType);
        Assert.assertTrue(subjectExists(s, subs));
    }

    private boolean subjectExists(Subject s, List<SubjectOptions> subs) {
        List<String> names = subs.stream().map(sub->sub.getSubjectName()).collect(Collectors.toList());
        return names.contains(s.getName());
    }

   @Test
    public void saveSubjectWithoutToken(){
        Subject subject = new Subject();
        subject.setGroup("avanzadas");
        subject.setName("C");
        subject.setSchoolYear("201301");
        Division d = new Division();
        d.setComision(Comision.C1);
        d.setQuota(35L);
        List<String> wd = new ArrayList<>();
        wd.add("MArtes de 15 a 18");
        d.setWeekdays(wd);
        List<Division> divs = new ArrayList<>();
        divs.add(d);
        subject.setDivisions(divs);
        String subjectJson = GsonFactory.toJson(subject);
        Utils.TestResponse res = request("POST", "/director/subject", subjectJson, new HashMap<>());
        TestResponse res2 = request("GET", "/public/subjects/"+subject.getSchoolYear(),new HashMap<>());
        Type listType = new TypeToken<List<SubjectOptions>>() {}.getType();
        List<SubjectOptions> subs = GsonFactory.fromJson(res2.body, listType);
        Assert.assertFalse(subjectExists(subject,subs));
        Assert.assertEquals(res.status, 401);
    }

    private Subject saveSubject(String name, String directorToken){
        Subject subject = new Subject();
        subject.setGroup("avanzadas");
        subject.setName(name);
        subject.setSchoolYear("201301");
        Division d = new Division();
        d.setComision(Comision.C1);
        d.setQuota(35L);
        List<String> wd = new ArrayList<>();
        wd.add("Lunes de 15 a 18");
        d.setWeekdays(wd);
        List<Division> divs = new ArrayList<>();
        divs.add(d);
        subject.setDivisions(divs);
        String subjectJson = GsonFactory.toJson(subject);
        Utils.TestResponse res = request("POST", "/director/subject", subjectJson, setUpSecurityHeaders(SecurityHeaders.X_DIRECTOR_TOKEN, directorToken));
        //student.setAuthToken(res.body);
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


}