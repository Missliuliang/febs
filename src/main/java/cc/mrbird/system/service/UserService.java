package cc.mrbird.system.service;

import cc.mrbird.common.service.IService;
import cc.mrbird.system.domain.User;
import cc.mrbird.system.domain.UserWithRole;

import java.util.List;

public interface UserService extends IService<User> {

    UserWithRole findById(Long userId);

    User findByName(String name);

    List<User> findUserWithDept(User user);

    void registUser(User user);

    void updateTheme(String userName , String theme);

    void addUser(User user ,Long[] role);

    void updateUser(User u , Long[] role);

    void deleteUser(String userId);

    void updateLoginTime(String  userName);

    void updatePassword(String password);

    User findUserProfile(User user);

    void updateUserProfile(User user);



}
