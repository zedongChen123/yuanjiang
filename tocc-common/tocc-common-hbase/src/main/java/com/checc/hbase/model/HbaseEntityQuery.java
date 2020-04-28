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
public class HbaseEntityQuery {

    private String rowKey;

    private String timeStamp;

    private List<HbaseColumnFamilyQuery> columnFamilies = new ArrayList<>();

    public void addColumnFamily(HbaseColumnFamilyQuery hbaseColumnFamily) {
        this.columnFamilies.add(hbaseColumnFamily);
    }
}
