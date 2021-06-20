package com.demo.controller;

import com.demo.config.Poi.ExcelUtil;
import com.demo.model.User;
import com.demo.service.UserService;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * Created by 33852 on 2017/4/7.
 */
@RestController
@RequestMapping("/user")
public class UserController {


    @GetMapping("/hehe/export")
    public void export(HttpServletResponse response )throws Exception{
        HSSFWorkbook wb = new HSSFWorkbook();
        //建立新的sheet对象（excel的表单）
        HSSFSheet sheet=wb.createSheet("sheet1");
        //在sheet里创建第一行，参数为行索引(excel的行)，可以是0～65535之间的任何一个
        HSSFRow row1=sheet.createRow(0);
        //创建单元格（excel的单元格，参数为列索引，可以是0～255之间的任何一个
        HSSFCell cell=row1.createCell(0);
        //设置单元格内容
        cell.setCellValue("学员考试成绩一览表");
        //合并单元格CellRangeAddress构造参数依次表示起始行，截至行，起始列， 截至列
        sheet.addMergedRegion(new CellRangeAddress(0,0,0,3));
        //在sheet里创建第二行
        HSSFRow row2=sheet.createRow(1);
        //创建单元格并设置单元格内容
        row2.createCell(0).setCellValue("姓名");
        row2.createCell(1).setCellValue("班级");
        row2.createCell(2).setCellValue("笔试成绩");
        row2.createCell(3).setCellValue("机试成绩");


        //在sheet里创建第三行
        HSSFRow row3=sheet.createRow(2);
        row3.createCell(0).setCellValue("李明");
        row3.createCell(1).setCellValue("As178");
        row3.createCell(2).setCellValue(87);
        row3.createCell(3).setCellValue(78);
        //.....省略部分代码


        //输出Excel文件
        OutputStream output=response.getOutputStream();
        response.reset();
        response.setHeader("Content-disposition", "attachment; filename=details.xls");
        response.setContentType("application/msexcel");
        wb.write(output);
        output.close();
    }

    //发送响应流方法
    public void setResponseHeader(HttpServletResponse response, String fileName) {
        try {
            try {
                fileName = new String(fileName.getBytes(),"ISO8859-1");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename="+ fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @GetMapping(value="/houhou/importExcel")
    public void doMakeXls( HttpServletRequest request, HttpServletResponse response) throws Exception{
        String content = "<div class=\"el-table el-table--fit el-table--border el-table--enable-row-hover el-table--enable-row-transition\" style=\"width: 100%;\"><div class=\"hidden-columns\"><div></div><div></div><div></div></div><div class=\"el-table__header-wrapper\"><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" class=\"el-table__header\" style=\"width: 617px;\"><colgroup><col name=\"el-table_3_column_7\" width=\"180\"><col name=\"el-table_3_column_8\" width=\"180\"><col name=\"el-table_3_column_9\" width=\"257\"><col name=\"gutter\" width=\"0\"></colgroup><thead class=\"has-gutter\"><tr class=\"\"><th colspan=\"1\" rowspan=\"1\" class=\"el-table_3_column_7     is-leaf\"><div class=\"cell\">日期</div></th><th colspan=\"1\" rowspan=\"1\" class=\"el-table_3_column_8     is-leaf\"><div class=\"cell\">姓名</div></th><th colspan=\"1\" rowspan=\"1\" class=\"el-table_3_column_9     is-leaf\"><div class=\"cell\">地址</div></th><th class=\"gutter\" style=\"width: 0px; display: none;\"></th></tr></thead></table></div><div class=\"el-table__body-wrapper is-scrolling-none\"><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" class=\"el-table__body\" style=\"width: 617px;\"><colgroup><col name=\"el-table_3_column_7\" width=\"180\"><col name=\"el-table_3_column_8\" width=\"180\"><col name=\"el-table_3_column_9\" width=\"257\"></colgroup><tbody><tr class=\"el-table__row\"><td rowspan=\"1\" colspan=\"1\" class=\"el-table_3_column_7  \"><div class=\"cell\">2016-05-03</div></td><td rowspan=\"1\" colspan=\"1\" class=\"el-table_3_column_8  \"><div class=\"cell\">王小虎</div></td><td rowspan=\"1\" colspan=\"1\" class=\"el-table_3_column_9  \"><div class=\"cell\">上海市普陀区金沙江路 1518 弄</div></td></tr><tr class=\"el-table__row\"><td rowspan=\"1\" colspan=\"1\" class=\"el-table_3_column_7  \"><div class=\"cell\">2016-05-02</div></td><td rowspan=\"1\" colspan=\"1\" class=\"el-table_3_column_8  \"><div class=\"cell\">王小虎</div></td><td rowspan=\"1\" colspan=\"1\" class=\"el-table_3_column_9  \"><div class=\"cell\">上海市普陀区金沙江路 1518 弄</div></td></tr><tr class=\"el-table__row\"><td rowspan=\"1\" colspan=\"1\" class=\"el-table_3_column_7  \"><div class=\"cell\">2016-05-04</div></td><td rowspan=\"1\" colspan=\"1\" class=\"el-table_3_column_8  \"><div class=\"cell\">王小虎</div></td><td rowspan=\"1\" colspan=\"1\" class=\"el-table_3_column_9  \"><div class=\"cell\">上海市普陀区金沙江路 1518 弄</div></td></tr><tr class=\"el-table__row\"><td rowspan=\"1\" colspan=\"1\" class=\"el-table_3_column_7  \"><div class=\"cell\">2016-05-01</div></td><td rowspan=\"1\" colspan=\"1\" class=\"el-table_3_column_8  \"><div class=\"cell\">王小虎</div></td><td rowspan=\"1\" colspan=\"1\" class=\"el-table_3_column_9  \"><div class=\"cell\">上海市普陀区金沙江路 1518 弄</div></td></tr><!----></tbody></table><!----><!----></div><!----><!----><!----><!----><div class=\"el-table__column-resize-proxy\" style=\"display: none;\"></div></div>";
        System.out.println("sxl_content:"+request.getParameter("txtContent"));
        response.setHeader("content-disposition", "attachment;filename=\"" + request.getParameter("txtName")+System.currentTimeMillis() + ".xls\"");
        response.setContentType("Application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write("<html>\n<meta http-equiv='Content-Type' content='text/html; charset=utf-8'><head>\n");
        response.getWriter().write("<style type=\"text/css\">\n.pb{font-size:13px;border-collapse:collapse;} "+
                "\n.pb th{font-weight:bold;text-align:center;border:0.5pt solid windowtext;padding:2px;} " +
                "\n.pb td{border:0.5pt solid windowtext;padding:2px;}\n</style>\n</head>\n");
        response.getWriter().write("<body>\n" + content+ "\n</body>\n</html>");
        response.getWriter().flush();
        response.getWriter().close();
    }
}
