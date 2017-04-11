package unq.api.service.impl;

import com.google.common.cache.CacheLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unq.api.model.Director;
import unq.api.model.Student;
import unq.api.model.Subject;
import unq.api.model.Survey;
import unq.api.security.HMACEncrypter;
import unq.api.service.RepositoryService;
import unq.repository.MongoDBDAO;
import unq.utils.EnvConfiguration;
import unq.utils.MongoCache;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static unq.utils.EnvConfiguration.LOGGER;

/**
 * Created by mrivero on 17/12/16.
 */
public class RepositoryServiceImpl implements RepositoryService {

    private static MongoDBDAO mongoDAO = new MongoDBDAO();
    private static MongoCache mongoCache = new MongoCache();
    private static Logger LOGGER = LoggerFactory.getLogger(RepositoryServiceImpl.class);
    private static RepositoryService INSTANCE= null;


    public static RepositoryService getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new RepositoryServiceImpl();
        }
        return INSTANCE;
    }


    @Override
    public Student getStudent(String id) {
        LOGGER.info(String.format("Getting student from cache %s", id));
        try {
            return mongoCache.getStudent.get(id);
        } catch (ExecutionException e) {
            LOGGER.error("Error trying to get student from cache");
            throw new RuntimeException(e);
        }catch(CacheLoader.InvalidCacheLoadException e){
            LOGGER.info("Student not found in database");
            return null;
        }
    }

    @Override
    public String saveStudent(Student student) {
        LOGGER.info(String.format("Saving student %s", student.getName()));

        return mongoDAO.saveStudent(student);
    }

    @Override
    public String saveSurvey(Survey survey) {
        LOGGER.info(String.format("Saving survey for student %s", survey.getStudentName()));

        return mongoDAO.saveSurvey(survey);
    }

    @Override
    public Survey getSurveyByStudent(String id, String year) {
        LOGGER.info(String.format("Getting survey by student %s and year %s", id, year));
        return mongoDAO.getSurveyByStudent(id, year);
    }

    @Override
    public String saveSubject(Subject subject) {
        LOGGER.info("Saving subject");
        return mongoDAO.saveSubject(subject);
    }

    @Override
    public String saveDirector(Director director) {
        LOGGER.info("Saving director");
        return mongoDAO.saveDirector(director);
    }

    @Override
    public List<Subject> getSubjects(String year) {
        LOGGER.info(String.format("Getting subjects by year %s", year));

        try {
            return mongoCache.getSubjects.get(year);
        } catch (ExecutionException e) {
            LOGGER.error("Error trying to get subjects from cache");
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Subject> getAllSubjects() {
        LOGGER.info("Getting all subjects");

        try {
            return mongoCache.getAllSubjects.get("AllSubjects");
        } catch (ExecutionException e) {
            LOGGER.error("Error trying to get all subjects from cache");
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Student> getStudents() {
        LOGGER.info("Getting all students");

        try {
            return mongoCache.getStudents.get("GetStudents");
        } catch (ExecutionException e) {
            LOGGER.error("Error trying to get all students from cache");
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Survey> getSurveys() {
        LOGGER.info("Getting all students");

        try {
            return mongoCache.getSurveys.get("GetSurveys");
        } catch (ExecutionException e) {
            LOGGER.error("Error trying to get all surveys from cache");
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Survey> getSurveys(String year){
        LOGGER.info("Getting all students");

        try {
            return mongoCache.getSurveysByYear.get(year);
        } catch (ExecutionException e) {
            LOGGER.error("Error trying to get all surveys from cache");
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Student> getStudentByToken(String token) {
        LOGGER.info(String.format("Getting student by token %s", token));

        try {
            return Optional.ofNullable(mongoCache.getStudentByToken.get(token));
        } catch (ExecutionException e) {
            LOGGER.error("Error trying to get student from cache");
            throw new RuntimeException(e);
        } catch(CacheLoader.InvalidCacheLoadException e){
            LOGGER.info("Student not found in database");
            return Optional.empty();
        }
    }

    @Override
    public Optional<Director> getDirectorByToken(String token) {
        LOGGER.info(String.format("Getting director by token %s", token));

        //String encryptedToken = HMACEncrypter.encrypt(token, EnvConfiguration.configuration.getString("encryption-key"));


        try {
            return Optional.ofNullable(mongoCache.getDirectorByToken.get(token));
        } catch (ExecutionException e) {
            LOGGER.error("Error trying to get director from cache");
            throw new RuntimeException(e);
        } catch(CacheLoader.InvalidCacheLoadException e){
            LOGGER.info("Director not found in database");
            return Optional.empty();
        }
    }

    @Override
    public Integer cantStudents() {
        LOGGER.info("Counting all students");

        try {
            return mongoCache.cantStudents.get("CantStudents");
        } catch (ExecutionException e) {
            LOGGER.error("Error trying to count all surveys from cache");
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer cantSurveys(String year) {
        LOGGER.info(String.format("Counting surveys by year %s", year));

        try {
            return mongoCache.cantSurveys.get(year);
        } catch (ExecutionException e) {
            LOGGER.error("Error trying count surveys from cache");
            throw new RuntimeException(e);
        }
    }
}
