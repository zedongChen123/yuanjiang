package com.checc.hbase.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Desc 描述信息
 * @Auther: zhangwenhao
 * @Date: 2020/4/26
 * @Version: 1.0
 * @Last Modified by: zhangwenhao
 * @Last Modified time: 2020/4/26
 */
@Data
public class HbaseColumnFamily {

    private String columnFamilyName;

    private String columnFamilyCode;

    private List<HbaseColumn> columns = new ArrayList<>();

    public void addColumn(HbaseColumn hbaseColumn) {
        this.columns.add(hbaseColumn);
    }
}
