package cc.mrbird.common.aspect;

import cc.mrbird.common.annotation.Log;
import cc.mrbird.common.config.FebsProperies;
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
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

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



    }
}
