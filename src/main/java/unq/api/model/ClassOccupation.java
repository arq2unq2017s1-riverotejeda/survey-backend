package unq.api.model;

import java.io.Serializable;

/**
 * Created by mrivero on 5/11/16.
 */
public class ClassOccupation implements Serializable {

	private String subject;
	private String comision;
	private Long occupation;
	private Long percentage;

	public ClassOccupation(String subject, String comision, Long occupation, Long percentage) {
		this.subject = subject;
		this.comision = comision;
		this.occupation = occupation;
		this.percentage = percentage;
	}

	public Long getPercentage() {
		return percentage;
	}

	public void setPercentage(Long percentage) {
		this.percentage = percentage;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getComision() {
		return comision;
	}

	public void setComision(String comision) {
		this.comision = comision;
	}

	public Long getOccupation() {
		return occupation;
	}

	public void setOccupation(Long ocupation) {
		this.occupation = ocupation;
	}
}
