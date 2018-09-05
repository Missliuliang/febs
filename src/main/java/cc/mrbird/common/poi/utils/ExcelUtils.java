package cc.mrbird.common.poi.utils;

import cc.mrbird.common.poi.convert.ExportConvert;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class ExcelUtils {

    private  Class<?> mClass =null ;

    private HttpServletResponse mResponse;

    private Integer mMaxSheetRecords=10000;

    private Map<String , ExportConvert> mConvertInstanceCache =new HashMap<>();

}
