package unq.api.controller;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import spark.Spark;
import unq.api.service.ApiService;

import java.util.HashMap;
import java.util.Map;
import utils.Utils.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static utils.Utils.request;

public class DirectorControllerTest {

    @Before
    public void setUp() throws Exception {

    }
    @BeforeClass
    public static void beforeClass() {
        ApiService.main(null);
    }
    @AfterClass
    public static void afterClass() {
        Spark.stop();
    }

    @org.junit.Test
    public void initDirectorEndpoints() throws Exception {
        TestResponse res = request("GET", "/public/subjects/201701", new HashMap<>());
        Map<String, String> json = res.json();
        assertEquals(200, res.status);
        //assertEquals("john", json.get("name"));
        //assertEquals("john@foobar.com", json.get("email"));
        //assertNotNull(json.get("id"));
    }



}