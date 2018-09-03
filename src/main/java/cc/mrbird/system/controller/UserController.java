package cc.mrbird.system.controller;


import cc.mrbird.common.annotation.Log;
import cc.mrbird.common.controller.BaseController;
import cc.mrbird.common.domain.QueryRequest;
import cc.mrbird.common.domain.ResponseBo;
import cc.mrbird.system.domain.User;
import cc.mrbird.system.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class UserController  extends BaseController {

    @Autowired
    UserService userService;

    private static final  String ON="on";

    @RequestMapping("user")
    @RequiresPermissions("user:list")
    public String index(Model model){
        User user = super.currentUser();
        model.addAttribute("user" ,user);
        return "system/user/user";
    }

    @RequestMapping("user/getUser")
    @ResponseBody
    public ResponseBo getUser(Long userId){
        try{
            User user=userService.findById(userId);
            return ResponseBo.ok(user);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseBo.error("获取用户信息失败，请联系网站管理员！");
        }

    }

    @Log("获取用户信息")
    @ResponseBody
    @RequestMapping("user/list")
    public Map<String,Object> userList(QueryRequest request ,User user){
        PageHelper.startPage(request.getPageNum(),request.getPageSize());
        List<User> userWithDept = this.userService.findUserWithDept(user);
        PageInfo<User> info =new PageInfo<>(userWithDept);
        return getDataTable(info);
    }
}
