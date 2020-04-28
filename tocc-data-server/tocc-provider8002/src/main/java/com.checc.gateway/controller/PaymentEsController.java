package com.checc.gateway.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.checc.common.core.domain.AjaxResult;
import com.checc.common.core.lang.UUID;
import com.checc.es.entity.ElasticEntity;
import com.checc.es.service.BaseElasticService;
import com.checc.es.vo.ElasticDataVo;
import com.checc.es.vo.IdxVo;
import com.checc.gateway.dao.PaymentEsDao;
import com.checc.gateway.entities.PaymentEs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * ES 操作测试
 */

@RestController
@Slf4j
public class PaymentEsController {

    @Autowired
    private PaymentEsDao paymentEsDao;

    @Autowired
    private BaseElasticService baseElasticService;

    @PostMapping(value = "/payment/es/create")
    public void create(@RequestBody PaymentEs payment){
        paymentEsDao.index(payment);
    }

    @PostMapping(value = "/payment/es/add")
    public AjaxResult add(@RequestBody Map<String,Object> payment){
        ElasticDataVo elasticDataVo = new ElasticDataVo();
        elasticDataVo.setIdxName("idx_locate");
        elasticDataVo.setType("_doc");
        ElasticEntity elasticEntity = new ElasticEntity<>();
        if (ObjectUtil.isNotNull(payment.get("uuid"))) {
            elasticEntity.setId(String.valueOf(payment.get("uuid")));
        } else {
            elasticEntity.setId(UUID.randomUUID().toString());
        }
        elasticEntity.setData((payment));
        elasticDataVo.setElasticEntity(elasticEntity);
        String dataUrl = baseElasticService.insertOrUpdateOne(elasticDataVo.getIdxName(),elasticDataVo.getType(),elasticDataVo.getElasticEntity());
        log.info("ES插入成功");
        return AjaxResult.success("ES插入成功",dataUrl);
    }

    @RequestMapping(value = "/createIndex",method = RequestMethod.POST)
    public AjaxResult createIndex(@RequestBody IdxVo idxVo){
        try {
            //索引不存在，再创建，否则不允许创建
            if(!baseElasticService.indexExist(idxVo.getIdxName())){
                String idxSql = JSONObject.toJSONString(idxVo.getIdxSql());
                log.warn(" idxName={}, idxSql={}",idxVo.getIdxName(),idxSql);
                baseElasticService.createIndex(idxVo.getIdxName(),idxSql);
                log.info("索引创建成功");
                return AjaxResult.success("索引创建成功");
            } else{
                log.error("索引已存在");
                return AjaxResult.error("索引已存在");
            }
        } catch (Exception e) {
            return AjaxResult.error("索引创建失败");
        }
    }

}
