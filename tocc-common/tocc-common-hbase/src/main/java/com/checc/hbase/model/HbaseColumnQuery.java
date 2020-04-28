package com.checc.hbase.model;

import lombok.Data;
import org.apache.hadoop.hbase.filter.CompareFilter;

/**
 * @Desc 描述信息
 * @Auther: zhangwenhao
 * @Date: 2020/4/26
 * @Version: 1.0
 * @Last Modified by: zhangwenhao
 * @Last Modified time: 2020/4/26
 */
@Data
public class HbaseColumnQuery {

    /**
     * 所属列族代码
     */
    private String columnFamilyCode;

    /**
     * 列名称
     */
    private String name;

    /**
     * 操作符，例如 LESS,
     * LESS_OR_EQUAL,
     * EQUAL,
     * NOT_EQUAL,
     * GREATER_OR_EQUAL,
     * GREATER,
     * NO_OP;
     */
    private CompareFilter.CompareOp compareOp;

    /**
     * 列值
     */
    private String value;
}
