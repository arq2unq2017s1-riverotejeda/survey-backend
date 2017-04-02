package unq.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mrivero on 2/10/16.
 */
public enum SubjectStatus {

	@SerializedName("not_yet")
	NOT_YET, @SerializedName("approved")
	APPROVED, @SerializedName("bad_schedule")
	BAD_SCHEDULE

}
