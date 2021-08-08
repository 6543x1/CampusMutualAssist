package com.jessie.campusmutualassist.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 
 * @TableName student_points
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentPoints implements Serializable {
    /**
     * 
     */
    private String username;

    /**
     * 
     */
    private String classID;

    /**
     * 
     */
    private Integer points;

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        StudentPoints other = (StudentPoints) that;
        return (this.getUsername() == null ? other.getUsername() == null : this.getUsername().equals(other.getUsername()))
            && (this.getClassID() == null ? other.getClassID() == null : this.getClassID().equals(other.getClassID()))
            && (this.getPoints() == null ? other.getPoints() == null : this.getPoints().equals(other.getPoints()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getUsername() == null) ? 0 : getUsername().hashCode());
        result = prime * result + ((getClassID() == null) ? 0 : getClassID().hashCode());
        result = prime * result + ((getPoints() == null) ? 0 : getPoints().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", username=").append(username);
        sb.append(", classID=").append(classID);
        sb.append(", points=").append(points);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}