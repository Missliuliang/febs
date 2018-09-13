package cc.mrbird.system.service.impl;

import cc.mrbird.common.service.impl.BaseService;
import cc.mrbird.system.dao.RoleMapper;
import cc.mrbird.system.dao.RoleMenuMapper;
import cc.mrbird.system.dao.UserRoleMapper;
import cc.mrbird.system.domain.Role;
import cc.mrbird.system.domain.RoleMenu;
import cc.mrbird.system.domain.RoleWithMenu;
import cc.mrbird.system.service.RoleMenuService;
import cc.mrbird.system.service.RoleService;
import cc.mrbird.system.service.UserRoleService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
public class RoleServiceImpl extends BaseService<Role> implements RoleService {

    @Autowired
    RoleMapper roleMapper;
    @Autowired
    RoleMenuMapper roleMenuMapper;
    @Autowired
    UserRoleService userRoleService;
    @Autowired
    RoleMenuService roleMenuService;


    @Override
    public List<Role> findUserRole(String userName) {
        return roleMapper.findUserRole(userName);
    }

    @Override
    public List<Role> findAllRole(Role role) {
        try {
            Example example = new Example(Role.class);
            if (StringUtils.isNotBlank(role.getRoleName())) {
                example.createCriteria().andCondition("role_name=", role.getRoleName());
            }
            example.setOrderByClause("create_time");
            return this.selectByExample(example);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public RoleWithMenu findRoleWithMenu(Long roleId) {
        List<RoleWithMenu> roleWithMenuList = roleMapper.findById(roleId);
        List<Long> menulist =new ArrayList<>();
        roleWithMenuList.forEach(roleMenu->{
            menulist.add(roleMenu.getMenuId());
        });
        if (roleWithMenuList.size() ==0)return  null ;
        RoleWithMenu roleWithMenu = roleWithMenuList.get(0);
        roleWithMenu.setMenuIds(menulist);
        return roleWithMenu;
    }

    @Override
    public Role findByName(String roleName) {
        Example example = new Example(Role.class);
        example.createCriteria().andCondition("role_name=", roleName.toLowerCase());
        List<Role> roles = this.selectByExample(example);
        return roles.size() == 0 ? null : roles.get(0);
    }

    //保存role和menu 的id
    private void setRoleMenus(Role role , Long[] menuIds){
        Arrays.stream(menuIds).forEach( menuId->{
            RoleMenu roleMenu =new RoleMenu();
            roleMenu.setMenuId(menuId);
            roleMenu.setRoleId(role.getRoleId());
            this.roleMenuMapper.insert(roleMenu);
        });
    }

    @Override
    public void addRole(Role role, Long[] menuIds) {
        role.setCreateTime(new Date());
        this.save(role);
        setRoleMenus(role,menuIds);
    }

    @Override
    public void updateRole(Role role, Long[] menuIds) {
        role.setCreateTime(new Date());
        this.updateNotNull(role);
        Example example=new Example(RoleMenu.class);
        example.createCriteria().andCondition("role_id=",role.getRoleId());
        roleMenuMapper.deleteByExample(example);
        setRoleMenus(role,menuIds);

    }

    @Override
    public void deleteRoles(String roleIds) {
        List<String> list = Arrays.asList(roleIds.split(","));
        this.batchDelete(list,"roleId",Role.class);
        roleMenuService.deleteRoleMenuByMenuId(roleIds);
        roleMenuService.deleteRoleMenuByRoldId(roleIds);
    }
}
