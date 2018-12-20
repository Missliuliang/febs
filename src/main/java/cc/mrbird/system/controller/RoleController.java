package cc.mrbird.system.controller;

import cc.mrbird.common.annotation.Log;
import cc.mrbird.common.controller.BaseController;
import cc.mrbird.common.domain.QueryRequest;
import cc.mrbird.common.domain.ResponseBo;
import cc.mrbird.common.util.FileUtils;
import cc.mrbird.system.domain.Menu;
import cc.mrbird.system.domain.Role;
import cc.mrbird.system.domain.RoleWithMenu;
import cc.mrbird.system.service.RoleService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class   RoleController extends BaseController {


    @Autowired
    private RoleService roleService;

    @Log("或缺角色信息")
    @RequestMapping("role")
    @RequiresPermissions("role:list")
    public String index(){
        return "system/role/role";
    }

    @RequestMapping("role/list")
    @ResponseBody
     public Map<String ,Object> rolelist(QueryRequest request, Role role){
         PageHelper.startPage(request.getPageNum(),request.getPageSize());
         List<Role> allRole = this.roleService.findAllRole(role);
         PageInfo pageInfo=new PageInfo(allRole);
         return getDataTable(pageInfo);
     }

     @RequestMapping("role/excel")
     @ResponseBody
     public ResponseBo roleExcel(Role role){
        try{
            List<Role> list = roleService.findAllRole(role);
            return FileUtils.createExcelByPoiKit("角色表",list,Role.class);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseBo.error("导出excel错误");
        }
     }

     @RequestMapping("role/csv")
     @ResponseBody
     public ResponseBo roleCsv(Role role){
        try{
            List<Role> list = this.roleService.findAllRole(role);
            return FileUtils.createCsv("角色表",list,Role.class);
        }catch (Exception e){
            return ResponseBo.error("导出csv错误");
        }
     }

     @RequestMapping("role/getRole")
     @ResponseBody
     public ResponseBo getRole(Long roleId){
        try{
            Role role = this.roleService.findRoleWithMenu(roleId);
            return  ResponseBo.ok(role);

        }catch (Exception e){
            e.printStackTrace();
            return  ResponseBo.error("error");
        }

     }

     @RequestMapping("role/checkRoleName")
     @ResponseBody
     public boolean checkRoleName(String roleName ,String oldRoleName){
        if (StringUtils.isNotBlank(oldRoleName) && roleName.equalsIgnoreCase(oldRoleName)){
            return  true ;
        }
         Role name = this.roleService.findByName(roleName);
        if (name!=null){
            return  false;
        }
        return true;
     }

     @Log("添加角色")
     @RequestMapping("role/add")
     @RequiresPermissions("role:add")
     @ResponseBody
     public ResponseBo addrole(Role role , Long[] menuId){
        try{
            this.roleService.addRole(role,menuId);
            return  ResponseBo.ok("添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return  ResponseBo.error("添加失败");
        }
     }

     @Log("删除角色")
     @RequestMapping("role/delete")
     @RequiresPermissions("role:delete")
     @ResponseBody
     public ResponseBo deleterole(String ids){
        try {
            this.roleService.deleteRoles(ids);
            return ResponseBo.ok("删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return ResponseBo.error("删除失败");
        }
     }

     @Log("修改角色")
     @RequestMapping("role/update")
     @RequiresPermissions("role:update")
     @ResponseBody
     public ResponseBo updaterole(Role role ,Long[] menuIds){
        try {
            this.roleService.updateRole(role,menuIds);
            return ResponseBo.ok("修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return ResponseBo.error("修改失败");
        }
     }

}
