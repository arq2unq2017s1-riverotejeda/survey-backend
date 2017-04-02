package unq.api.model;

import java.util.List;

import com.despegar.integration.mongo.entities.IdentifiableEntity;

/**
 * Created by mrivero on 1/10/16.
 */
public class Survey implements IdentifiableEntity {

	private String id;
	private String studentName;
	private String legajo;
	private String token;
	private List<SelectedSubject> selectedSubjects;
	private String schoolYear;

	public String getSchoolYear() {
		return schoolYear;
	}

	public void setSchoolYear(String schoolYear) {
		this.schoolYear = schoolYear;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public String getLegajo() {
		return legajo;
	}

	public void setLegajo(String legajo) {
		this.legajo = legajo;
	}

	public List<SelectedSubject> getSelectedSubjects() {
		return selectedSubjects;
	}

	public void setSelectedSubjects(List<SelectedSubject> selectedSubjects) {
		this.selectedSubjects = selectedSubjects;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}
}
