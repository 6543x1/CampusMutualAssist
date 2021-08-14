package com.jessie.campusmutualassist.controller;

import com.github.pagehelper.PageInfo;
import com.jessie.campusmutualassist.entity.Result;
import com.jessie.campusmutualassist.entity.StuSelection;
import com.jessie.campusmutualassist.service.*;
import com.jessie.campusmutualassist.utils.JwtTokenUtil;
import com.jessie.campusmutualassist.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

import static com.jessie.campusmutualassist.service.impl.PermissionServiceImpl.getCurrentUsername;
@Api(tags = "学生通用操作类")
@RestController
@RequestMapping("/classes")
public class StuController {
    @Autowired
    TeachingClassService teachingClassService;
    @Autowired
    PermissionService permissionService;
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    NoticeService noticeService;
    @Autowired
    StuSelectionService stuSelectionService;
    @Autowired
    VoteService voteService;
    @ApiOperation(value = "加入班级",notes = "老师生成班级后会获得一个ID，请妥善保存")
    @PreAuthorize("hasAnyAuthority('student')")
    @PostMapping(value = "/{classID}/join",produces = "application/json;charset=UTF-8")
    public Result joinClass(@PathVariable("classID") String classID, HttpServletRequest request){
        if(redisUtil.hasKey("classID"+":"+classID+":type:"+"Auto_AcceptStu")){
            redisUtil.sAdd("class:"+classID+":"+"type:"+"members",getCurrentUsername());
            return Result.success("已经成功加入");
        }
        else {
            redisUtil.sAdd("class:" + classID + ":" + "type:" + "JoinQueue", getCurrentUsername());
        }
        return Result.success("已经成功申请加入该班级，请等待同意");
    }

