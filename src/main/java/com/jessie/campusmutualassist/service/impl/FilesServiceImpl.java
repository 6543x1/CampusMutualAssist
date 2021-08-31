package com.jessie.campusmutualassist.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jessie.campusmutualassist.entity.Files;
import com.jessie.campusmutualassist.mapper.FilesMapper;
import com.jessie.campusmutualassist.service.FilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

/**
 *
 */
@Service("filesService")
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
        if(files==null){
            return false;
        }
        File file=new File(files.getPath()+files.getName());
        file.delete();
        filesMapper.deleteByFid(fid);
        return true;
    }


}




