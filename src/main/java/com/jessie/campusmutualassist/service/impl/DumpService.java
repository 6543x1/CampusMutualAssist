package com.jessie.campusmutualassist.service.impl;

import com.jessie.campusmutualassist.entity.Files;
import com.jessie.campusmutualassist.entity.StuPointsDetail;
import com.jessie.campusmutualassist.entity.StuPointsWithRealName;
import com.jessie.campusmutualassist.service.FilesService;
import com.jessie.campusmutualassist.service.StudentPointsService;
import com.jessie.campusmutualassist.utils.DigestUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Service("dumpService")
public class DumpService {
    @Autowired
    StudentPointsService studentPointsService;
    @Autowired
    FilesService filesService;
    @Autowired
    StuPointsDetailServiceImpl stuPointsDetailService;

    @Async
    public void dumpClassPointsXlsx(String classID, String username, String className) {
        String path = "/usr/camFiles/" + classID + "/";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        File file2 = new File(path, className + "Points.xlsx");
        try {
            file2.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("活跃分总览");     //读取sheet 0

        Row firstRow = sheet.createRow(0);
        Cell cell1 = firstRow.createCell(0);
        cell1.setCellValue("学号");
        Cell cell2 = firstRow.createCell(1);
        cell2.setCellValue("姓名");
        Cell cell3 = firstRow.createCell(2);
        cell3.setCellValue("活跃分");
        List<StuPointsWithRealName> list = studentPointsService.StusPoints(classID);
        System.out.println(list);
        for (int rIndex = 1; rIndex <= list.size(); rIndex++) {   //遍历行
            System.out.println("rIndex: " + rIndex);
            Row row = sheet.createRow(rIndex);
            Cell cellNo = row.createCell(0);
            cellNo.setCellType(CellType.STRING);
            cellNo.setCellValue(list.get(rIndex - 1).getUsername());
            Cell cellName = row.createCell(1);
            cellName.setCellValue(list.get(rIndex - 1).getRealName());
            Cell cellPoints = row.createCell(2);
            cellPoints.setCellValue(list.get(rIndex - 1).getPoints());
            //在Excel 2019中测试，发现虽然打开文件的时候，学号前面有0，但是一旦点击，马上。。。就变成无0
            //不知道是Excel的锅还是啥，我已经尽力了（指设置Type为String）
        }
        Sheet sheet2 = workbook.createSheet("详细加分情况");
        String[] titles = {"学号", "类别", "分数变化", "时间", "操作人"};
        List<StuPointsDetail> DetailList = stuPointsDetailService.classDetails(classID);
        Row firstRow2 = sheet2.createRow(0);
        for (int i = 0; i < titles.length; i++) {
            Cell cell = firstRow2.createCell(i);
            cell.setCellValue(titles[i]);
        }
        for (int rIndex = 1; rIndex <= DetailList.size(); rIndex++) {
            Row row = sheet2.createRow(rIndex);
            Cell cellNo = row.createCell(0);
            cellNo.setCellType(CellType.STRING);
            cellNo.setCellValue(DetailList.get(rIndex - 1).getTarget());
            Cell cellType = row.createCell(1);
            cellType.setCellValue(DetailList.get(rIndex - 1).getReason());
            Cell cellPoints = row.createCell(2);
            cellPoints.setCellValue(DetailList.get(rIndex - 1).getPoints());
            Cell cellTime = row.createCell(3);
            cellTime.setCellValue(DetailList.get(rIndex - 1).getTime().toString());
            Cell cellOperator = row.createCell(4);
            cellOperator.setCellValue(DetailList.get(rIndex - 1).getOperator());
            //非文本型读取后会变成数字(强行toString情况下），有时候变成科学计数法了。。。。并且缺失掉0，实在是懒得做这个检测
        }       //所以只能要求硬性设置为文本型

        try {
            workbook.write(new FileOutputStream(file2));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Files files = new Files();
        files.setName(className + "Points.xlsx");
        files.setPath(path);
        files.setClassID(classID);
        files.setUsername(username);
        files.setType("文档");
        files.setHash(DigestUtil.getSHA256(file2));
        filesService.newFile(files);
        try {
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
