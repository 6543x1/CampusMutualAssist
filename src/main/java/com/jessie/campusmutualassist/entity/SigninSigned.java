package com.jessie.campusmutualassist.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 持久化签到信息保存到数据库（为什么不是signer呢？因为signer意思不太对）
 * @TableName signin_signed
 */
@TableName(value ="signin_signed")
@Data
public class SigninSigned implements Serializable {
    /**
     * 
     */
    @TableId(value = "signID")
    private Integer signID;

    /**
     * 
     */
    @TableField(value = "signed")
    private Object signed;

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
        SigninSigned other = (SigninSigned) that;
        return (this.getSignID() == null ? other.getSignID() == null : this.getSignID().equals(other.getSignID()))
            && (this.getSigned() == null ? other.getSigned() == null : this.getSigned().equals(other.getSigned()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getSignID() == null) ? 0 : getSignID().hashCode());
        result = prime * result + ((getSigned() == null) ? 0 : getSigned().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", signID=").append(signID);
        sb.append(", signed=").append(signed);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}