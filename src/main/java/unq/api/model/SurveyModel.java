package unq.api.model;

import java.io.Serializable;
import java.util.List;

import unq.api.model.catalogs.SubjectOptions;

/**
 * Created by mrivero on 26/11/16.
 */
public class SurveyModel implements Serializable{
    private String studentName;
    private String legajo;
    private List<SubjectOptions> options;
    private Survey completedSurvey;

    public SurveyModel(String studentName, String legajo, List<SubjectOptions> options, Survey completedSurvey) {
        this.studentName = studentName;
        this.legajo = legajo;
        this.options = options;
        this.completedSurvey = completedSurvey;
    }

    public String getLegajo() {
        return legajo;
    }

    public void setLegajo(String legajo) {
        this.legajo = legajo;
    }

    public List<SubjectOptions> getOptions() {
        return options;
    }

    public void setOptions(List<SubjectOptions> options) {
        this.options = options;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
}
