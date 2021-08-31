package com.jessie.campusmutualassist.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jessie.campusmutualassist.entity.Files;

import java.util.List;

/**
 *
 */
public interface FilesService extends IService<Files> {

    void newFile(Files files);

    Files getFile(long fid);

    List<Files> getClassFiles(String classID);

    Files getFilesByHash(String hash);

    boolean delete(long fid);
}
