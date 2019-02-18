package cc.mrbird.system.service.impl;

import cc.mrbird.common.util.AddressUtils;
import cc.mrbird.system.domain.User;
import cc.mrbird.system.domain.UserOnline;
import cc.mrbird.system.service.SessionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class SessionServiceImpl implements SessionService {

    @Autowired
    public SessionDAO sessionDAO;

    @Autowired
    public ObjectMapper mapper;


    @Override
    public List<UserOnline> list() {
        List<UserOnline> list =new ArrayList<>();
        Collection<Session> sessions=sessionDAO.getActiveSessions();
        for (Session s : sessions) {
            UserOnline userOnline=new UserOnline();
            User user=new User();
            SimplePrincipalCollection simplePrincipalCollection=new SimplePrincipalCollection();
            if (s.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY)==null){
                continue;
            }else {
              simplePrincipalCollection = (SimplePrincipalCollection) s.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
              user=(User) simplePrincipalCollection.getPrimaryPrincipal();
              userOnline.setUserId(user.getUserId().toString());
              userOnline.setUsername(user.getUsername());
            }
            userOnline.setHost(s.getHost());
            userOnline.setId(s.getId().toString());
            userOnline.setLastAccessTime(s.getLastAccessTime());
            userOnline.setStartTimestamp(s.getStartTimestamp());
            long timeout=s.getTimeout();
            if (timeout==0L){
                userOnline.setStatus("0");
            }else{
                userOnline.setStatus("1");
            }
            userOnline.setTimeout(timeout);
            userOnline.setLocation(AddressUtils.getRealAddressByIP(userOnline.getHost(),mapper));
            list.add(userOnline);
        }
        return list;
    }

    @Override
    public boolean forceLogOut(String sessionId) {
        Session session = sessionDAO.readSession(sessionId);
        session.setTimeout(0);
        return true;
    }
}
