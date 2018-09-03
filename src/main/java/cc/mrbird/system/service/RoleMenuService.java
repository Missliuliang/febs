package cc.mrbird.system.service;

import cc.mrbird.common.service.IService;
import cc.mrbird.system.domain.RoleMenu;

public interface RoleMenuService  extends IService<RoleMenu> {
    void deleteRoleMenuByRoldId(String roleIds);
    void deleteRoleMenuByMenuId(String menuIds);
}
