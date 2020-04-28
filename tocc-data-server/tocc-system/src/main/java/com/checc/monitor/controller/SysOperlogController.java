package com.checc.monitor.controller;

import com.checc.common.core.controller.BaseController;
import com.checc.common.core.domain.AjaxResult;
import com.checc.common.core.domain.R;
import com.checc.common.core.page.TableDataInfo;
import com.checc.common.log.annotation.OperLog;
import com.checc.common.log.enums.BusinessType;
import com.checc.common.security.annotation.HasPermission;
import com.checc.common.utils.poi.ExcelUtil;
import com.checc.monitor.service.ISysOperLogService;
import com.checc.system.domain.SysOperLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 操作日志记录
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/monitor/operlog")
public class SysOperlogController extends BaseController
{
    @Autowired
    private ISysOperLogService operLogService;

    @HasPermission("monitor:operlog:list")
    @GetMapping("/list")
    public TableDataInfo list(SysOperLog operLog)
    {
        startPage();
        List<SysOperLog> list = operLogService.selectOperLogList(operLog);
        return getDataTable(list);
    }

    @OperLog(title = "操作日志", businessType = BusinessType.EXPORT)
    @HasPermission("monitor:operlog:export")
    @GetMapping("/export")
    public R export(SysOperLog operLog)
    {
        List<SysOperLog> list = operLogService.selectOperLogList(operLog);
        ExcelUtil<SysOperLog> util = new ExcelUtil<SysOperLog>(SysOperLog.class);
        return util.exportExcel(list, "操作日志");
    }

    @HasPermission("monitor:operlog:remove")
    @DeleteMapping("/{operIds}")
    public AjaxResult remove(@PathVariable Long[] operIds)
    {
        return toAjax(operLogService.deleteOperLogByIds(operIds));
    }

    @OperLog(title = "操作日志", businessType = BusinessType.CLEAN)
    @HasPermission("monitor:operlog:remove")
    @DeleteMapping("/clean")
    public AjaxResult clean()
    {
        operLogService.cleanOperLog();
        return AjaxResult.success();
    }

    /**
     * 远程调用方法
     * 新增保存操作日志记录
     */
    @PostMapping("/save")
    public void addSave(@RequestBody SysOperLog sysOperLog)
    {
        operLogService.insertOperlog(sysOperLog);
    }
}
