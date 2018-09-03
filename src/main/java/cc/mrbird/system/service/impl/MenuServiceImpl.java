package cc.mrbird.system.service.impl;

import cc.mrbird.common.domain.Tree;
import cc.mrbird.common.service.impl.BaseServiceImpl;
import cc.mrbird.system.dao.MenuMapper;
import cc.mrbird.system.domain.Menu;
import cc.mrbird.system.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(propagation = Propagation.SUPPORTS ,isolation = Isolation.DEFAULT ,readOnly = true ,rollbackFor = Exception.class)
public class MenuServiceImpl extends BaseServiceImpl<Menu> implements MenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Override
    public List<Menu> findUserPermissions(String userName) {
        return menuMapper.findUserPermissions(userName);
    }

    @Override
    public List<Menu> findUserMenus(String userName) {
        return menuMapper.findUserMenus(userName);
    }

    @Override
    public List<Menu> getUserMenu(String userName) {
        List<Tree<Menu>> trees=new ArrayList<>();
        List<Menu> userMenus = this.findUserMenus(userName);
        userMenus.forEach(menu -> {
            Tree<Menu> tree =new Tree<>();
            tree.setId(menu.getMenuId().toString());
            tree.setParentId(menu.getParentId().toString());
            tree.setText(menu.getMenuName());
            tree.setUrl(menu.getUrl());
            tree.setIcon(menu.getIcon());
            trees.add(tree);
        });

        return null;
    }

    @Override
    public List<Menu> findAllMenus(Menu menu) {
        return null;
    }

    @Override
    public Tree<Menu> getMenuButtonTree() {
        return null;
    }

    @Override
    public Tree<Menu> getMenuTree() {
        return null;
    }

    @Override
    public Menu findById(long menuId) {
        return null;
    }

    @Override
    public Menu findByNameAndType(String menuName, String type) {
        return null;
    }

    @Override
    public void addMenu(Menu menu) {

    }

    @Override
    public void updateMenu(Menu menu) {

    }

    @Override
    public void deleteMenu(String menuId) {

    }
}
