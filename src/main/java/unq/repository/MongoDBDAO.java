package unq.repository;

import java.net.UnknownHostException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.despegar.integration.mongo.connector.MongoCollection;
import com.despegar.integration.mongo.connector.MongoCollectionFactory;
import com.despegar.integration.mongo.query.Query;

import unq.api.model.Director;
import unq.api.model.Student;
import unq.api.model.Subject;
import unq.api.model.Survey;
import unq.client.MongoDBClient;

/**
 * Created by mrivero on 22/9/16.
 */
public class MongoDBDAO {

	private static Logger LOGGER = LoggerFactory.getLogger(MongoDBDAO.class);
	private static MongoCollectionFactory mongoCollectionFactory = MongoDBClient.init();

	public Student getStudent(String id) {
		try {
			MongoCollection<Student> students = mongoCollectionFactory.buildMongoCollection("student", Student.class);

			Query query = new Query();
			query.equals("legajo", id);
			return students.findOne(query);

		} catch (UnknownHostException e) {
			throw new RuntimeException("Error executing Mongo query", e);
		}
	}

	public String saveStudent(Student student) {
		try {
			MongoCollection<Student> students = mongoCollectionFactory.buildMongoCollection("student", Student.class);
			return students.save(student);

		} catch (UnknownHostException e) {
			throw new RuntimeException("Error executing Mongo query", e);
		}
	}

	public String saveSurvey(Survey survey) {
		try {
			LOGGER.info("Starting saving survey object");
			MongoCollection<Survey> surveys = mongoCollectionFactory.buildMongoCollection("survey", Survey.class);

            //Delete existing survey
            LOGGER.info("Deleting existing survey");
            Query query = new Query();
            query.equals("legajo", survey.getLegajo());

            boolean remove = surveys.remove(query);
            LOGGER.info(String.format("Deleting survey %s", remove));

            String saved = surveys.save(survey);
            LOGGER.info("Finish saving survey object");


			return saved;

		} catch (UnknownHostException e) {
			throw new RuntimeException("Error executing Mongo query", e);
		}

	}

	public Survey getSurveyByStudent(String id, String year) {
		try {
			LOGGER.info("Getting survey by student");
			MongoCollection<Survey> surveys = mongoCollectionFactory.buildMongoCollection("survey", Survey.class);

			Query query = new Query();
			query.equals("legajo", id);
			query.equals("schoolYear", year);

			Survey survey = surveys.findOne(query);
			LOGGER.info("Finish survey by student");
			return survey;

		} catch (UnknownHostException e) {
			throw new RuntimeException("Error executing Mongo query", e);
		}
	}

	public String saveSubject(Subject subject) {
		try {
			LOGGER.info("Starting saving subject object");
			MongoCollection<Subject> subjects = mongoCollectionFactory.buildMongoCollection("subject", Subject.class);

			String saved = subjects.save(subject);
			LOGGER.info("Finish saving subject object");

			return saved;

		} catch (UnknownHostException e) {
			throw new RuntimeException("Error executing Mongo query", e);
		}

	}

	public List<Subject> getSubjects(String year) {
		try {
			LOGGER.info("Getting subjects from database");
			MongoCollection<Subject> subjects = mongoCollectionFactory.buildMongoCollection("subject", Subject.class);

			Query query = new Query();
			query.equals("schoolYear", year);

			List<Subject> savedSubjects = subjects.find(query);

			return savedSubjects;

		} catch (UnknownHostException e) {
			throw new RuntimeException("Error executing Mongo query", e);
		}
	}

	public List<Subject> getAllSubjects() {
		try {
			LOGGER.info("Getting subjects from database");
			MongoCollection<Subject> subjects = mongoCollectionFactory.buildMongoCollection("subject", Subject.class);
			List<Subject> savedSubjects = subjects.find();
			LOGGER.info("Finish getting subjects from database");

			return savedSubjects;

		} catch (UnknownHostException e) {
			throw new RuntimeException("Error executing Mongo query", e);
		}
	}

