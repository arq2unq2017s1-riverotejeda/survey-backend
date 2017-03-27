package unq.api.model;

import java.io.Serializable;
import java.util.Objects;

import static java.util.Objects.isNull;

public class ClassStatistics implements Serializable {

	private String subject;
	private CommissionOccupation c1;
	private CommissionOccupation c2;
	private CommissionOccupation c3;
	private CommissionOccupation c4;
	private Long notYet;
	private Long approved;
	private Long badSchedule;

	public ClassStatistics(String subject, CommissionOccupation c1, CommissionOccupation c2, CommissionOccupation c3,
						   CommissionOccupation c4, Long notYet, Long approved, Long badSchedule) {
		this.subject = subject;
		this.c1 = c1;
		this.c2 = c2;
		this.c3 = c3;
		this.c4 = c4;
		this.notYet = notYet;
		this.approved = approved;
		this.badSchedule = badSchedule;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public CommissionOccupation getC1() {
		return c1;
	}

	public void setC1(CommissionOccupation c1) {
		this.c1 = c1;
	}

	public CommissionOccupation getC2() {
		return c2;
	}

	public void setC2(CommissionOccupation c2) {
		this.c2 = c2;
	}

	public CommissionOccupation getC3() {
		return c3;
	}

	public void setC3(CommissionOccupation c3) {
		this.c3 = c3;
	}

	public CommissionOccupation getC4() {
		return c4;
	}

	public void setC4(CommissionOccupation c4) {
		this.c4 = c4;
	}

	public Long getNotYet() {
		return notYet;
	}

	public void setNotYet(Long notYet) {
		this.notYet = notYet;
	}

	public Long getApproved() {
		return approved;
	}

	public void setApproved(Long approved) {
		this.approved = approved;
	}

	public Long getBadSchedule() {
		return badSchedule;
	}

	public void setBadSchedule(Long badSchedule) {
		this.badSchedule = badSchedule;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("ClassStatistics{");
		sb.append("subject='").append(subject).append('\'');
		sb.append(", c1=").append(c1);
		sb.append(", c2=").append(c2);
		sb.append(", c3=").append(c3);
		sb.append(", c4=").append(c4);
		sb.append(", notYet=").append(notYet);
		sb.append(", approved=").append(approved);
		sb.append(", badSchedule=").append(badSchedule);
		sb.append('}');
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ClassStatistics classStatistics = (ClassStatistics) o;
		return Objects.equals(subject, classStatistics.subject) &&
				Objects.equals(c1, classStatistics.c1) &&
				Objects.equals(c2, classStatistics.c2) &&
				Objects.equals(c3, classStatistics.c3) &&
				Objects.equals(c4, classStatistics.c4) &&
				Objects.equals(notYet, classStatistics.notYet) &&
				Objects.equals(approved, classStatistics.approved) &&
				Objects.equals(badSchedule, classStatistics.badSchedule);
	}

	@Override
	public int hashCode() {
		return Objects.hash(subject, c1, c2, c3, c4, notYet, approved, badSchedule);
	}

	public static class Builder {

		private String subject;
		private CommissionOccupation c1;
		private CommissionOccupation c2;
		private CommissionOccupation c3;
		private CommissionOccupation c4;
		private Long notYet;
		private Long approved;
		private Long badSchedule;

		public Builder subject(String subject) {
			this.subject = subject;
			return this;
		}

		public Builder c1(CommissionOccupation c1) {
			this.c1 = c1;
			return this;
		}

		public Builder c2(CommissionOccupation c2) {
			this.c2 = c2;
			return this;
		}

		public Builder c3(CommissionOccupation c3) {
			this.c3 =  c3;
			return this;
		}

		public Builder c4(CommissionOccupation c4) {
			this.c4 = c4;
			return this;
		}

		public Builder notYet(Long notYet) {
			this.notYet = notYet;
			return this;
		}

		public Builder approved(Long approved) {
			this.approved = approved;
			return this;
		}

		public Builder badSchedule(Long badSchedule) {
			this.badSchedule = badSchedule;
			return this;
		}

		public ClassStatistics build() {

			if (isNull(this.notYet)) {
				this.notYet(0L);
			}

			if (isNull(this.approved)) {
				this.approved(0L);
			}

			if (isNull(this.badSchedule)) {
				this.badSchedule(0L);
			}

			return new ClassStatistics(this.subject, this.c1, this.c2, this.c3, this.c4, this.notYet, this.approved,
					this.badSchedule);
		}

	}

}
