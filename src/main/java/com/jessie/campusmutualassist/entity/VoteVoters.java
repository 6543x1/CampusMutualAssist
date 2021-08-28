package com.jessie.campusmutualassist.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 持久化投票信息到数据库中
 * @TableName vote_voters
 */
@TableName(value ="vote_voters")
@Data
public class VoteVoters implements Serializable {
    /**
     * 
     */
    @TableId(value = "vid")
    private Integer vid;

    /**
     * 
     */
    @TableField(value = "voters")
    private Object voters;

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
        VoteVoters other = (VoteVoters) that;
        return (this.getVid() == null ? other.getVid() == null : this.getVid().equals(other.getVid()))
            && (this.getVoters() == null ? other.getVoters() == null : this.getVoters().equals(other.getVoters()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getVid() == null) ? 0 : getVid().hashCode());
        result = prime * result + ((getVoters() == null) ? 0 : getVoters().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", vid=").append(vid);
        sb.append(", voters=").append(voters);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}