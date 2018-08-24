package cc.mrbird.system;

import cc.mrbird.common.service.IService;
import cc.mrbird.system.domain.UserRole;

public interface UserRoleService extends IService<UserRole> {
    void deleteUserRoleByRoleId(String roleId);

    void deleteUserRoleByUserid(String userId);

}
