package cc.mrbird.system.controller;


import cc.mrbird.common.annotation.Log;
import cc.mrbird.common.controller.BaseController;
import cc.mrbird.common.domain.ResponseBo;
import cc.mrbird.common.domain.Tree;
import cc.mrbird.common.util.FileUtils;
import cc.mrbird.system.domain.Dept;
import cc.mrbird.system.service.DeptService;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.swing.StringUIClientPropertyKey;

import java.util.List;

@Controller
public class DeptController extends BaseController {

    @Autowired
    private DeptService deptService;

    @Log("获取部门信息")
    @RequiresPermissions("dept:list")
    @RequestMapping("dept")
    public String index(){
        return "system/dept/dept";
    }

    @RequestMapping("dept/list")
    @ResponseBody
    public ResponseBo deptList(Dept dept){
        try{
            this.deptService.findAllDept(dept);
            return  ResponseBo.ok("部门菜单成功");
        }catch (Exception e){
            e.printStackTrace();
            return ResponseBo.error("部门菜单失败");
        }
    }


    @RequestMapping("dept/tree")
    @ResponseBody
    public ResponseBo getTreeDept(){
        try{
            Tree<Dept> tree=this.deptService.getDeptTree();
            return  ResponseBo.ok(tree);
        }catch (Exception e){
            e.printStackTrace();
            return  ResponseBo.error("获取部门列表失败");
        }
    }

    @RequestMapping("dept/getDept")
    @ResponseBody
    public ResponseBo getDept(Long ids){
        try {
            Dept dept = this.deptService.findById(ids);
            return ResponseBo.ok(dept);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseBo.error("查询部门失败");
        }
    }

    @RequestMapping("dept/checkDeptName")
    public boolean checkDeptName(String deptName,Dept dept){
            if (StringUtils.isNotBlank(deptName) && deptName.equalsIgnoreCase(dept.getDeptName())){
                return true;
            }
            Dept result = this.deptService.findByName(deptName);
            if (result!=null){
                return false;
            }
            return true;
    }

    @RequestMapping("dept/excel")
    @ResponseBody
    public ResponseBo exportExcel(Dept dept){
        try {
            List<Dept> list = this.deptService.findAllDept(dept);
            return FileUtils.createExcelByPoiKit("部门表",list,Dept.class);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseBo.error("导出部门表失败");
        }
    }

    @RequestMapping("dept/csv")
    @ResponseBody
    public ResponseBo exportCsv(Dept dept){
        try {
            List<Dept> list = this.deptService.findAllDept(dept);
            return FileUtils.createCsv("部门表",list,Dept.class);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseBo.error("导出部门表失败");
        }
    }

    @Log("添加部门")
    @RequiresPermissions("dept:add")
    @RequestMapping("dept/add")
    @ResponseBody
    public ResponseBo addDept(Dept dept){
        try {
            this.deptService.addDept(dept);
            return ResponseBo.ok("添加部门成功");
        }catch (Exception e){
            e.printStackTrace();
            return ResponseBo.error("添加部门失败");
        }
    }

    @Log("修改部门")
    @RequiresPermissions("dept:update")
    @RequestMapping("dept/update")
    @ResponseBody
    public ResponseBo updateDept(Dept dept){
        try {
            this.deptService.updateDept(dept);
            return ResponseBo.ok("修改部门成功");
        }catch (Exception e){
            e.printStackTrace();
            return  ResponseBo.error("修改部门失败");
        }
    }

    @Log("删除部门")
    @RequiresPermissions("dept:delete")
    @RequestMapping("dept/delete")
    @ResponseBody
    public ResponseBo delete(String deptids){
        try {
            this.deptService.deleteDept(deptids);
            return ResponseBo.ok("删除部门成功");
        }catch (Exception e){
            e.printStackTrace();
            return  ResponseBo.error("删除部门失败");
        }
    }
}