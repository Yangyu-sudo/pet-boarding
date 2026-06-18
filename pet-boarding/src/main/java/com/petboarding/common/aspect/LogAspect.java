package com.petboarding.common.aspect;

import cn.hutool.json.JSONUtil;
import com.petboarding.common.security.UserDetailsServiceImpl;
import com.petboarding.modules.system.entity.OperationLog;
import com.petboarding.modules.system.mapper.OperationLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class LogAspect {

    @Autowired(required = false)
    private OperationLogMapper operationLogMapper;

    @Pointcut("execution(* com.petboarding.modules..controller.*.*(..))")
    public void controllerPointcut() {}

    @Around("controllerPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 执行方法
        Object result = joinPoint.proceed();

        long duration = System.currentTimeMillis() - startTime;

        // 记录日志
        try {
            saveLog(joinPoint, duration);
        } catch (Exception e) {
            log.warn("记录操作日志失败: {}", e.getMessage());
        }

        return result;
    }

    private void saveLog(ProceedingJoinPoint joinPoint, long duration) {
        if (operationLogMapper == null) return;

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return;

        HttpServletRequest request = attributes.getRequest();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        OperationLog operationLog = new OperationLog();
        operationLog.setMethod(request.getMethod());
        operationLog.setParams(JSONUtil.toJsonStr(joinPoint.getArgs()));
        operationLog.setIp(getIpAddress(request));
        operationLog.setDuration(duration);
        operationLog.setModule(method.getDeclaringClass().getSimpleName());
        operationLog.setOperation(method.getName());

        // 获取当前用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            operationLog.setUsername(authentication.getName());
        }

        operationLogMapper.insert(operationLog);
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip != null && ip.contains(",") ? ip.split(",")[0] : ip;
    }
}
