package com.jessie.campusmutualassist;

import com.jessie.campusmutualassist.controller.teacherController;
import com.jessie.campusmutualassist.entity.StuPointsWithRealName;
import com.jessie.campusmutualassist.mapper.UserMapper;
import com.jessie.campusmutualassist.service.PermissionService;
import com.jessie.campusmutualassist.service.StudentPointsService;
import com.jessie.campusmutualassist.service.UserService;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CampusMutualAssistApplication.class)
public class MyTest {
    @Autowired
    UserService userService;
    @Autowired
    UserMapper userMapper;
    @Autowired
    PermissionService permissionService;
    @Autowired
    StudentPointsService studentPointsService;
    private static Logger logger= LoggerFactory.getLogger(MyTest.class);
    @Test
    public void testQueryUserPermissions(){
        List<String> myList=permissionService.getAllUserPermissions("Jessie");
        for(String x:myList){
            System.out.println(x);
        }
        logger.info("测试已经结束");
    }
    @Test
    public void testReadExcel() throws Exception {
        File file2 = new File("D:/test.xlsx");
        String suffix = file2.getName();
        suffix = suffix.substring(suffix.lastIndexOf(".") + 1);
        Workbook workbook;
        if ("xlsx".equals(suffix)) {
            workbook = new XSSFWorkbook(file2);
        } else if ("xls".equals(suffix)) {
            FileInputStream fis = new FileInputStream(file2);
            workbook = new HSSFWorkbook(fis);
        } else {
            return;
        }
        Sheet sheet = workbook.getSheetAt(0);     //读取sheet 0

        int firstRowIndex = sheet.getFirstRowNum();   //第一行是列名，所以不读
        int lastRowIndex = sheet.getLastRowNum();
        System.out.println("firstRowIndex: " + firstRowIndex);
        System.out.println("lastRowIndex: " + lastRowIndex);
        Row row0=sheet.getRow(firstRowIndex);
        int theNoIndex=0;
        if(row0!=null){
            int firstCellIndex = row0.getFirstCellNum();
            int lastCellIndex = row0.getLastCellNum();
            for (int cIndex = firstCellIndex; cIndex < lastCellIndex; cIndex++) {   //遍历列
                Cell cell = row0.getCell(cIndex);
                if (cell != null&&cell.toString().contains("学号")) {
                    theNoIndex=cIndex;
                    System.out.println("学号位于"+theNoIndex);
                    break;
                }
            }
        }
        for (int rIndex = firstRowIndex+1; rIndex <= lastRowIndex; rIndex++) {   //遍历行
            System.out.println("rIndex: " + rIndex);
            Row row = sheet.getRow(rIndex);
            if (row != null) {
                    Cell cell = row.getCell(theNoIndex);

                    if (cell != null) {
                        cell.setCellType(CellType.STRING);//强转String了。。这玩意自动把长数字文本识别成数字
                        System.out.println(cell.toString());
                        //System.out.println(cell.getCellTypeEnum());
                        if(cell.toString().contains(".")){
                            System.out.println("学号所在的列不是文本型，导入失败！请调整为文本型后重新上传");
                        }
                        //非文本型读取后会变成数字(强行toString情况下），有时候变成科学计数法了。。。。并且缺失掉0，实在是懒得做这个检测
                        //所以只能要求硬性设置为文本型
                    }
                }
            }

        file2.delete();
        return;
    }
    @Test
    public void testXMlQuery(){
        Set<String> mySet=new HashSet<>();
        mySet.add("student1");
        mySet.add("student2");
        List<Map<String,String>> myMap= userMapper.getRealNameWithUsername(mySet);
        System.out.println(myMap);
        //这种只能返回
    }
    @Test
    public void testJWCHPost()
    {
        String No="66233";
        String Password="123456";
        int code = 200;
        String result;
        try
        {
            String postURL = "http://59.77.226.32/logincheck.asp";
            PostMethod postMethod = null;
            postMethod = new PostMethod(postURL);
            postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            postMethod.setRequestHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
            postMethod.setRequestHeader("Accept-Encoding", "gzip, deflate");
            postMethod.setRequestHeader("Referer", "http://jwch.fzu.edu.cn/");
            postMethod.setRequestHeader("Origin", "http://jwch.fzu.edu.cn");
            postMethod.setRequestHeader("Upgrade-Insecure-Requests", "1");
            postMethod.setRequestHeader("DNT", "1");
            postMethod.setRequestHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");
            NameValuePair[] data = {
                    new NameValuePair("muser", No),
                    new NameValuePair("passwd", Password)

            };
            //参数设置，需要注意的就是里边不能传NULL，要传空字符串
            postMethod.setRequestBody(data);

            org.apache.commons.httpclient.HttpClient httpClient = new org.apache.commons.httpclient.HttpClient();
            int response = httpClient.executeMethod(postMethod); // 执行POST方法
            result = postMethod.getResponseBodyAsString();
            System.out.println(result);
            System.out.println(postMethod.getStatusCode());
            code = postMethod.getStatusCode();
            postMethod.abort();
        } catch (Exception e)
        {
            System.out.println("请求异常" + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        if (code == 302)
        {
            System.out.println("Not");
        }
        System.out.println("OK");
    }
    @Test
    public void ProjectDir(){
        String projectName = System.getProperty("user.dir");
        System.out.println(projectName);
    }
    @Test
    public void writeToXlsx() throws Exception{
        String classID="CIRD9F";
        String path = teacherController.class.getResource("").toString().substring(5) + classID + "/";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        File file2 = new File(path, classID + ".xlsx");
        Workbook workbook = new XSSFWorkbook(file2);
        Sheet sheet = workbook.getSheetAt(0);     //读取sheet 0

        int firstRowIndex = sheet.getFirstRowNum() + 1;   //第一行是列名，所以不读
        int lastRowIndex = sheet.getLastRowNum();
        System.out.println("firstRowIndex: " + firstRowIndex);
        System.out.println("lastRowIndex: " + lastRowIndex);
        Row firstRow = sheet.getRow(0);
        Cell cell1 = firstRow.getCell(0);
        cell1.setCellValue("学号");
        Cell cell2 = firstRow.getCell(1);
        cell2.setCellValue("姓名");
        Cell cell3 = firstRow.getCell(2);
        cell3.setCellValue("Points");
        Row row0 = sheet.getRow(firstRowIndex);
        int theNoIndex = 0;
        List<StuPointsWithRealName> list = studentPointsService.StusPoints(classID);
        for (int rIndex = 1; rIndex <= list.size(); rIndex++) {   //遍历行
            System.out.println("rIndex: " + rIndex);
            Row row = sheet.getRow(rIndex);
            Cell cellNo = row.getCell(0);
            cellNo.setCellType(CellType.STRING);
            cell1.setCellValue(list.get(rIndex).getUsername());
            Cell cellName = row.getCell(1);
            cell2.setCellValue(list.get(rIndex).getRealName());
            Cell cellPoints = row.getCell(2);
            cell3.setCellValue(list.get(rIndex).getPoints());
            //非文本型读取后会变成数字(强行toString情况下），有时候变成科学计数法了。。。。并且缺失掉0，实在是懒得做这个检测
        }       //所以只能要求硬性设置为文本型
    }
}
