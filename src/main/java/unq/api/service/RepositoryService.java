package unq.api.service;

import unq.api.model.Director;
import unq.api.model.Student;
import unq.api.model.Subject;
import unq.api.model.Survey;

import java.util.List;
import java.util.Optional;

/**
 * Created by mrivero on 17/12/16.
 */
public interface RepositoryService {

    Student getStudent(String id);
    String saveStudent(Student student);
    String saveSurvey(Survey survey);
    Survey getSurveyByStudent(String id, String year);
    String saveSubject(Subject subject);
    String saveDirector (Director director);
    List<Subject> getSubjects(String year);
    List<Subject> getAllSubjects();
    List<Student> getStudents();
    List<Survey> getSurveys();
    List<Survey> getSurveys(String year);
    Optional<Student> getStudentByToken(String token);
    Optional<Director> getDirectorByToken(String token);
    Integer cantStudents();
    Integer cantSurveys(String year);
}
