package cc.mrbird.system.service.impl;

import cc.mrbird.common.service.impl.BaseServiceImpl;
import cc.mrbird.system.domain.RoleMenu;
import cc.mrbird.system.service.RoleMenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional(propagation = Propagation.SUPPORTS ,isolation = Isolation.DEFAULT ,rollbackFor = Exception.class)
public class RoleMenuServiceImpl extends BaseServiceImpl<RoleMenu> implements RoleMenuService {
    @Override
    @Transactional
    public void deleteRoleMenuByRoldId(String roleIds) {
        List<String> list = Arrays.asList(roleIds.split(","));
        this.batchDelete(list,"roleId" ,RoleMenu.class);
    }

    @Override
    public void deleteRoleMenuByMenuId(String menuIds) {
        List<String> list = Arrays.asList(menuIds.split(","));
        this.batchDelete(list,"menuIds" ,RoleMenu.class);

    }
}
