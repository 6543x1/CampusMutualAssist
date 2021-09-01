package com.jessie.campusmutualassist.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jessie.campusmutualassist.entity.Files;
import com.jessie.campusmutualassist.entity.Result;
import com.jessie.campusmutualassist.mapper.FilesMapper;
import com.jessie.campusmutualassist.service.FilesService;
import com.jessie.campusmutualassist.utils.DigestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

/**
 *
 */
@Service("filesService")
@Slf4j
public class FilesServiceImpl extends ServiceImpl<FilesMapper, Files>
        implements FilesService {
    @Autowired
    FilesMapper filesMapper;


    @Override
    @Caching(evict = {
            @CacheEvict(value = "fileByFid", key = "#files.fid"),
            @CacheEvict(value = "classFiles", key = "#files.classID")
    })
    public void newFile(Files files) {
        filesMapper.newFile(files);
    }

    @Override
    @Cacheable(value = "filesByFid", key = "#fid")
    public Files getFile(long fid) {
        return filesMapper.getFile(fid);
    }

    @Override
    @Cacheable(value = "classFiles", key = "#classID")
    public List<Files> getClassFiles(String classID) {
        return filesMapper.getClassFiles(classID);
    }

    @Override
    @Cacheable(value = "filesByHash", key = "#hash")//可以设置短些时间
    public Files getFilesByHash(String hash) {
        return filesMapper.getFilesByHash(hash);
    }

    @Override
    public boolean delete(long fid){
        Files files = filesMapper.getFile(fid);
        if (files == null) {
            return false;
        }
        File file = new File(files.getPath() + files.getName());
        file.delete();
        filesMapper.deleteByFid(fid);
        return true;
    }

    @Override
    public Result saveUpload(MultipartFile upload, String classID, String username, String hash, String type) {
        String path = "/usr/camFiles/" + classID + "/";
        return realSave(upload, classID, username, hash, type, path);
    }

    @Override
    public Result saveNoticeFiles(MultipartFile upload, String classID, String username, String hash, String type) {
        String path = "/usr/camFiles/" + classID + "/" + "notice/";
        if (type != null) {
            type = "Notice-" + type;
        } else {
            type = "Notice-" + "其他";
        }
        return realSave(upload, classID, username, hash, type, path);
    }

    public Result realSave(MultipartFile upload, String classID, String username, String hash, String type, String path) {
        String hashCode = "";
        File file = new File(path);
        Files files = new Files();
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            String filename = upload.getOriginalFilename();
            String suffix = filename.substring(filename.lastIndexOf(".") + 1);
            File file2 = new File(path + upload.getOriginalFilename());
            if (file2.exists()) {
                return Result.error("已经存在同名文件！");
            }
            upload.transferTo(file2);
            log.info("new file write to disk");
            hashCode = DigestUtil.getSHA256(file2);
            if (hash != null && !hash.equals(hashCode)) {
                file2.delete();
                return Result.error("文件的hash码不匹配");
            }
            files.setName(upload.getOriginalFilename());
            files.setClassID(classID);
            files.setHash(hashCode);
            files.setPath(path);
            files.setUsername(username);
            files.setType(type);
            files.setUploadTime(LocalDateTime.now());
            newFile(files);
            log.info("new file write to mysql");
        } catch (NullPointerException e) {
            e.printStackTrace();
            return Result.error("找不到文件的名字", 404);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("未知错误", 500);
        }
        return Result.success("成功", files.getFid());
    }

}




