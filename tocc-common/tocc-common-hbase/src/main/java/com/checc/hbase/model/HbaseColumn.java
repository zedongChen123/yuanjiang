package com.checc.hbase.model;

import lombok.Data;

/**
 * @Desc 描述信息
 * @Auther: zhangwenhao
 * @Date: 2020/4/26
 * @Version: 1.0
 * @Last Modified by: zhangwenhao
 * @Last Modified time: 2020/4/26
 */
@Data
public class HbaseColumn {

    /**
     * 列名称
     */
    private String name;
}
