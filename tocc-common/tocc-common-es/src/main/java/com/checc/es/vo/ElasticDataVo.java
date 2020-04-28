package com.checc.es.vo;

import com.checc.es.entity.ElasticEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElasticDataVo {

    /**
     * 索引名
     */
    private String idxName;

    /**
     * 类型
     */
    private String type;

    /**
     * 数据存储对象
     */
    private ElasticEntity elasticEntity;
}
