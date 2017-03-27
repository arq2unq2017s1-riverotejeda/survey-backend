package unq.api.model;

import java.io.Serializable;

import com.despegar.integration.mongo.entities.IdentifiableEntity;

/**
 * Created by marina.rivero on 19/09/2016.
 */
public class Student implements IdentifiableEntity, Serializable {

	private String id;
	private String name;
	private String legajo;
	private String email;
	private String authToken;

	public Student() {
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLegajo(String legajo) {
		this.legajo = legajo;
	}

	public String getLegajo() {
		return legajo;
	}

	public String getName() {
		return name;
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
