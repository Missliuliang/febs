package cc.mrbird.system.service.impl;

import cc.mrbird.common.service.impl.BaseServiceImpl;
import cc.mrbird.system.dao.UserMapper;
import cc.mrbird.system.dao.UserRoleMapper;
import cc.mrbird.system.domain.Role;
import cc.mrbird.system.domain.User;
import cc.mrbird.system.domain.UserWithRole;
import cc.mrbird.system.service.UserRoleService;
import cc.mrbird.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.DEFAULT , readOnly = true ,rollbackFor = Exception.class)
public class UserServiceImpl extends BaseServiceImpl<User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private UserRoleService userRoleService;

    @Override
    public UserWithRole findById(Long userId) {
       List<UserWithRole> list= userMapper.findUserWithRole (userId);
       List<Long> roleList=new ArrayList<> ();
       list.forEach (userWithRole -> {
           roleList.add (userWithRole.getRoleId ());
       });
       if(list.size ()==0) return  null;
       UserWithRole uwr= list.get (0);
       uwr.setRoleIds (roleList);
       return  uwr;
    }

    @Override
    public User findByName(String name) {
        return null;
    }

    @Override
    public List<User> findUserWithDept(User user) {
        return null;
    }

    @Override
    public void registUser(User user) {

    }

    @Override
    public void updateTheme(String userName, String theme) {

    }

    @Override
    public void addUser(User user, Long[] role) {

    }

    @Override
    public void updateUser(User u, Long[] role) {

    }

    @Override
    public void deleteUser(String userId) {

    }

    @Override
    public void updateLoginTime(String userName) {

    }

    @Override
    public void updatePassword(String password) {

    }

    @Override
    public User findUserProfile(User user) {
        return null;
    }

    @Override
    public void updateUserProfile(User user) {

    }
}
