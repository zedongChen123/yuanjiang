package com.checc.common.security.aspect;

import com.checc.common.exception.ForbiddenException;
import com.checc.common.security.LoginUser;
import com.checc.common.security.annotation.HasPermission;
import com.checc.common.security.annotation.HasRole;
import com.checc.common.security.utils.SecurityUtils;
import com.checc.common.utils.StringUtils;
import com.checc.system.domain.SysRole;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.Set;

@Aspect
@Component
@Slf4j
public class RoleAspect
{
    /** 管理员角色权限标识 */
    private static final String SUPER_ADMIN = "admin";

    private static final String ROLE_DELIMETER = ",";


    @Around("@annotation(com.checc.common.security.annotation.HasRole)")
    public Object around(ProceedingJoinPoint point) throws Throwable
    {
        Signature signature = point.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        HasRole annotation = method.getAnnotation(HasRole.class);
        if (annotation == null)
        {
            return point.proceed();
        }
        String role = new StringBuilder(annotation.value()).toString();
        if (hasRole(role))
        {
            return point.proceed();
        }
        else
        {
            throw new ForbiddenException();
        }
    }

    /**
     * 判断用户是否拥有某个角色
     *
     * @param role 角色字符串
     * @return 用户是否具备某角色
     */
    public boolean hasRole(String role)
    {
        if (StringUtils.isEmpty(role))
        {
            return false;
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isNull(loginUser) || CollectionUtils.isEmpty(loginUser.getUser().getRoles()))
        {
            return false;
        }
        for (SysRole sysRole : loginUser.getUser().getRoles())
        {
            String roleKey = sysRole.getRoleKey();
            if (SUPER_ADMIN.contains(roleKey) || roleKey.contains(StringUtils.trim(role)))
            {
                return true;
            }
        }
        return false;
    }
}