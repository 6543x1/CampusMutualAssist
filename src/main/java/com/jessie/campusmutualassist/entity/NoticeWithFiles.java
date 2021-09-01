package com.jessie.campusmutualassist.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class NoticeWithFiles extends Notice {
    List<Files> files;
}
