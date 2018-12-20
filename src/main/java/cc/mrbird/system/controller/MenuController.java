package cc.mrbird.system.controller;

import cc.mrbird.common.annotation.Log;
import cc.mrbird.common.controller.BaseController;
import cc.mrbird.common.domain.ResponseBo;
import cc.mrbird.common.domain.Tree;
import cc.mrbird.common.util.FileUtils;
import cc.mrbird.system.domain.Menu;
import cc.mrbird.system.service.MenuService;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class MenuController extends BaseController {

    @Autowired
    MenuService menuService;

    @Log()
    @RequestMapping(value = "/menu")
    @RequiresPermissions("menu:list")
    public String index(){
        return "system/menu/menu";
    }

    @RequestMapping("/menu/menu")
    @ResponseBody
    public ResponseBo getMenu(String userName){
        try{
            List<Menu> userMenus = this.menuService.findUserMenus(userName);
            return  ResponseBo.ok(userMenus);
        }catch (Exception e){
            return ResponseBo.error(e.getMessage());
        }
    }


    @RequestMapping("/menu/getMenu")
    public ResponseBo getMenu(long menuId){
        try{
            Menu menu = this.menuService.findById(menuId);
            return ResponseBo.ok(menu);
        }catch (Exception e){
            e.printStackTrace();
            return  ResponseBo.error("获取信息失败，请联系网站管理员！");
        }
    }

    @RequestMapping("/menu/getMenuButtonTree")
    @ResponseBody
    public ResponseBo getMenuButtonTree(){
        try {
            Tree<Menu> menuButtonTree = this.menuService.getMenuButtonTree();
            return ResponseBo.ok(menuButtonTree);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseBo.error(e.getMessage());
        }
    }

    @RequestMapping("/menu/tree")
    @ResponseBody
    public ResponseBo getTree(){
        try {
            Tree<Menu> menuTree = this.menuService.getMenuTree();
            return ResponseBo.ok(menuTree);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseBo.error("获取菜单列表失败！");
        }
    }

    @RequestMapping("getUserMenu")
    @ResponseBody
    public ResponseBo getUserMenu(String userName){
        try {
            Tree<Menu> userMenu = this.menuService.getUserMenu(userName);
            return  ResponseBo.ok(userMenu);
        }catch (Exception e){
            e.printStackTrace();
            return  ResponseBo.error("\"获取用户菜单失败！\"");
        }
    }

    @ResponseBody
    @RequestMapping("/menu/list")
    public ResponseBo menuList(Menu menu){
        try{
            return ResponseBo.ok(this.menuService.findAllMenus(menu)) ;
        }catch (Exception e){
            return  ResponseBo.error(e.getMessage());
        }
    }

    @RequestMapping("/menu/excel")
    @ResponseBody
    public ResponseBo menuExcel(Menu menu){
        try {
            List<Menu> allMenus = this.menuService.findAllMenus(menu);
            return FileUtils.createExcelByPoiKit("菜单表",allMenus,Menu.class);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseBo.error("导出excel失败");
        }
    }

    @RequestMapping("/menu/csv")
    @ResponseBody
    public ResponseBo menuCsv(Menu menu){
        try {
            List<Menu> allMenus = this.menuService.findAllMenus(menu);
            return  FileUtils.createCsv("菜单表",allMenus,Menu.class);
       }catch (Exception e){
            e.printStackTrace();
            return ResponseBo.error("导出csv失败");
        }
    }


    @RequestMapping("menu/checkMenuName")
    public boolean checkMenuName(String menuName , String type ,String oldMenuName){
        if (StringUtils.isNotBlank(menuName) && menuName.equalsIgnoreCase(oldMenuName)){
            return  true;
        }
        Menu nameAndType = this.menuService.findByNameAndType(menuName, type);
        if (nameAndType==null){
            return true;
        }
            return false;
    }

    @Log("添加菜单-菜单-按钮")
    @RequiresPermissions("menu:add")
    @RequestMapping("menu/add")
    @ResponseBody
    public ResponseBo addMenu(Menu menu){
        String name="";
        if (Menu.TYPE_MENU.equals(menu.getType())){
            name="菜单";
        }else {
            name="按钮";
        }
        try{
            this.menuService.addMenu(menu);
            return ResponseBo.ok("添加"+name+"成功");
        }catch (Exception e){
            e.printStackTrace();
            return ResponseBo.error("添加"+name+"失败");
        }

    }

    @Log("删除菜单")
    @RequiresPermissions("menu:delete")
    @RequestMapping("menu/delete")
    @ResponseBody
    public ResponseBo deleteMenu(String ids){
        try {
            this.menuService.deleteMenu(ids);
            return ResponseBo.ok("删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return  ResponseBo.error("删除失败");
        }
    }

    @Log("修改菜单")
    @RequiresPermissions("menu:update")
    @RequestMapping("menu/update")
    @ResponseBody
    public ResponseBo updateMenu(Menu menu){
        String name="";
        if (Menu.TYPE_MENU.equals(menu.getType())){
            name="菜单";
        }else {
            name="按钮";
        }
        try {
            this.menuService.updateMenu(menu);
            return  ResponseBo.ok("修改"+name+"成功");
        }catch (Exception e){
            e.printStackTrace();
            return  ResponseBo.error("修改"+name+"失败");
        }
    }

    @RequestMapping("menu/menuButtonTree")
    @ResponseBody
    public ResponseBo menuButtonTree(){
        try{
            Tree<Menu> menuButtonTree = this.menuService.getMenuButtonTree();
            return  ResponseBo.ok(menuButtonTree);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseBo.error("获取菜单错误");
        }

    }





}
