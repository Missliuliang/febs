package cc.mrbird.system.service.impl;

import cc.mrbird.common.service.impl.BaseService;
import cc.mrbird.system.service.UserRoleService;
import cc.mrbird.system.domain.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional(propagation = Propagation.SUPPORTS  ,isolation = Isolation.DEFAULT ,readOnly = true ,rollbackFor = Exception.class)
public class UserRoleServiceImpl extends BaseService<UserRole> implements UserRoleService {
    @Override
    @Transactional
    public void deleteUserRoleByRoleId(String roleId) {
        List<String> list = Arrays.asList(roleId.split(","));
        this.batchDelete(list, "roleId",UserRole.class);
    }

    @Override
    @Transactional
    public void deleteUserRoleByUserid(String userId) {

        List<String> list = Arrays.asList(userId.split(","));
        this.batchDelete(list, "userId",UserRole.class);

    }
}
