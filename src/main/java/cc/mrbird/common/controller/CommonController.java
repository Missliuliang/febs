package cc.mrbird.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

@Controller
public class CommonController {

//    public  static  void main(String[] arge){
//        String aa="2111111132_3222";
//        System.out.println(aa.substring(aa.indexOf("_")+1));
//    }

    @RequestMapping("common/download")
    public void fileDownLoad(String fileName , Boolean delete , HttpServletResponse response){
        String realFileName=System.currentTimeMillis()+fileName.substring(fileName.indexOf("_")+1);
        try{
            response.setCharacterEncoding("utf-8");
            response.setContentType("multipart/form-data");
            response.setHeader("Content-Disposition",
                    "attachment;fileName=" + java.net.URLEncoder.encode(realFileName, "utf-8"));
            String path ="file/"+fileName;
            File file=new File(path);
            InputStream fileInputStream = new FileInputStream(file);
            OutputStream outputStream = response.getOutputStream();
            byte[] bytes = new byte[1024];
            int read ;
            while (  (read= fileInputStream.read(bytes)) > 0  ){
                outputStream.write(bytes,0,read);
            }
            outputStream.close();;
            fileInputStream.close();
            if(delete && file.exists()) file.delete();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
