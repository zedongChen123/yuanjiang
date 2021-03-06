package com.checc.common.security.aspect;

import com.checc.common.exception.ForbiddenException;
import com.checc.common.security.LoginUser;
import com.checc.common.security.annotation.HasAnyPermissions;
import com.checc.common.security.annotation.HasPermission;
import com.checc.common.security.utils.SecurityUtils;
import com.checc.common.utils.StringUtils;
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
public class AnyPremissionAspect
{
    /** 所有权限标识 */
    private static final String ALL_PERMISSION = "*:*:*";


    private static final String PERMISSION_DELIMETER = ",";


    @Around("@annotation(com.checc.common.security.annotation.HasAnyPermissions)")
    public Object around(ProceedingJoinPoint point) throws Throwable
    {
        Signature signature = point.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        HasAnyPermissions annotation = method.getAnnotation(HasAnyPermissions.class);
        if (annotation == null)
        {
            return point.proceed();
        }
        String authority = new StringBuilder(annotation.value()).toString();
        if (hasAnyPermi(authority))
        {
            return point.proceed();
        }
        else
        {
            throw new ForbiddenException();
        }
    }

    /**
     * 验证用户是否具有以下任意一个权限
     *
     * @param permissions 以 PERMISSION_NAMES_DELIMETER 为分隔符的权限列表
     * @return 用户是否具有以下任意一个权限
     */
    public boolean hasAnyPermi(String permissions)
    {
        if (StringUtils.isEmpty(permissions))
        {
            return false;
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isNull(loginUser) || CollectionUtils.isEmpty(loginUser.getPermissions()))
        {
            return false;
        }
        Set<String> authorities = loginUser.getPermissions();
        for (String permission : permissions.split(PERMISSION_DELIMETER))
        {
            if (permission != null && hasPermissions(authorities, permission))
            {
                return true;
            }
        }
        return false;
    }
    /**
     * 判断是否包含权限
     *
     * @param permissions 权限列表
     * @param permission 权限字符串
     * @return 用户是否具备某权限
     */
    private boolean hasPermissions(Set<String> permissions, String permission)
    {
        return permissions.contains(ALL_PERMISSION) || permissions.contains(StringUtils.trim(permission));
    }
}