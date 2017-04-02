package unq.api.model;

import java.io.Serializable;
import java.util.List;

import com.despegar.integration.mongo.entities.IdentifiableEntity;

/**
 * Created by mrivero on 5/11/16.
 */
public class Division implements Serializable, IdentifiableEntity {

	private String id;
	private Comision comision;
	private List<String> weekdays;
	private Long quota;

	public Comision getComision() {
		return comision;
	}

	public void setComision(Comision comision) {
		this.comision = comision;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	public List<String> getWeekdays() {
		return weekdays;
	}

	public void setWeekdays(List<String> weekdays) {
		this.weekdays = weekdays;
	}

	public Long getQuota() {
		return quota;
	}

	public void setQuota(Long quota) {
		this.quota = quota;
	}
}
