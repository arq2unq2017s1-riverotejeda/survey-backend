package unq.api.service;

import unq.api.model.*;
import unq.api.model.catalogs.SubjectOptions;

import java.util.List;
import java.util.Set;

/**
 * Created by mrivero on 21/9/16.
 */
public interface SurveyService {

	String saveStudent(Student student);

	Student getStudentByID(String name);

	String saveSurvey(Survey survey);

	Survey getSurveyByStudent(String studentName, String year);

	String saveSubject(Subject subject);

	List<SubjectOptions> getAllSubjects(String year);

	List<ClassStatistics> getClassOccupation(String year);

	SurveyStudentData getSurveyStudentData(String year);

	SurveyModel getSurveyModel(String token, String year);

	String getLastActiveYear();

	Set<String> getAllYears();
}
