package cc.mrbird.common.shiro;

import cc.mrbird.system.domain.Menu;
import cc.mrbird.system.domain.Role;
import cc.mrbird.system.domain.User;
import cc.mrbird.system.service.MenuService;
import cc.mrbird.system.service.RoleMenuService;
import cc.mrbird.system.service.RoleService;
import cc.mrbird.system.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ShiroRealm  extends AuthorizingRealm {

    @Autowired
    UserService userService;
    @Autowired
    MenuService menuService;
    @Autowired
    RoleService roleService;



    /**
     * 授权模块，获取用户角色和权限
     * @param principal principal
     * @return AuthorizationInfo 权限信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
       User user = (User) SecurityUtils.getSubject().getPrincipal();
        String username = user.getUsername();
        SimpleAuthorizationInfo info =new SimpleAuthorizationInfo();
        // 获取用户角色集
        List<Role> roleList = roleService.findUserRole(username);
        //转化成set
        Set<String> collect = roleList.stream().map(Role::getRoleName).collect(Collectors.toSet());
        info.setRoles(collect);

        List<Menu> userPermissions = menuService.findUserPermissions(username);
        Set<String> permissionSet =new HashSet<>();
        for (Menu m:userPermissions) {
            permissionSet.add(m.getPerms());
            
        }
        info.setStringPermissions(permissionSet);
        return  info;

    }

    /**
     * 用户认证
     * @param token AuthenticationToken 身份认证 token
     * @return AuthenticationInfo 身份认证信息
     * @throws AuthenticationException 认证相关异常
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        String username = (String) token.getPrincipal();
        String password = new String((char[]) token.getCredentials());
        User user = userService.findByName(username);
        if (user==null) {
            throw new UnknownAccountException("用户名或密码错误！");
        }
        if (!user.getPassword().equals(password)){
            throw new IncorrectCredentialsException("用户名或密码错误！");

        }
        if (User.STATUS_LOCK.equals(user.getStatus())){
            throw new LockedAccountException("账号已被锁定,请联系管理员！");
        }
        SimpleAuthenticationInfo info =new SimpleAuthenticationInfo(user,password,super.getName());
        return info;
    }
}
