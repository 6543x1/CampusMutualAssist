package com.jessie.campusmutualassist.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jessie.campusmutualassist.entity.Files;
import com.jessie.campusmutualassist.entity.Result;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 *
 */
public interface FilesService extends IService<Files> {

    void newFile(Files files);

    Files getFile(long fid);

    List<Files> getClassFiles(String classID);

    @Cacheable(value = "classFiles", key = "#classID")
    PageInfo getClassFilesPage(String classID, int pageNum);

    Files getFilesByHash(String hash);

    boolean delete(long fid, String classID);

    Result fastUpload(Files files, String classID, String fileName, String hash, String username);

    Result saveUpload(MultipartFile upload, String classID, String username, String hash, String type);

    Result saveNoticeFiles(MultipartFile upload, String classID, String username, String hash, String type);

    Files getFileWithPath(long fid);
}
