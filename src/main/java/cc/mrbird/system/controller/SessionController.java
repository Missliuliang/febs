package cc.mrbird.system.controller;

import cc.mrbird.common.annotation.Log;
import cc.mrbird.common.domain.ResponseBo;
import cc.mrbird.system.domain.UserOnline;
import cc.mrbird.system.service.SessionService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SessionController {

    @Autowired
    SessionService sessionService;

    @Log("获取在线用户")
    @RequestMapping("session")
    @RequiresPermissions("session:list")
    public String online(){
        return  "system/monitor/online";
    }

    @Log("获取sessionlist")
    @ResponseBody
    @RequestMapping("session/list")
    public Map<String ,Object> list(){
        List<UserOnline> list = sessionService.list();
        Map<String,Object> rspData= new HashMap<>();
        rspData.put("rows",list);
        rspData.put("total",list.size());
        return  rspData;
    }


    @ResponseBody
    @RequiresPermissions("user:kickout")
    @RequestMapping("session/forceLogout")
    public ResponseBo forceLogout(String id){
        try{
            sessionService.forceLogOut(id);
            return  ResponseBo.ok();
        }catch (Exception e){
            e.printStackTrace();
            return  ResponseBo.error("踢出用户");
        }
    }
}