    @ApiOperation(value = "查询当前账号所加入的全部班级",
            notes = "老师目前不算在里面"
    )
    @PreAuthorize("hasAnyAuthority('student')")
    @GetMapping(value = "/getMyClass",produces = "application/json;charset=UTF-8")
    public Result getClassInfo()
    {
        List<StuSelection> stuSelections=stuSelectionService.getStuSelections(getCurrentUsername());
        return Result.success("查询学生信息成功",stuSelections);
    }
    @ApiOperation(value = "查看班级投票")
    @PreAuthorize("hasAnyAuthority('student_'+#classID)")
    @GetMapping(value = "/{classID}/getVotes",produces = "application/json;charset=UTF-8")
    public PageInfo receiveAllVotes(@PathVariable("classID") String classID,@RequestParam(defaultValue = "1")int pageNum){
        //Set<String> voteSet= redisUtil.sGetMembers("class:" + classID + ":" + "type:" + "Vote");
        //PageInfo<Vote> voteList=voteService.getClassVotes(classID,pageNum);

        //分页可以从voteSet开始做...redis只能获取全部的元素，好在redis的速度并不会很慢，但是这样确实效率很低很低.........
        //老师发布新的投票后，信息应该推送给客户端，然后客户端去直接利用这个投票的ID获取这个投票的内容，不然的话，去刷新那个投票列表，可太哈人了
        //后台要写推送消息相关！
        //"class:" + classID + ":" + "type:" + "Vote"+":"+"title:"+title+":limit:"+"1"+"publisher:"+Username()
//        for(String title:voteSet){
//           Map<Object, Object> argus=redisUtil.hGetAll("class:" + classID + ":" + "type:" + "Vote"+":"+"title"+":"+title);
//           Vote vote=new Vote();
//           vote.setClassID(classID);
//           vote.setLimitation(Integer.parseInt((String)argus.get("limit")));
//           vote.setTitle((String) argus.get("title"));
//           //vote.setPublishedTime(LocalDateTime.ofInstant(Instant.ofEpochMills(Long.parseLong((String) argus.get("publishedTime"))), ZoneId.systemDefault()));
//           System.out.println(argus.get("publishedTime"));
//           Instant instant = Instant.ofEpochMilli(Long.parseLong((String) argus.get("publishedTime")));
//           vote.setPublishedTime(LocalDateTime.ofInstant(instant,ZoneId.systemDefault()));
//           vote.setSelections(redisUtil.zReverseRange("class:" + classID + ":" + "type:" + "VoteSelections"+":"+"title"+":"+title,0,-1));
//           votes.add(vote);
//           //投票也要更新一下，不要再去redis里查；应该去数据库里查询（开启enableCache加速）
//        }
        //分页怎么办？？？？，此处应该分页。。。额好像也不用，redis很快，一个班级的投票应该也不会太多吧,10个以内查询的速度应该不会太慢的
        //这个是很值得关注的问题
        return voteService.getClassVotes(classID,pageNum);
    }
    @ApiOperation(value = "查看某个投票的选项",notes = "适用于老师刚发布投票的场景，老师发布新投票后，直接根据ID请求，就不用去查看全部投票了")
    @PreAuthorize("hasAnyAuthority('student_'+#classID)")
    @GetMapping(value = "/{classID}/voteSelections",produces = "application/json;charset=UTF-8")
    public Result VoteSelections(@PathVariable("classID") String classID,long vid){
        //此处不再是title，注意返回的信息要包含title
        Set<ZSetOperations.TypedTuple<String>> typedTuples = redisUtil.zReverseRangeWithScores("class:" + classID + ":" + "type:" + "VoteSelections" + ":" + "vid" + ":" + vid, 0, -1);
        return Result.success("获取投票选项成功",typedTuples);
    }
    @ApiOperation(value = "学生投票，多选投票在selections中加上多个选项即可")
    @PreAuthorize("hasAnyAuthority('student_'+#classID)")//和下面保持一致
    @PostMapping(value = "/{classID}/vote", produces = "application/json;charset=UTF-8")
    public Result vote(@PathVariable("classID") String classID, String title,@RequestParam("selections") Set<String> selections) {
        if(redisUtil.sIsMember("class:" + classID + ":" + "type:" + "Voter"+":"+"title:"+title,getCurrentUsername())){
            return Result.error("你已投票，请勿重复投票");
        }
        if(Integer.parseInt(String.valueOf(redisUtil.hGet("class:" + classID + ":" + "type:" + "Vote"+":"+"title:"+title,"limit")))>=selections.size()){
            return Result.error("最多只能选择"+redisUtil.hGet("class:" + classID + ":" + "type:" + "Vote"+":"+"title:"+title,"limit")+"项选项！");
        }
        for(String x:selections){
            redisUtil.zIncrementScore("class:" + classID + ":" + "type:" + "VoteSelections"+":"+"title:"+title,x,1);
        }
        redisUtil.sAdd("class:" + classID + ":" + "type:" + "Voter"+":"+"title:"+title,getCurrentUsername());
        if((boolean) redisUtil.hGet("class:" + classID + ":" + "type:" + "Vote"+":"+"title:"+title,"anonymous")){
            for(String x:selections){
            redisUtil.sAdd("class:" + classID + ":" + "type:" + "Voter"+":"+"title:"+title+":Selection:"+x,getCurrentUsername());
            }
        }
        //投票可以搞个哈希表;;;;;
        //请勿添加过多选项，以免响应缓慢
        return Result.success("投票成功");
    }
    //我觉得投票的结果还是要存到数据库里去，可以用消息队列来解决这个问题，要不就@Async
    @ApiOperation(value = "签到，直接以signIn来请求")
    @PreAuthorize("hasAnyAuthority('student_'+#classID)")//和下面保持一致
    @PostMapping(value = "/{classID}/signIn", produces = "application/json;charset=UTF-8")
    public Result SignIn(@PathVariable("classID") String classID, String key) {
       if(redisUtil.hasKey("class:" + classID + ":type:" + "signIn"+":"+"key:"+key)){
           redisUtil.sRemove("class:" + classID + ":type:" + "signIn"+":"+"key:"+key,getCurrentUsername());
       }
       return Result.success("签到成功");
    }
    @ApiOperation(value = "确认公告")
    @PreAuthorize("hasAnyAuthority('student_'+#classID)")//和下面保持一致
    @PostMapping(value = "/{classID}/notice/confirm", produces = "application/json;charset=UTF-8")
    public Result confirmNotice(@PathVariable("classID") String classID, long nid) {
        redisUtil.sAdd("class:" + classID + ":type:" + "noticeConfirmed"+":"+"nid:"+nid,getCurrentUsername());
        return Result.success("已确认");
    }


}
