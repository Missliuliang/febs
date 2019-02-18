package cc.mrbird.system.service;

import cc.mrbird.common.service.IService;
import cc.mrbird.system.domain.UserOnline;

import java.util.List;

public interface SessionService {
    List<UserOnline> list();

    boolean forceLogOut(String sessionId);

}
