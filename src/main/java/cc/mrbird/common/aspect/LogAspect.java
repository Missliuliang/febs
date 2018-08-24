package cc.mrbird.common.aspect;

import cc.mrbird.common.annotation.Log;
import cc.mrbird.common.config.FebsProperies;
import cc.mrbird.common.util.HttpContextUtils;
import cc.mrbird.common.util.IPUtils;
import cc.mrbird.system.domain.SysLog;
import cc.mrbird.system.domain.User;
import cc.mrbird.system.service.LogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;

@Aspect
@Component
public class LogAspect {

    @Autowired
    private LogService logService ;

    @Autowired
    private FebsProperies febsProperies;

    private ObjectMapper mapper;


    @Pointcut("@annotation(cc.mrbird.common.annotation.Log)")
    public void pointCut(){}

    public void saveLog(ProceedingJoinPoint joinPoint ,long time)throws Exception{
        User user= (User) SecurityUtils.getSubject().getPrincipal();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        SysLog sysLog=new SysLog();
        Log annotation = method.getAnnotation(Log.class);
        if(annotation !=null){

            sysLog.setOperation(annotation.value());
        }
        String name = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getMethod().getName();
        sysLog.setMethod(name+"."+methodName+"()");

        Object[] args = joinPoint.getArgs();
        LocalVariableTableParameterNameDiscoverer u=new LocalVariableTableParameterNameDiscoverer();
        String[] parameterNames = u.getParameterNames(method);
        if(args!=null){
            StringBuffer params=new StringBuffer();
            int i =0 ;
            while (i<args.length){
               if(args[i] instanceof Serializable)
                   params.append("").append(parameterNames[i]).append(": ").append(this.mapper.writeValueAsString(args[i]));
               else
                   params.append("").append(parameterNames[i]).append(": ").append(args[i]);
                   i++ ;
            }
            sysLog.setParams(params.toString());
        }
       /* ServletRequestAttributes attributes =(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();*/
        HttpServletRequest request = HttpContextUtils.getServletRequest();
        sysLog.setIp(IPUtils.getIpAddr(request));
        sysLog.setUsername(user.getUsername());
        sysLog.setCreateTime(new Date());
        sysLog.setLocation(request.getLocalAddr());
        this.logService.save(sysLog);
    }
}
