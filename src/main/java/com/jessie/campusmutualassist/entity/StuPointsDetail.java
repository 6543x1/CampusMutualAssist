package com.jessie.campusmutualassist.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @TableName stu_points_detail
 */
@TableName(value = "stu_points_detail")
@Data
public class StuPointsDetail implements Serializable {
    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     *
     */
    @TableField(value = "classID")
    private String classID;

    /**
     *
     */
    @TableField(value = "operator")
    private String operator;

    /**
     *
     */
    @TableField(value = "target")
    private String target;

    /**
     *
     */
    @TableField(value = "points")
    private Integer points;

    /**
     *
     */
    @TableField(value = "reason")
    private String reason;

    @TableField(value = "time")
    private LocalDateTime time;

    @TableField(exist = false)
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
        StuPointsDetail other = (StuPointsDetail) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getClassID() == null ? other.getClassID() == null : this.getClassID().equals(other.getClassID()))
                && (this.getOperator() == null ? other.getOperator() == null : this.getOperator().equals(other.getOperator()))
                && (this.getTarget() == null ? other.getTarget() == null : this.getTarget().equals(other.getTarget()))
                && (this.getPoints() == null ? other.getPoints() == null : this.getPoints().equals(other.getPoints()))
                && (this.getReason() == null ? other.getReason() == null : this.getReason().equals(other.getReason()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getClassID() == null) ? 0 : getClassID().hashCode());
        result = prime * result + ((getOperator() == null) ? 0 : getOperator().hashCode());
        result = prime * result + ((getTarget() == null) ? 0 : getTarget().hashCode());
        result = prime * result + ((getPoints() == null) ? 0 : getPoints().hashCode());
        result = prime * result + ((getReason() == null) ? 0 : getReason().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", classID=").append(classID);
        sb.append(", operator=").append(operator);
        sb.append(", target=").append(target);
        sb.append(", points=").append(points);
        sb.append(", reason=").append(reason);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}