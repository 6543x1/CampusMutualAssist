package com.jessie.campusmutualassist.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jessie.campusmutualassist.entity.Files;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Entity com.jessie.campusmutualassist..Files
 */
public interface FilesMapper extends BaseMapper<Files> {

    @Insert("insert into files (name, path, hash, classID, type,username,uploadTime) VALUES (#{name},#{path},#{hash},#{classID},#{type},#{username},#{uploadTime})")
    @Options(useGeneratedKeys = true,keyColumn = "fid",keyProperty = "fid")
    void newFile(Files files);

    @Select("select * from files where fid=#{fid}")
    Files getFile(long fid);

    @Select("select * from files where classID=#{classID}")
    List<Files> getClassFiles(String classID);

    @Select("select * from files where hash=#{hash} limit 1")
    Files getFilesByHash(String hash);
    //笑死 一开始limit1没写，然后上传三个就报500

    void newFile2(Files files);

    @Delete("delete from files where fid=#{fid}")
    void deleteByFid(long fid);
}




