package com.jessie.campusmutualassist.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jessie.campusmutualassist.entity.myEnum.SignType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @TableName signin
 */
@TableName(value = "signin")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignIn implements Serializable {
    /**
     *
     */
    @TableId(value = "signID", type = IdType.AUTO)
    private Integer signID;

    /**
     *
     */
    @TableField(value = "title")
    private String title;

    /**
     *
     */
    @TableField(value = "signKey")
    private String signKey;

    /**
     *
     */
    @TableField(value = "classID")
    private String classID;

    /**
     *
     */
    @TableField(value = "signedNum")
    private Integer signedNum;

    /**
     *
     */
    @TableField(value = "totalNum")
    private Integer totalNum;

    @TableField(value = "deadLine")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deadLine;

    @TableField(value = "signType")
    private SignType signType;

    @TableField(value = "publishedTime")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedTime;

    @TableField(value = "publisher")
    private String publisher;

    private boolean signed;
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
        SignIn other = (SignIn) that;
        return (this.getSignID() == null ? other.getSignID() == null : this.getSignID().equals(other.getSignID()))
                && (this.getTitle() == null ? other.getTitle() == null : this.getTitle().equals(other.getTitle()))
                && (this.getSignKey() == null ? other.getSignKey() == null : this.getSignKey().equals(other.getSignKey()))
                && (this.getClassID() == null ? other.getClassID() == null : this.getClassID().equals(other.getClassID()))
                && (this.getSignedNum() == null ? other.getSignedNum() == null : this.getSignedNum().equals(other.getSignedNum()))
                && (this.getTotalNum() == null ? other.getTotalNum() == null : this.getTotalNum().equals(other.getTotalNum()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getSignID() == null) ? 0 : getSignID().hashCode());
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
        result = prime * result + ((getSignKey() == null) ? 0 : getSignKey().hashCode());
        result = prime * result + ((getClassID() == null) ? 0 : getClassID().hashCode());
        result = prime * result + ((getSignedNum() == null) ? 0 : getSignedNum().hashCode());
        result = prime * result + ((getTotalNum() == null) ? 0 : getTotalNum().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", signID=").append(signID);
        sb.append(", title=").append(title);
        sb.append(", signKey=").append(signKey);
        sb.append(", classID=").append(classID);
        sb.append(", signedNum=").append(signedNum);
        sb.append(", totalNum=").append(totalNum);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}