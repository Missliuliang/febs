package cc.mrbird.common.poi.utils;

import org.apache.poi.openxml4j.util.ZipFileZipEntrySource;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;
import java.net.URLEncoder;

/**
 * Apache POI SXSS相关API的简易封装
 */
public class POIUtils {

    private  static  final  int mDefaultRowAccessWindowSize=100;

    public static  SXSSFWorkbook sxssfWorkbook(){
        return   newsxssfWorkbook(mDefaultRowAccessWindowSize);
    }


    public  static SXSSFWorkbook newsxssfWorkbook(int rowAccessWindowSize){
        return  new SXSSFWorkbook(rowAccessWindowSize);
    }

    public static SXSSFSheet sxssfSheet(SXSSFWorkbook workbook ,String sheetName){
        return workbook.createSheet(sheetName);
    }

    public  static SXSSFRow sxssfRow(SXSSFSheet sheet , int index){
        return  sheet.createRow(index);
    }

    public static SXSSFCell sxssfCell(SXSSFRow row , int index){
        return  row.createCell(index);
    }

    public static  void setColumnWidth(SXSSFSheet sheet ,int index ,short width ,String value){
        if (width==-1 && !value.equals("") && value!=null){
            sheet.setColumnWidth(index,value.length()*512);
        }
        else {
            width=width==-1?200:width;
            sheet.setColumnWidth(index, (int) (width*37.5));
        }
    }

    /**
     * 设定单元格宽度 (手动/自动)
     *
     * @param sheet
     *            工作薄对象
     * @param index
     *            单元格索引
     * @param width
     *            指定宽度,-1为自适应
     * @param value
     *            自适应需要单元格内容进行计算
     */
    public static  void checkFile(File file){
        if (file==null || !file.exists()){
            throw  new IllegalArgumentException("excel文件不存在.");
        }
        if (!file.getName().endsWith(Constant.XLSX_SUFFIX)) {
            throw new IllegalArgumentException("抱歉,目前ExcelKit仅支持.xlsx格式的文件.");
        }
    }

    public static  void  writeByLocalOrBrowser(HttpServletResponse response , String fileName,
                                               SXSSFWorkbook workbook , OutputStream out)throws  Exception{
        ZipSecureFile.setMinInflateRatio(0L);
        if (response!=null ){
            response.setContentType(Constant.XLSX_CONTENT_TYPE);
            response.setHeader("Content-disposition", "attachment; filename="+
                    URLEncoder.encode(String.format("%s%s",fileName,Constant.XLSX_SUFFIX),"UTF-8"));
            if (out==null){
                out=response.getOutputStream();
            }
        }
        workbook.write(out);
        out.flush();
        out.close();

    }


}
