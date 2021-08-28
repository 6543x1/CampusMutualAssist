package com.jessie.campusmutualassist.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface JsonTest {

        @Insert("insert into jsontest (json) VALUES (#{json})")
        void newJson(String json);

        @Select("select json from jsontest where id=#{id}")
        String getJson(int id);

}
