package cc.mrbird.common.util;

import cc.mrbird.common.domain.ResponseBo;
import cc.mrbird.common.poi.utils.ExcelUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtils {


    public static  String makeFileName(String fileName){
        return UUID.randomUUID()+"_"+fileName;
    }

    //编译给定正则表达式并尝试将给定输入与其匹配。
    //去除特殊符号
    public static  String stringFilter(String fileName){
        String regex= "[`~!@#$%^&*+=|{}':; ',//[//]<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern pattern= Pattern.compile(regex);
        Matcher matcher = pattern.matcher(fileName);
        return matcher.replaceAll("").trim();
    }

    /**
     * 生成Excel文件
     *
     * @param filename 文件名称
     * @param list     文件内容List
     * @param clazz    List中的对象类型
     * @return ResponseBo
     */
    public static ResponseBo createExcelByPoiKit(String fileName , List<?> list ,Class clazz){
        if (list.isEmpty()){
            return ResponseBo.warn("导出数据为空！");
        }else {
            boolean operateSgin=false;
            String newfileName=makeFileName(fileName+".xlsx");
            try {
                File dirFile=new File("file");
                if (!dirFile.exists()){
                    dirFile.mkdir();
                    String path="file/"+newfileName;
                    FileOutputStream outputStream=new FileOutputStream(path);
                    operateSgin = ExcelUtils.builder(clazz).toExcel(list,"查询结果",outputStream);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            if (operateSgin){
                return  ResponseBo.ok(fileName);
            }else {
                return  ResponseBo.error("导出Excel失败，请联系网站管理员！");
            }
        }
    }
    public static ResponseBo createCsv(String fileName ,List<?> data ,Class<?> clazz){
         if (data.isEmpty()){
             return  ResponseBo.warn("导出数据为空！");
         } else {
             boolean flag=false;
             String newfileName= makeFileName(fileName+".csv");
             File file=new File("file");
             if (!file.exists()){
                 file.mkdir();
                 String path="file/"+newfileName;
                 flag=ExcelUtils.builder(clazz).toCvs(data,path);
             }
             if (flag){
                 return ResponseBo.ok(fileName);
             }else {
                 return ResponseBo.error("导出Csv失败，请联系网站管理员！");
             }
         }
    }



    public static void main(String[] args) {
        System.out.printf(stringFilter("12@#!56"));
    }
}
