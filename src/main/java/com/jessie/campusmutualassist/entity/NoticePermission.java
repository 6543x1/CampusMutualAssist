package com.jessie.campusmutualassist.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName notice_permission
 */
@TableName(value ="notice_permission")
@Data
public class NoticePermission implements Serializable {
    /**
     * 
     */
    @TableField(value = "reader")
    private String reader;

    /**
     * 
     */
    @TableField(value = "nid")
    private Integer nid;

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
        NoticePermission other = (NoticePermission) that;
        return (this.getReader() == null ? other.getReader() == null : this.getReader().equals(other.getReader()))
            && (this.getNid() == null ? other.getNid() == null : this.getNid().equals(other.getNid()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getReader() == null) ? 0 : getReader().hashCode());
        result = prime * result + ((getNid() == null) ? 0 : getNid().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", reader=").append(reader);
        sb.append(", nid=").append(nid);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}