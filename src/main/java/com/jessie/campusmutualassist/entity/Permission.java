package com.jessie.campusmutualassist.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Permission implements Serializable
{

    private String name;
    private String description;
    private String url;



}
