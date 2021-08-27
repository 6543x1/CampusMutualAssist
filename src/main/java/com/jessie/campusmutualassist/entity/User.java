package com.jessie.campusmutualassist.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.jessie.campusmutualassist.entity.myEnum.Role;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("用户类")
public class User implements Serializable
{
    //@Email
    @Length(min=2,max=20,message = "2<=长度<=20")//设置为2因为之前前端注册了个两个长度的号。。。
    String username;
    @Length(min=6,max=100,message = "密码长度为6-100")
    //@JSONField(serialize = false)
    String password;
    int uid;
    @Email
    String mailAddr;
    String realName;
    int status;
    @ApiModelProperty("用户身份，enum类型，包含admin student teacher")
    Role role;
    String img_path;
    int evaluation;
    String mobileNumber;

    @Override
    public String toString()
    {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", uid=" + uid +
                ", mailAddr='" + mailAddr + '\'' +
                ", nickName='" + realName + '\'' +
                ", status=" + status +
                ", role='" + role + '\'' +
                ", img_path='" + img_path + '\'' +
                '}';
    }

    @JSONField(serialzeFeatures = SerializerFeature.WriteNullStringAsEmpty)//为啥BEAN里的没用？？？
    public String getRealName()
    {
        return realName;
    }//5.8日 答：因为当时Bean里可能是注入到最后一个了


}
