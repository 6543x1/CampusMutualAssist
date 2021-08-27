package com.jessie.campusmutualassist.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleStu {
    String username;
    String realName;
    int points;
}
