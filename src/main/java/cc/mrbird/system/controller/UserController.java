package cc.mrbird.system.controller;


import cc.mrbird.common.annotation.Log;
import cc.mrbird.common.controller.BaseController;
import cc.mrbird.common.domain.QueryRequest;
import cc.mrbird.common.domain.ResponseBo;
import cc.mrbird.common.util.FileUtils;
import cc.mrbird.common.util.MD5Utils;
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

    @ResponseBody
    @RequestMapping("/user/excel")
    public ResponseBo userExcel(User user){
        List<User> userWithDept = this.userService.findUserWithDept(user);
        ResponseBo userExcel = FileUtils.createExcelByPoiKit("userExcel", userWithDept, User.class);
        return  userExcel;
    }

    @RequestMapping("/user/csv")
    @ResponseBody
    public ResponseBo userCsv(User user){
        List<User> userWithDept = this.userService.findUserWithDept(user);
        ResponseBo userExcel = FileUtils.createCsv("userCsv",userWithDept,User.class);
        return  userExcel;
    }

    @RequestMapping("/user/regist")
    @ResponseBody
    public ResponseBo registUser(User user){
        User byName = this.userService.findByName(user.getUsername());
        if (byName!=null ){
            return ResponseBo.warn("该用户名已被使用！");
        }
        this.userService.registUser(user);
        return  ResponseBo.ok("用户注册成功");
    }

    @Log("新增用户")
    @RequestMapping("/user/addUser")
    @RequiresPermissions("user:add")
    @ResponseBody
    public ResponseBo  addUser(User user ,Long[] roles){
        if (ON.equalsIgnoreCase(user.getStatus()))
            user.setStatus(User.STATUS_VALID);
        else
            user.setStatus(User.STATUS_LOCK);
            this.userService.addUser(user,roles);
            return ResponseBo.ok("新增用户成功！");
    }

    @Log("修改用户")
    @RequestMapping("/user/update")
    @RequiresPermissions("user:update")
    @ResponseBody
    public ResponseBo updateUser(User user ,Long[] rolesSelect){
        if (ON.equalsIgnoreCase(user.getStatus()))
            user.setStatus(User.STATUS_VALID);
        else
            user.setStatus(User.STATUS_LOCK);
            this.userService.updateUser(user,rolesSelect);
            return ResponseBo.ok("修改用户成功！");
    }

    @Log("删除用户")
    @RequestMapping("/user/delete")
    @RequiresPermissions("user:delete")
    @ResponseBody
    public  ResponseBo deleteUser(String ids){
        this.userService.deleteUser(ids);
        return ResponseBo.ok("删除用户成功！");
    }


    @RequestMapping("user/checkPassword")
    @ResponseBody
    public boolean checkPassword(String password){
        User user = super.currentUser();
        String encrypt = MD5Utils.encrypt(user.getUsername().toLowerCase(), password);
        return user.getPassword().equals(encrypt);
    }



}
