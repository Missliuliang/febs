package cc.mrbird.system.controller;

import cc.mrbird.common.annotation.Log;
import cc.mrbird.common.controller.BaseController;
import cc.mrbird.common.domain.QueryRequest;
import cc.mrbird.common.domain.ResponseBo;
import cc.mrbird.common.util.FileUtils;
import cc.mrbird.system.domain.SysLog;
import cc.mrbird.system.service.LogService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class LogController extends BaseController  {

    @Autowired
    private LogService logService;

    @RequestMapping("log")
    @RequiresPermissions("log:list")
    public String index(){
        return  "system/log/log";
    }

    @RequestMapping("log/list")
    @ResponseBody
    public Map<String ,Object> logList(QueryRequest request , SysLog log){
        PageHelper.startPage(request.getPageNum(),request.getPageSize());
        List<SysLog> allLogs = this.logService.findAllLogs(log);
        PageInfo<SysLog> pageInfo=new PageInfo<>(allLogs);
        return  getDataTable(pageInfo);
    }


    @RequestMapping("log/excel")
    @ResponseBody
    public ResponseBo logExcel(SysLog log){
        try{
            List<SysLog> allLogs = this.logService.findAllLogs(log);
            return FileUtils.createExcelByPoiKit("日志表",allLogs,SysLog.class);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseBo.error("导出日志失败");
        }
    }


    @RequestMapping("log/csv")
    @ResponseBody
    public ResponseBo logCsv(SysLog log){
        try{
            List<SysLog> allLogs = this.logService.findAllLogs(log);
            return FileUtils.createCsv("日志表",allLogs,SysLog.class);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseBo.error("导出日志失败");
        }
    }

    @RequestMapping("log/delete")
    @ResponseBody
    public ResponseBo delete(String logIds){
        try {
            this.logService.deleteLogs(logIds);
            return ResponseBo.ok();
        }catch (Exception e){
            e.printStackTrace();
            return  ResponseBo.error("删除失败");
        }
    }

}


