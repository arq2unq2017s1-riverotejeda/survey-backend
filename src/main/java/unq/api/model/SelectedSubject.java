package unq.api.model;

import java.io.Serializable;

/**
 * Created by mrivero on 2/10/16.
 */
public class SelectedSubject implements Serializable {

	private String subject;

	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

}
