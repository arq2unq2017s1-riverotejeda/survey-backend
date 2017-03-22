package unq.api.model;

import java.util.Objects;

import static java.util.Objects.isNull;

public class CommissionOccupation {

    private Long quota;
    private Long occupation;

    public CommissionOccupation(Long quota, Long occupation) {
        this.quota = isNull(quota) ? 0L : quota;
        this.occupation = isNull(occupation) ? 0L : occupation;
    }

    public Long getQuota() {
        return quota;
    }

    public void setQuota(Long quota) {
        this.quota = quota;
    }

    public Long getOccupation() {
        return occupation;
    }

    public void setOccupation(Long occupation) {
        this.occupation = occupation;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("CommissionOccupation{");
        sb.append("quota=").append(quota);
        sb.append(", occupation=").append(occupation);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommissionOccupation that = (CommissionOccupation) o;
        return Objects.equals(quota, that.quota) &&
                Objects.equals(occupation, that.occupation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quota, occupation);
    }
}
