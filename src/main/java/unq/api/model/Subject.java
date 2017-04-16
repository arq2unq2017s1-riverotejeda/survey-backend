package unq.api.model;

import java.io.Serializable;
import java.util.List;

import com.despegar.integration.mongo.entities.IdentifiableEntity;

/**
 * Created by mrivero on 2/10/16.
 */
public class Subject implements IdentifiableEntity, Serializable {

	private String id;
	private String name;
	private List<Division> divisions;
	private String group;
	private String schoolYear;

	public Subject() {
		super();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Division> getDivisions() {
		return divisions;
	}

	public void setDivisions(List<Division> divisions) {
		this.divisions = divisions;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getSchoolYear() {
		return schoolYear;
	}

	public void setSchoolYear(String schoolYear) {
		this.schoolYear = schoolYear;
	}

	@Override
	public String toString() {
		return "Subject{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", divisions=" + divisions +
				", group='" + group + '\'' +
				", schoolYear='" + schoolYear + '\'' +
				'}';
	}
}
