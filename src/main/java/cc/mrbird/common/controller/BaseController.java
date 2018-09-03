package cc.mrbird.common.controller;

import cc.mrbird.system.domain.User;
import com.github.pagehelper.PageInfo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import java.util.HashMap;
import java.util.Map;

public class BaseController  {

    public Map<String ,Object> getDataTable(PageInfo<?> pageInfo){
        Map<String ,Object> data =new HashMap<>();
        data.put("rows" ,pageInfo.getList());
        data.put("total" , pageInfo.getTotal());
        return  data;
    }

    public static Subject getSubject(){
        return SecurityUtils.getSubject();
    }

    public static User currentUser(){
        return (User) getSubject().getPrincipal();
    }

    public static Session getSession(){
        return getSubject().getSession();
    }

    public static  Session getSession(Boolean flag){
        return  getSubject().getSession(flag);
    }
    public static  void login(AuthenticationToken token){
        getSubject().login(token);
    }
}
