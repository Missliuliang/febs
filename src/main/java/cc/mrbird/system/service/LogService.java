package cc.mrbird.system.service;

import cc.mrbird.common.service.IService;
import cc.mrbird.system.domain.SysLog;

import java.util.List;

public interface LogService extends IService<SysLog> {

   List<SysLog> findAllLogs( SysLog sysLog);

   int deleteLogs(String logId);
}
