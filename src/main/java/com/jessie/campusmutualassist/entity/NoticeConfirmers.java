package com.jessie.campusmutualassist.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 用于持久化地保存原本在Redis中的确认数据
 * @TableName notice_confirmers
 */
@TableName(value ="notice_confirmers")
@Data
public class NoticeConfirmers implements Serializable {
    /**
     * 
     */
    @TableId(value = "nid")
    private Integer nid;

    /**
     * 
     */
    @TableField(value = "confirmers")
    private Object confirmers;

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
        NoticeConfirmers other = (NoticeConfirmers) that;
        return (this.getNid() == null ? other.getNid() == null : this.getNid().equals(other.getNid()))
            && (this.getConfirmers() == null ? other.getConfirmers() == null : this.getConfirmers().equals(other.getConfirmers()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getNid() == null) ? 0 : getNid().hashCode());
        result = prime * result + ((getConfirmers() == null) ? 0 : getConfirmers().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", nid=").append(nid);
        sb.append(", confirmers=").append(confirmers);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}