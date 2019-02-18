package cc.mrbird.system.service;

import cc.mrbird.common.service.IService;
import cc.mrbird.system.domain.Dict;

import java.util.List;

public interface DictService extends IService<Dict> {

    List<Dict> findAllDict(Dict dict);

    Dict findById(Long id);

    int  addDict(Dict dict);

    int  updateDict(Dict dict);

    int deleteDict(String ids);





}
