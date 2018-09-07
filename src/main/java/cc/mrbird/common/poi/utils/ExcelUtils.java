package cc.mrbird.common.poi.utils;

import cc.mrbird.common.annotation.ExportConfig;
import cc.mrbird.common.handler.ExportHandler;
import cc.mrbird.common.poi.convert.ExportConvert;
import cc.mrbird.common.poi.pojo.ExportItem;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelUtils {

    private  Class<?> mClass =null ;

    private HttpServletResponse mResponse;

    private Integer mMaxSheetRecords=10000;

    private Map<String , ExportConvert> mConvertInstanceCache =new HashMap<>();

    private ExcelUtils(Class<?> clazz){
        this(clazz,null);
    }
    private ExcelUtils(Class<?> clazz ,HttpServletResponse response){
        this.mResponse=response;
        this.mClass=clazz;
    }

    public static ExcelUtils builder(Class<?> clazz){
        return  new ExcelUtils(clazz);
    }

    public static ExcelUtils export(Class<?> clazz ,HttpServletResponse response){
        return  new ExcelUtils(clazz,response);
    }

    public ExcelUtils setMaxSheetRecords(Integer size){
        this.mMaxSheetRecords=size;
        return this;
    }

    private  void required_BuilderParams(){
        if (mClass == null) {
            throw new IllegalArgumentException("请先使用cc.mrbird.util.ExcelUtils.$Builder(Class<?>)构造器初始化参数。");
        }
    }

    private void required_ExportParams(){
        if (mClass==null || mResponse==null){
            throw new IllegalArgumentException(
                    "请先使用cc.mrbird.util.ExcelUtils.$Export(Class<?>, HttpServletResponse)构造器初始化参数。");
        }
    }

    public boolean toExcel(List<?> data, String sheetName){
        required_BuilderParams();
        try{
            return  toExcel(data ,sheetName,mResponse.getOutputStream());
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean toExcel(List<?> data , String sheetName , ExportHandler exportHandler ,OutputStream out){
        required_BuilderParams();
        if (data ==null || data.size()<1) return  false;
        ExportConfig exportConfig;
        ExportItem exportItem;
        List<ExportItem> exportItemList=new ArrayList<>();
        for (Field field: mClass.getDeclaredFields()) {
            exportConfig = field.getAnnotation(ExportConfig.class);
            if (exportConfig !=null ){
                exportItem =new ExportItem();
                exportItem.setField(field.getName());
                exportItem.setDisplay("field".equals(exportConfig.value())?field.getName():exportConfig.value());
                exportItem.setColor(exportConfig.color());
                exportItem.setReplace(exportConfig.replace());
                exportItem.setConvert(exportConfig.convert());
                exportItem.setWidth(exportConfig.width());
                exportItemList.add(exportItem);
            }
        }
        SXSSFWorkbook sxssfWorkbook =new SXSSFWorkbook();
        double sheetNo = Math.ceil(data.size() / mMaxSheetRecords);// 取出一共有多少个sheet.
        int index=0;
        while (index<=(sheetNo==0.0 ? sheetNo: sheetNo-1)){
            SXSSFSheet sheet =POIUtils.sxssfSheet(sxssfWorkbook ,sheetName+(index==0?"":"_"+index));
            SXSSFRow headRow=POIUtils.sxssfRow(sheet,0);
            for (int i = 0; i <exportItemList.size() ; i++) {
                SXSSFCell cell = POIUtils.sxssfCell(headRow, i);
                POIUtils.setColumnWidth(sheet,i,exportItemList.get(i).getWidth(),exportItemList.get(i).getDisplay());
                cell.setCellValue(exportItemList.get(i).getDisplay());
                CellStyle cellStyle = exportHandler.headCellStyle(sxssfWorkbook);
            }
        }



        return true;
    }

    public boolean toExcel(List<?> data , String sheetName , OutputStream out){
        return  toExcel(data, sheetName,new ExportHandler(){
            @Override
            public CellStyle headCellStyle(SXSSFWorkbook sxssfWorkbook) {
                CellStyle cellStyle = sxssfWorkbook.createCellStyle();
                Font font = sxssfWorkbook.createFont();
                cellStyle.setFillBackgroundColor((short) 12);
                cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
                cellStyle.setBorderTop(CellStyle.BORDER_THIN);
                cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
                cellStyle.setAlignment(CellStyle.ALIGN_LEFT);
                cellStyle.setFillBackgroundColor(HSSFColor.BLACK.index);
                cellStyle.setFillBackgroundColor(HSSFColor.GREEN.index);
                font.setBoldweight(Font.BOLDWEIGHT_BOLD);
                font.setColor(HSSFColor.BLACK.index);
                cellStyle.setFont(font);
                return cellStyle;
            }

            @Override
            public String exportFileName(String sheetName) {
                return String.format("Export-%s-%s" ,sheetName ,System.currentTimeMillis());
            }
        } ,out );
    }

}
