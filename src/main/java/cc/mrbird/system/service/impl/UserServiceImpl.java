package cc.mrbird.system.service.impl;

import cc.mrbird.common.service.impl.BaseService;
import cc.mrbird.common.util.MD5Utils;
import cc.mrbird.system.dao.UserMapper;
import cc.mrbird.system.dao.UserRoleMapper;
import cc.mrbird.system.domain.Role;
import cc.mrbird.system.domain.User;
import cc.mrbird.system.domain.UserRole;
import cc.mrbird.system.domain.UserWithRole;
import cc.mrbird.system.service.UserRoleService;
import cc.mrbird.system.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.security.auth.Subject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.DEFAULT , readOnly = true ,rollbackFor = Exception.class)
public class UserServiceImpl extends BaseService<User> implements UserService {

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
        Example example=new Example(User.class);
        example.createCriteria().andCondition("lower(username)=",name.toLowerCase());
        List<User> users = userMapper.selectByExample(example);
        if (users.size()==0){
            return null;
        }else {
            return users.get(0);
        }
    }

    @Override
    public List<User> findUserWithDept(User user) {
        try{
            return userMapper.findUserWithDept(user);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public void registUser(User user) {
        user.setCrateTime(new Date());
        user.setTheme(User.DEFAULT_THEME);
        user.setAvatar(User.DEFAULT_AVATAR);
        user.setSsex(User.SEX_UNKNOW);
        user.setPassword(MD5Utils.encrypt(user.getUsername().toLowerCase(),user.getPassword()));
        this.save(user);
        UserRole ur=new UserRole();
        ur.setUserId(user.getUserId());
        ur.setRoleId(3L);
        this.userRoleMapper.insert(ur);
    }

    @Override
    public void updateTheme(String userName, String theme) {
        Example example=new Example(User.class);
        example.createCriteria().andCondition("username=" ,userName);
        User user=new User();
        user.setTheme(theme);
        userMapper.updateByExampleSelective(user,example);
    }

    @Override
    public void addUser(User user, Long[] role) {
        user.setTheme(User.DEFAULT_THEME);
        user.setAvatar(User.DEFAULT_AVATAR);
        user.setCrateTime(new Date());
        user.setPassword(MD5Utils.encrypt(user.getUsername().toLowerCase(),user.getPassword()));
        this.save(user);
        for (Long roleId:role
             ) {
            UserRole ur=new UserRole();
            ur.setRoleId(roleId);
            ur.setUserId(user.getUserId());
            userRoleMapper.insert(ur);
        }

    }

    @Override
    public void updateUser(User u, Long[] role) {
        u.setPassword(null);
        u.setUsername(null);
        u.setModifyTime(new Date());
        this.updateNotNull(u);
        Example example=new Example(User.class);
        example.createCriteria().andCondition("user_id=",u.getUserId());
        this.userMapper.deleteByExample(example);
        for (Long r:role
             ) {
            UserRole ur=new UserRole();
            ur.setRoleId(r);
            ur.setUserId(u.getUserId());
            this.userRoleMapper.insert(ur);
        }

    }

    @Override
    public void deleteUser(String userId) {
        List<String> list = Arrays.asList(userId.split(","));
        this.batchDelete(list,"userId",User.class);
        this.userRoleService.deleteUserRoleByRoleId(userId);
    }

    @Override
    public void updateLoginTime(String userName) {
        Example example=new Example(User.class);
        example.createCriteria().andCondition("lower(username)=", userName.toLowerCase());
        User u=new User();
        u.setLastLoginTime(new Date());
        this.userMapper.updateByExampleSelective(u,example);
    }

    @Override
    public void updatePassword(String password) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        Example example=new Example(User.class);
        example.createCriteria().andCondition("lower(username)=", user.getUsername().toLowerCase());
        String  newPwd=MD5Utils.encrypt(user.getUsername().toLowerCase(),password);
        user.setPassword(newPwd);
        this.userMapper.updateByExampleSelective(user,example);


    }

    @Override
    public User findUserProfile(User user) {
        return this.userMapper.findUserProfile(user);
    }

    @Override
    public void updateUserProfile(User user) {
        user.setPassword(null);;
        user.setUsername(null);
        if (user.getDeptId() ==null){
            user.setDeptId(0L);
            this.updateNotNull(user);
        }

    }
}
