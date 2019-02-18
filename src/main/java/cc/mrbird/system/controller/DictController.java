package cc.mrbird.system.controller;

import cc.mrbird.common.annotation.Log;
import cc.mrbird.common.controller.BaseController;
import cc.mrbird.common.domain.QueryRequest;
import cc.mrbird.common.domain.ResponseBo;
import cc.mrbird.common.util.FileUtils;
import cc.mrbird.system.domain.Dict;
import cc.mrbird.system.service.DictService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.util.List;
import java.util.Map;

@Controller
public class DictController extends BaseController {


    @Autowired
    DictService dictService;

    @Log("获取字典数据")
    @RequiresPermissions("dict:list")
    @RequestMapping("dict")
    public String index(){
        return  "system/dict/dict";
    }

    @RequestMapping("dict/list")
    @ResponseBody
    public Map<String ,Object> dictList(QueryRequest request , Dict dict){
        PageHelper.startPage(request.getPageNum(),request.getPageSize());
        List<Dict> allDict = this.dictService.findAllDict(dict);
        PageInfo pageInfo=new PageInfo(allDict);
        return getDataTable(pageInfo);

    }


    @RequestMapping("dict/excel")
    @ResponseBody
    public ResponseBo dictExcel(Dict dict){
        try {
            List<Dict> allDict = this.dictService.findAllDict(dict);
            return FileUtils.createExcelByPoiKit("字典excel",allDict,Dict.class);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseBo.error("导出excel失败");
        }
    }

    @ResponseBody
    @RequestMapping("dict/csv")
    public ResponseBo dictCsv(Dict dict){
        try {
            List<Dict> allDict = this.dictService.findAllDict(dict);
            return FileUtils.createCsv("字典表",allDict,Dict.class);
        }catch (Exception e){
            return  ResponseBo.error("导出csv失败");
        }
    }

    @RequestMapping("dict/getDict")
    @ResponseBody
    public ResponseBo getDict(Long id){
        try {
            Dict dict = this.dictService.findById(id);
            return  ResponseBo.ok(dict);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseBo.error("获取字典数据失败");
        }
    }

    @ResponseBody
    @RequestMapping("dict/add")
    @RequiresPermissions("dict:add")
    public ResponseBo addDict(Dict dict){
        try{
            int i = this.dictService.addDict(dict);
            return  ResponseBo.ok(i);
        }catch (Exception e){
            e.printStackTrace();
            return  ResponseBo.error("添加字典失败");
        }
    }

    @ResponseBody
    @RequestMapping("dict/delete")
    @RequiresPermissions("dict:delete")
    public ResponseBo deleteDict(String ids){
        try{
            int i = this.dictService.deleteDict(ids);
            return ResponseBo.ok(i);
        }catch (Exception e){
            e.printStackTrace();
            return  ResponseBo.error("删除失败");

        }
    }

    @ResponseBody
    @RequestMapping("dict/update")
    public ResponseBo updateDict(Dict dict){
        try {
            int i = this.dictService.updateDict(dict);
            return ResponseBo.ok(i);
        }catch ( Exception e){

            e.printStackTrace();
            return ResponseBo.error("修改失败");
        }
    }
}


