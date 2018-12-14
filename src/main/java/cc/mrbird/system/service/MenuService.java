package cc.mrbird.system.service;

import cc.mrbird.common.domain.Tree;
import cc.mrbird.common.service.IService;
import cc.mrbird.system.domain.Menu;

import java.util.List;

public interface MenuService extends IService<Menu> {

    List<Menu> findUserPermissions(String userName);

    List<Menu> findUserMenus(String userName);

    Tree<Menu>  getUserMenu(String userName);

    List<Menu> findAllMenus(Menu menu);

    Tree<Menu> getMenuButtonTree();

    Tree<Menu> getMenuTree();

    Menu findById(long menuId);

    Menu findByNameAndType(String menuName , String type);

    void addMenu(Menu menu);

    void updateMenu(Menu menu);

    void deleteMenu(String menuId);





}
