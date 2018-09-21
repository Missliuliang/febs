package cc.mrbird.common.poi.utils;

import cc.mrbird.common.annotation.ExportConfig;
import cc.mrbird.common.handler.ExportHandler;
import cc.mrbird.common.poi.convert.ExportConvert;
import cc.mrbird.common.poi.pojo.ExportItem;
import com.csvreader.CsvWriter;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelUtils {

    private  Class<?> mClass =null ;

    private HttpServletResponse mResponse;

    // 分Sheet机制：每个Sheet最多多少条数据
    private Integer mMaxSheetRecords=10000;

    //缓存数据格式器实例,避免多次使用反射进行实例化
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

    private String convertValue(Object oldValue ,String format){
        try {
            String  protocol=format.split(":")[0];
            if ("s".equalsIgnoreCase(protocol)){
                String[] pattern= format.split(":")[1].split(",");
                for (String p:pattern){
                    String[] cp= p.split("=");
                    if (cp[0].equals(oldValue)) return cp[1];
                }
            }
            if ("c".equalsIgnoreCase(protocol)){
                String clazz=protocol.split(":")[1];
                ExportConvert exportConvert=mConvertInstanceCache.get(clazz);
                if (exportConvert==null){
                    exportConvert=(ExportConvert) Class.forName(clazz).newInstance();
                    mConvertInstanceCache.put(clazz,exportConvert);
                }
                if (mConvertInstanceCache.size()>10){
                    mConvertInstanceCache.clear();
                    return  exportConvert.handler(oldValue);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return  String.valueOf(oldValue);
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
        // 导出列查询。
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
        // 创建新的工作薄。
        SXSSFWorkbook sxssfWorkbook =new SXSSFWorkbook();
        double sheetNo = Math.ceil(data.size() / mMaxSheetRecords);// 取出一共有多少个sheet.
        // =====多sheet生成填充数据=====
        int index=0;
        while (index<=(sheetNo==0.0 ? sheetNo: sheetNo-1)){
            SXSSFSheet sheet =POIUtils.sxssfSheet(sxssfWorkbook ,sheetName+(index==0?"":"_"+index));
            // 创建表头
            SXSSFRow headRow=POIUtils.sxssfRow(sheet,0);
            for (int i = 0; i <exportItemList.size() ; i++) {
                SXSSFCell cell = POIUtils.sxssfCell(headRow, i);
                POIUtils.setColumnWidth(sheet,i,exportItemList.get(i).getWidth(),exportItemList.get(i).getDisplay());
                cell.setCellValue(exportItemList.get(i).getDisplay());
                CellStyle cellStyle = exportHandler.headCellStyle(sxssfWorkbook);
                if (cellStyle !=null ){
                    cell.setCellStyle(cellStyle);
                }
            }
            SXSSFRow bodyRow;
            String cellValue;
            SXSSFCell bodyCell;
            CellStyle style=sxssfWorkbook.createCellStyle();
            Font font = sxssfWorkbook.createFont();
            style.setFont(font);

            // 产生数据行
            if (data.size()>0){
                int startNo= index* mMaxSheetRecords;
                int endNo =Math.min(startNo + mMaxSheetRecords , data.size());
                int i =startNo;
                while (i<endNo){
                    bodyRow=POIUtils.sxssfRow(sheet,i+1-startNo);
                    for (int j = 0; j < exportItemList.size(); j++) {
                        cellValue=exportItemList.get(j).getReplace();
                        if ("".equals(cellValue)){
                            try{
                                cellValue= BeanUtils.getProperty(data.get(i) ,exportItemList.get(j).field);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        if (!"".equals(exportItemList.get(j).getConvert())){
                            cellValue=convertValue(cellValue,exportItemList.get(j).getConvert());
                        }
                        POIUtils.setColumnWidth(sheet,j,exportItemList.get(j).getWidth(),cellValue);
                        SXSSFCell sxssfCell = POIUtils.sxssfCell(bodyRow, j);
                        sxssfCell.setCellValue("".equals(cellValue)?null:cellValue);
                        sxssfCell.setCellStyle(style);
                    }
                    i++;
                }
            }
            index++;
        }
        try{
            POIUtils.writeByLocalOrBrowser(mResponse,exportHandler.exportFileName(sheetName),sxssfWorkbook,out);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }



    public boolean toCvs(List<?> data ,String path){
        try {
            required_BuilderParams();
            if (data==null || data.size()<0)return  false;

            ExportConfig exportConfig;
            ExportItem exportItem=null;
            List<ExportItem> list=new ArrayList<>();
            for (Field field: mClass.getDeclaredFields()) {
                exportConfig = field.getAnnotation(ExportConfig.class);
                if (exportConfig!=null){
                    exportItem=new ExportItem();
                    exportItem.setField(field.getName());
                    exportItem.setDisplay("field".equals(exportConfig.value())?field.getName():exportConfig.value());
                    exportItem.setConvert(exportConfig.convert());
                    exportItem.setReplace(exportConfig.replace());
                    list.add(exportItem);
                }
            }
            String cellValue = null;
            FileOutputStream outputStream=new FileOutputStream(path);
            outputStream.write(new byte[]{ (byte) 0xEF, (byte) 0xBB, (byte) 0xBF  });
            CsvWriter csvWriter = new CsvWriter(outputStream, ',', Charset.forName("utf-8"));
            String[] toArray = list.stream().map(ExportItem::getDisplay).toArray(String[]::new);
            csvWriter.writeRecord(toArray);
            for (Object  adata:data) {
                List<Object> csvContent=new ArrayList<>();
                for (ExportItem exportItem1 : list
                     ) {
                    cellValue=exportItem1.getReplace();
                    if (!StringUtils.isNotBlank(cellValue)){
                        try {
                            cellValue=BeanUtils.getProperty(adata,exportItem.getField());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    if (!StringUtils.isNotBlank(exportItem.getConvert())){
                        cellValue=convertValue(cellValue,exportItem.getConvert());
                    }
                    csvContent.add(cellValue);
                }
                String[] array = csvContent.toArray(new String[0]);
                csvWriter.writeRecord(array);
            }
            csvWriter.close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return  false;
        }
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
