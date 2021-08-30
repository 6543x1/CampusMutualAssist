package com.jessie.campusmutualassist.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @TableName files
 */
//为什么实体类还叫files呢？我觉得不好。
// 但是想一想，别的名字什么MyFile customizedFile ClassFile什么的名字太长了，干脆就跟着表名叫files算了,叫File就和自带的File冲了我不喜欢
@TableName(value = "files")
@Data
public class Files implements Serializable {
    /**
     *
     */

    @TableId(value = "fid", type = IdType.AUTO)
    @ApiModelProperty(value = "不用填")
    private Integer fid;

    /**
     *
     */
    @TableField(value = "name")
    @ApiModelProperty(value = "文件名字，可以不用")
    private String name;

    /**
     *
     */
    @TableField(value = "path")
    @ApiModelProperty(value = "不用填")
    private String path;

    /**
     *
     */
    @TableField(value = "hash")
    @ApiModelProperty(value = "文件SHA256，必填")
    private String hash;

    /**
     *
     */
    @TableField(value = "classID")
    @ApiModelProperty(value = "班级，服务器生成，不用填")
    private String classID;

    /**
     *
     */
    @TableField(value = "type")
    @ApiModelProperty(value = "文件类型，可以不用填")
    private String type;

    @TableField(value = "username")
    private String username;

    @TableField(value = "uploadTime")
    private LocalDateTime uploadTime;

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
        Files other = (Files) that;
        return (this.getFid() == null ? other.getFid() == null : this.getFid().equals(other.getFid()))
                && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
                && (this.getPath() == null ? other.getPath() == null : this.getPath().equals(other.getPath()))
                && (this.getHash() == null ? other.getHash() == null : this.getHash().equals(other.getHash()))
                && (this.getClassID() == null ? other.getClassID() == null : this.getClassID().equals(other.getClassID()))
                && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getFid() == null) ? 0 : getFid().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getPath() == null) ? 0 : getPath().hashCode());
        result = prime * result + ((getHash() == null) ? 0 : getHash().hashCode());
        result = prime * result + ((getClassID() == null) ? 0 : getClassID().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", fid=").append(fid);
        sb.append(", name=").append(name);
        sb.append(", path=").append(path);
        sb.append(", hash=").append(hash);
        sb.append(", classID=").append(classID);
        sb.append(", type=").append(type);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}