	public List<Student> getStudents() {
		try {
			LOGGER.info("Getting students from database");
			MongoCollection<Student> students = mongoCollectionFactory.buildMongoCollection("student", Student.class);

			List<Student> savedStudents = students.find();

			return savedStudents;

		} catch (UnknownHostException e) {
			throw new RuntimeException("Error executing Mongo query", e);
		}
	}

	public List<Survey> getSurveys() {
		try {
			MongoCollection<Survey> surveys = mongoCollectionFactory.buildMongoCollection("survey", Survey.class);
			return surveys.find();
		} catch (UnknownHostException e) {
			throw new RuntimeException("Error executing Mongo query", e);
		}
	}

	public List<Survey> getSurveysByYear(String year) {
		try {
			MongoCollection<Survey> surveys = mongoCollectionFactory.buildMongoCollection("survey", Survey.class);
			Query query = new Query();
			query.equals("schoolYear", year);

			return surveys.find(query);
		} catch (UnknownHostException e) {
			throw new RuntimeException("Error executing Mongo query", e);
		}
	}

	public Student getStudentByToken(String token) {
		try {
			MongoCollection<Student> students = mongoCollectionFactory.buildMongoCollection("student", Student.class);

			Query query = new Query();
			query.equals("authToken", token);
			return students.findOne(query);

		} catch (UnknownHostException e) {
			throw new RuntimeException("Error executing Mongo query", e);
		}
	}

	public Director getDirectorByToken(String token) {
		try {
			MongoCollection<Director> directors = mongoCollectionFactory.buildMongoCollection("director", Director.class);

			Query query = new Query();
			query.equals("token", token);
			return directors.findOne(query);

		} catch (UnknownHostException e) {
			throw new RuntimeException("Error executing Mongo query", e);
		}
	}

	public Integer cantStudents(){
		try {
			LOGGER.info("Counting students from database");
			MongoCollection<Student> students = mongoCollectionFactory.buildMongoCollection("student", Student.class);

			Integer cantStudents = students.count(null);

			return cantStudents;

		} catch (UnknownHostException e) {
			throw new RuntimeException("Error executing Mongo query", e);
		}
	}

	public Integer cantSurveys(String year) {
		try {
			LOGGER.info("Counting surveys from database");

			MongoCollection<Survey> surveys = mongoCollectionFactory.buildMongoCollection("survey", Survey.class);
			Query query = new Query();
			query.equals("schoolYear", year);

			return surveys.count(query);
		} catch (UnknownHostException e) {
			throw new RuntimeException("Error executing Mongo query", e);
		}
	}
	public String saveDirector(Director director) {
		try {
			MongoCollection<Director> directors = mongoCollectionFactory.buildMongoCollection("director", Director.class);
			directors.save(director);
			return director.getToken();

		} catch (UnknownHostException e) {
			throw new RuntimeException("Error executing Mongo query", e);
		}
	}

	public void deleteDirector(String token){
		try {
			MongoCollection<Director> directors = mongoCollectionFactory.buildMongoCollection("director", Director.class);
			Query query = new Query();
			query.equals("token", token);
			directors.remove(query);

		} catch (UnknownHostException e) {
			throw new RuntimeException("Error executing Mongo query", e);
		}
	}

	public void deleteStudent(String legajo){
		try {
			MongoCollection<Student> student = mongoCollectionFactory.buildMongoCollection("student", Student.class);
			Query query = new Query();
			query.equals("legajo", legajo);
			student.remove(query);

		} catch (UnknownHostException e) {
			throw new RuntimeException("Error executing Mongo query", e);
		}
	}

	public void deleteSurvey(String studentName, String schoolYear){
		try {
			MongoCollection<Survey> survey = mongoCollectionFactory.buildMongoCollection("Survey", Survey.class);
			Query query = new Query();
			query.equals("studentName", studentName);
			query.equals("schoolYear", schoolYear);

			survey.remove(query);
		} catch (UnknownHostException e) {
			throw new RuntimeException("Error executing Mongo query", e);
		}
	}
}
