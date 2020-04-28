package com.checc.system.feign;

import com.checc.common.constant.ServiceNameConstants;
import com.checc.system.domain.SysDept;
import com.checc.system.feign.factory.RemoteDeptFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 用户 Feign服务层
 * 
 * @author checc
 * @date 2019-05-20
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteDeptFallbackFactory.class)
public interface RemoteDeptService
{
    @GetMapping("/system/dept/get/{deptId}")
    public SysDept selectSysDeptByDeptId(@PathVariable("deptId") Long deptId);
}
