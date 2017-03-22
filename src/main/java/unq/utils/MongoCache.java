package unq.utils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unq.api.model.Student;
import unq.api.model.Subject;
import unq.api.model.Survey;
import unq.repository.MongoDBDAO;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by mrivero on 17/12/16.
 */
public class MongoCache {

    private static MongoDBDAO mongoDAO = new MongoDBDAO();
    private static Logger LOGGER = LoggerFactory.getLogger(MongoCache.class);

    public LoadingCache<String, Student> getStudent = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(10L, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<String, Student>() {
                        public Student load(String id) throws RuntimeException {
                            LOGGER.info("Missing key, getting student from MongoDB");
                            return mongoDAO.getStudent(id);
                        }
                    });

    public LoadingCache<String, Survey> getSurveyByStudent = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(5L, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<String, Survey>() {
                        public Survey load(String parameters) throws RuntimeException {
                            LOGGER.info("Missing key, getting survey by student from MongoDB");
                            return mongoDAO.getSurveyByStudent(parameters.substring(6,parameters.length()), parameters.substring(0,6));
                        }
                    });

    public LoadingCache<String, List<Subject>> getSubjects = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(30L, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<String, List<Subject>>() {
                        public List<Subject> load(String year) throws RuntimeException {
                            LOGGER.info("Missing key, getting subjects from MongoDB");
                            return mongoDAO.getSubjects(year);
                        }
                    });


    public LoadingCache<String, List<Subject>> getAllSubjects = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(30L, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<String, List<Subject>>() {
                        public List<Subject> load(String key) throws RuntimeException {
                            LOGGER.info("Missing key, getting all subjects from MongoDB");
                            return mongoDAO.getAllSubjects();
                        }
                    });

    public LoadingCache<String, List<Student>> getStudents = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(30L, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<String, List<Student>>() {
                        public List<Student> load(String key) throws RuntimeException {
                            LOGGER.info("Missing key, getting all students from MongoDB");
                            return mongoDAO.getStudents();
                        }
                    });

    public LoadingCache<String, List<Survey>> getSurveys = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(15L, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<String, List<Survey>>() {
                        public List<Survey> load(String key) throws RuntimeException {
                            LOGGER.info("Missing key, getting all surveys from MongoDB");
                            return mongoDAO.getSurveys();
                        }
                    });

    public LoadingCache<String, List<Survey>> getSurveysByYear = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(15L, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<String, List<Survey>>() {
                        public List<Survey> load(String year) throws RuntimeException {
                            LOGGER.info("Missing key, getting all surveys from MongoDB");
                            return mongoDAO.getSurveysByYear(year);
                        }
                    });



    public LoadingCache<String, Student> getStudentByToken = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(5L, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<String, Student>() {
                        public Student load(String key) throws RuntimeException {
                            LOGGER.info("Missing key, getting student from MongoDB");
                            return mongoDAO.getStudentByToken(key);
                        }
                    });

    public LoadingCache<String, Integer> cantStudents = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(5L, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<String, Integer>() {
                        public Integer load(String key) throws RuntimeException {
                            LOGGER.info("Missing key, counting students from MongoDB");
                            return mongoDAO.cantStudents();
                        }
                    });

    public LoadingCache<String, Integer> cantSurveys = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(5L, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<String, Integer>() {
                        public Integer load(String key) throws RuntimeException {
                            LOGGER.info("Missing key, counting surveys from MongoDB");
                            return mongoDAO.cantSurveys(key);
                        }
                    });
}
