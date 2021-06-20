package com.demo.config.Poi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * 将html table 转成 excel
 *
 * 记录下来所占的行和列，然后填充合并
 */
public class ConvertXml2Excel {

    private static short BORDER_WIDTH = (short) 1;
    /**
     * html表格转excel.
     * 重载方法
     * 注意xml的根元素为tables,其它暂时和html相似
     * @param tableHtml 这个字段是头部信息,包含下边的table2Excel(String tableHtml)
     *                  另外,table添加来表示table的数据源(第二个参数dataMap的key)<br>
     *                  tbody 只要一个tr,tr下的td添加column-data来表示column的数据源
     *  @param tableHtml 格式如
     *            <tables>
     *              <table table-data="" col-split="2" row-split="3" sheet-name="会计师事务所基本情况汇总表（全国）" sheet-title="会计师事务所基本情况汇总表（全国）">
     *                  <thead>
     *                      <th></th>
     *                      ...
     *                  </thead>
     *                  <tbody>
     *                      <tr>
     *                          <td column-data="结果集对应的字段"></td>
     *                          ...
     *                      </tr>
     *                  </tbody>
     *              </table>
     *            </tables>
     * @param dataMap 数据结构为 {table-data:[{column-data:000}...]}
     * @return
     */
    @SuppressWarnings("unchecked")
    public static HSSFWorkbook table2Excel(String tableHtml, Map<String,Object> dataMap) {
        HSSFWorkbook wb = new HSSFWorkbook();
        try {
            Document data = DocumentHelper.parseText(tableHtml);
            Element tables = data.getRootElement();
            List<Element> tableList = tables.elements("table");
            for (Element table : tableList) {

                //获取sheet的name,sheet的标题,冻结列数,冻结行数,数据源
                Attribute sheetName = table.attribute("sheet-name"),
                        colSplit = table.attribute("col-split"),
                        rowSplit = table.attribute("row-split"),
                        tableData = table.attribute("table-data");
                //获取数据源
                if(tableData == null){
                    System.out.println("数据源为空,这里要怎么处理一下呢?");
                    continue;
                }
                String tableDataSrc = tableData.getValue();
                if(!dataMap.containsKey(tableDataSrc)){
                    System.out.println("数据源为空,这里要怎么处理一下呢?");
                    continue;
                }
                List<Map<String,Object>> tableDataList = (List<Map<String,Object>>)dataMap.get(tableDataSrc);

                HSSFSheet sheet = sheetName == null ?
                        wb.createSheet() : StringUtils.isEmpty(sheetName.getValue()) ?
                        wb.createSheet() : sheetName.getValue().length() > 31 ?
                        wb.createSheet(sheetName.getValue().substring(0,30)) : wb.createSheet(sheetName.getValue());

                List<CrossRangeCellMeta> crossRowEleMetaLs = new ArrayList<CrossRangeCellMeta>();
                int rowIndex = setTitleAndThread(wb,sheet,table,crossRowEleMetaLs);

                // 生成表体
                Element tbody = table.element("tbody");
                if (tbody != null) {
                    //有且只有唯一的一个tr
                    Element tr = tbody.element("tr");
                    List<Element> tds = tr.elements("td");
                    //导出数据过多时，由于cell styles太多create造成报错The maximum number of cell styles was exceeded，
                    //故一般可以把cellstyle设置放到循环外面
                    CellStyle cellStyle = getContentStyle(wb);
                    for(Map<String,Object> map: tableDataList){
                        HSSFRow row = sheet.createRow(rowIndex);
                        int cellIndex = 0;
                        for(Element tdEle:tds){
//                            Element thEle = tdLs.get(eleIndex);
                            Attribute columnData = tdEle.attribute("column-data");
                            Object val = "";
                            if(columnData != null && StringUtils.isNotEmpty(columnData.getValue())){
                                val = map.get(columnData.getValue());
                            }
                            HSSFCell c = row.createCell(cellIndex);
                            //设置列宽,据说*2*256这种写法对中文比较好.
                            c.setCellValue(val != null ? val.toString() : "");
                            c.setCellStyle(cellStyle);
                            cellIndex++;
                        }
                        rowIndex++;
                    }
                }
                // 合并表头
                for (CrossRangeCellMeta crcm : crossRowEleMetaLs) {
                    CellRangeAddress cra = new CellRangeAddress(crcm.getFirstRow(), crcm.getLastRow(), crcm.getFirstCol(), crcm.getLastCol());
                    //解决合并单元格后的边框问题
                    setRegionBorder(cra, sheet,wb);
                    sheet.addMergedRegion(cra);
                }
                //计算并设置冻结行列数
                int h = colSplit == null ? 0 : NumberUtils.toInt(colSplit.getValue()),
                        i = rowSplit == null ? 0 : NumberUtils.toInt(rowSplit.getValue()),
                        j = h+1,k=i+1;
                sheet.createFreezePane(h,i,j,k);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return wb;
    }

    /**
     * 设置sheet的标题和表头,主要是为了两个重载的方法公用
     * @param wb
     * @param sheet
     * @param table
     * @param crossRowEleMetaLs
     * @return
     */
    @SuppressWarnings("unchecked")
    private static int setTitleAndThread(HSSFWorkbook wb,HSSFSheet sheet,Element table,List<CrossRangeCellMeta> crossRowEleMetaLs){
        int rowIndex = 0;
        HSSFCellStyle titleStyle = getTitleStyle(wb);
        Attribute sheetTitle = table.attribute("sheet-title");
        Element thead = table.element("thead");
        // 生成标题
        if(sheetTitle != null){
            int titleColspan = 1;
            if (thead != null) {
                List<Element> trLs = thead.elements("tr");
                titleColspan = getColspan(trLs.get(0).elements("th"));
            }
            HSSFRow row = sheet.createRow(rowIndex);
            row.setHeight((short)500);
            HSSFCell c = row.createCell(0);
            c.setCellValue(sheetTitle.getValue());
            c.setCellStyle(titleStyle);
            crossRowEleMetaLs.add(new CrossRangeCellMeta(rowIndex, 0, 1, titleColspan));
            rowIndex++;
        }
        //生成表头
        //表头自动换行
        titleStyle.setWrapText(true);
        if (thead != null) {
            List<Element> trLs = thead.elements("tr");
            for (Element trEle : trLs) {
                HSSFRow row = sheet.createRow(rowIndex);
                List<Element> thLs = trEle.elements("th");
                makeRowCell(sheet,thLs, rowIndex, row, 0, titleStyle, crossRowEleMetaLs);
                rowIndex++;
            }
        }
        return rowIndex;
    }
    /**
     * html表格转excel.注意xml的根元素为tables,其它暂时和html相似
     *
     * @param tableHtml 如
     *            <tables>
     *              <table col-split="2" row-split="3" sheet-name="会计师事务所基本情况汇总表（全国）" sheet-title="会计师事务所基本情况汇总表（全国）">
     *
     *                   </table>
     *            </table>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static HSSFWorkbook table2Excel(String tableHtml) {
        HSSFWorkbook wb = new HSSFWorkbook();
        try {
            Document data = DocumentHelper.parseText(tableHtml);
            Element tables = data.getRootElement();
            List<Element> tableList = tables.elements("table");
            for (Element table : tableList) {

                //获取sheet的name,sheet的标题,冻结列数,冻结行数
                Attribute sheetName = table.attribute("sheet-name"),
                        colSplit = table.attribute("col-split"),
                        rowSplit = table.attribute("row-split");

                //如果有sheetName,那么长度应在1-31之间.不要问我为什么,poi的规定,或者excel的规定?
                HSSFSheet sheet = sheetName == null ?
                        wb.createSheet() : StringUtils.isEmpty(sheetName.getValue()) ?
                        wb.createSheet() : sheetName.getValue().length() > 31 ?
                        wb.createSheet(sheetName.getValue().substring(0,30)) : wb.createSheet(sheetName.getValue());

                List<CrossRangeCellMeta> crossRowEleMetaLs = new ArrayList<CrossRangeCellMeta>();

                HSSFCellStyle titleStyle = getTitleStyle(wb);
                int rowIndex = setTitleAndThread(wb,sheet,table,crossRowEleMetaLs);
                // 生成表体
                Element tbody = table.element("tbody");
                if (tbody != null) {
                    HSSFCellStyle contentStyle = getContentStyle(wb);
                    List<Element> trLs = tbody.elements("tr");
                    for (Element trEle : trLs) {
                        HSSFRow row = sheet.createRow(rowIndex);
                        int cellIndex = makeRowCell(null,trEle.elements("th"), rowIndex, row, 0, titleStyle, crossRowEleMetaLs);
                        makeRowCell(null,trEle.elements("td"), rowIndex, row, cellIndex, contentStyle, crossRowEleMetaLs);
                        rowIndex++;
                    }
                }
                // 合并表头
                for (CrossRangeCellMeta crcm : crossRowEleMetaLs) {
                    CellRangeAddress cra = new CellRangeAddress(crcm.getFirstRow(), crcm.getLastRow(), crcm.getFirstCol(), crcm.getLastCol());
                    //解决合并单元格后的边框问题
                    setRegionBorder(cra, sheet,wb);
                    sheet.addMergedRegion(cra);
                }
                //计算并设置冻结行列数
                int h = colSplit == null ? 0 : NumberUtils.toInt(colSplit.getValue()),
                        i = rowSplit == null ? 0 : NumberUtils.toInt(rowSplit.getValue()),
                        j = h+1,k=i+1;
                sheet.createFreezePane(h,i,j,k);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return wb;
    }
    /**
     * 解决合并单元格后的边框问题
     * @param region 合并单元格区域范围
     * @param sheet
     * @param wb
     */
    private static void setRegionBorder(CellRangeAddress region, HSSFSheet sheet,HSSFWorkbook wb){
      //  RegionUtil.setBorderBottom(BORDER_WIDTH,region, sheet, wb);
      //  RegionUtil.setBorderLeft(BORDER_WIDTH,region, sheet, wb);
//        RegionUtil.setBorderTop(BORDER_WIDTH,region, sheet, wb);
    }
    /**
     * 计算标题行的colspan
     * @param thLs
     * @return
     */
    private static int getColspan(List<Element> thLs){
        int colSpans = 0;
        for (Element ele: thLs) {
            colSpans += NumberUtils.toInt(ele.attributeValue("colspan"), 1);
        }
        return colSpans;
    }

    /**
     * 生成行内容
     * @param tdLs th或者td集合
     * @param rowIndex 行号
     * @param row POI行对象
     * @param startCellIndex 开始索引
     * @param cellStyle 样式
     * @param crossRowEleMetaLs 跨行元数据集合
     * @return
     */
    private static int makeRowCell(HSSFSheet sheet,List<Element> tdLs, int rowIndex, HSSFRow row, int startCellIndex, HSSFCellStyle cellStyle,
                                   List<CrossRangeCellMeta> crossRowEleMetaLs) {
        int i = startCellIndex;
        row.setHeight((short)400);
        for (int eleIndex = 0; eleIndex < tdLs.size(); i++, eleIndex++) {
            int captureCellSize = getCaptureCellSize(rowIndex, i, crossRowEleMetaLs);
            while (captureCellSize > 0) {
                for (int j = 0; j < captureCellSize; j++) {// 当前行跨列处理（补单元格）
                    row.createCell(i);
                    i++;
                }
                captureCellSize = getCaptureCellSize(rowIndex, i, crossRowEleMetaLs);
            }
            Element thEle = tdLs.get(eleIndex);
            String val = thEle.getTextTrim();
            if (StringUtils.isBlank(val)) {
                Element e = thEle.element("a");
                if (e != null) {
                    val = e.getTextTrim();
                }
            }
            HSSFCell c = row.createCell(i);

            HSSFCellStyle cloneStyle = null;
            //设置列宽,据说*2*256这种写法对中文比较好.
            if(sheet != null) {
                cloneStyle = sheet.getWorkbook().createCellStyle();
                cloneStyle.cloneStyleFrom(cellStyle);
                cloneStyle = setBackgroundColor(sheet,thEle,cloneStyle);
//                sheet.setColumnWidth(i, val.getBytes().length * 256);
                //部分标题文字较长，导致列很宽，暂时固定宽度
                sheet.setColumnWidth(i, 5000);
            }
            //所有的数字都当做字符串
//            if (NumberUtils.isNumber(val)) {
//                c.setCellValue(Double.parseDouble(val));
//                c.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
//            } else {
            c.setCellValue(val);
//            }
            c.setCellStyle(cloneStyle == null ? cellStyle : cloneStyle);
            int rowSpan = NumberUtils.toInt(thEle.attributeValue("rowspan"), 1);
            int colSpan = NumberUtils.toInt(thEle.attributeValue("colspan"), 1);
            if (rowSpan > 1 || colSpan > 1) { // 存在跨行或跨列
                crossRowEleMetaLs.add(new CrossRangeCellMeta(rowIndex, i, rowSpan, colSpan));
            }
            if (colSpan > 1) {// 当前行跨列处理（补单元格）
                for (int j = 1; j < colSpan; j++) {
                    i++;
                    row.createCell(i);
                }
            }
        }
        return i;
    }

    /**
     * 自定义背景颜色
     * @param thEle
     * @param cellStyle
     */
    private static HSSFCellStyle setBackgroundColor(HSSFSheet sheet,Element thEle,HSSFCellStyle cellStyle){

        String backgroundColor = thEle.attributeValue("background-color");
        if (StringUtils.isNotEmpty(backgroundColor)) {
            String[] backgroundColorArr = backgroundColor.split(",",-1);
            if(backgroundColorArr.length != 3){
                System.out.println("RGB颜色有错误,将使用默认值!");
            }else {
                byte[] rgb = new byte[3];
                for (int j =0 ;j<backgroundColorArr.length;j++) {
                    try {
                        rgb[j] = (byte)Integer.parseInt(backgroundColorArr[j].trim());
                    }catch (Exception e){
                        System.out.println("RGB颜色有错误!");
                        rgb[j] = -1;
                    }
                }

                //我们需要做的就是用我们自定义RGB将这个颜色替换掉
                cellStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
                cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
                //拿到palette颜色板
                HSSFPalette palette = sheet.getWorkbook().getCustomPalette();
                palette.setColorAtIndex(IndexedColors.LIGHT_YELLOW.getIndex(), rgb[0], rgb[1], rgb[2]);
            }
        }
        return cellStyle;
    }

    /**
     * 获得因rowSpan占据的单元格
     *
     * @param rowIndex 行号
     * @param colIndex 列号
     * @param crossRowEleMetaLs 跨行列元数据
     * @return 当前行在某列需要占据单元格
     */
    private static int getCaptureCellSize(int rowIndex, int colIndex, List<CrossRangeCellMeta> crossRowEleMetaLs) {
        int captureCellSize = 0;
        for (CrossRangeCellMeta crossRangeCellMeta : crossRowEleMetaLs) {
            if (crossRangeCellMeta.getFirstRow() < rowIndex && crossRangeCellMeta.getLastRow() >= rowIndex) {
                if (crossRangeCellMeta.getFirstCol() <= colIndex && crossRangeCellMeta.getLastCol() >= colIndex) {
                    captureCellSize = crossRangeCellMeta.getLastCol() - colIndex + 1;
                }
            }
        }
        return captureCellSize;
    }

    /**
     * 获得标题样式
     *
     * @param workbook
     * @return
     */
    @SuppressWarnings("deprecation")
    private static HSSFCellStyle getTitleStyle(HSSFWorkbook workbook) {
        short titlebackgroundcolor = HSSFColor.GREY_25_PERCENT.index;
        HSSFCellStyle style = workbook.createCellStyle();
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//横向居中
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);//竖向居中
        //上下左右边框均为1
        style.setBorderBottom(BORDER_WIDTH);
        style.setBorderTop(BORDER_WIDTH);
        style.setBorderLeft(BORDER_WIDTH);
        style.setBorderRight(BORDER_WIDTH);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setFillForegroundColor(titlebackgroundcolor);// 背景色

        //设置字体
        HSSFFont font = workbook.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short)10);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        style.setFont(font);
        return style;
    }

    /**
     * 获得内容样式
     *
     * @param wb
     * @return
     */
    @SuppressWarnings("deprecation")
    private static HSSFCellStyle getContentStyle(HSSFWorkbook wb) {
        HSSFCellStyle style = wb.createCellStyle();
        style.setBorderBottom(BORDER_WIDTH);
        style.setBorderTop(BORDER_WIDTH);
        style.setBorderLeft(BORDER_WIDTH);
        style.setBorderRight(BORDER_WIDTH);

        HSSFFont font = wb.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        return style;
    }
}
