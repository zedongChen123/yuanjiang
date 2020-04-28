package com.checc.hbase.service;

import com.checc.hbase.model.*;
import org.apache.hadoop.hbase.client.ResultScanner;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Desc 描述信息
 * @Auther: zhangwenhao
 * @Date: 2020/4/26
 * @Version: 1.0
 * @Last Modified by: zhangwenhao
 * @Last Modified time: 2020/4/26
 */
public interface HBaseService {

    /**
     * 创建Hbase表
     *
     * @param tableName 表名称
     * @param columnFamily
     * @return boolean
     */
    boolean createTable(String tableName, List<String> columnFamily);

    /**
     * 创建Hbase表
     *
     * @param tableName
     * @param startKey
     * @param endKey
     * @param columnFamily
     * @return boolean
     */
    boolean createTable(String tableName, String startKey, String endKey, List<String> columnFamily);

    /**
     * 删除Hbase表
     *
     * @param tableName
     * @return boolean
     */
    boolean deleteTable(String tableName);

    /**
     * 根据rowKey获取
     *
     * @param tableName
     * @param rowKeys
     * @return
     * @throws IOException
     */
    List<Map<String, Object>> getByRowKey(String tableName, List<String> rowKeys) throws IOException;

    /**
     * 插入或更新
     *
     * @param tableName
     * @param hbaseEntity
     * @return boolean
     */
    boolean insertUpdate(String tableName, HbaseEntityQuery hbaseEntity);

    /**
     * 批量插入或更新
     *
     * @param tableName
     * @param hbaseEntityList
     * @return boolean
     */
    boolean insertUpdateBatch(String tableName, List<HbaseEntityQuery> hbaseEntityList);

    /**
     * 删除单个Hbase数据
     *
     * @param tableName
     * @param hbaseEntity
     * @return boolean
     */
    boolean delete(String tableName, HbaseEntity hbaseEntity);

    /**
     * 批量删除
     *
     * @param tableName
     * @param hbaseEntityList
     * @return boolean
     */
    boolean deleteBatch(String tableName, List<HbaseEntity> hbaseEntityList);

    /**
     * @param tableName
     * @param isReversed
     * @return
     * @throws IOException
     */

    ResultScanner scanData(String tableName, boolean isReversed) throws IOException;

    /**
     * 按照时间扫描
     * 注：需在元数据BusIndex中添加createtime系统字段
     *
     * @param tableName
     * @param startTime
     * @param endTime
     * @param hbaseColumnFamilyList
     * @param hbaseColumnQuery
     * @return
     * @throws IOException
     */
    List<Map<String, Object>> scanByTime(String tableName, String startTime, String endTime, List<HbaseColumnFamily> hbaseColumnFamilyList, HbaseColumnQuery hbaseColumnQuery) throws IOException;

    /**
     * 按照rowKey区间扫描
     *
     * @param tableName
     * @param startRow
     * @param stopRow
     * @param hbaseColumnFamilyList
     * @return
     * @throws IOException
     */
    ResultScanner areaScan(String tableName, String startRow, String stopRow, List<HbaseColumnFamily> hbaseColumnFamilyList) throws IOException;

    /**
     * 按照指定的过滤器扫描
     *
     * @param tableName
     * @param hbaseColumnFamilyList
     * @param hbaseColumnFamilyQueryList
     * @return
     * @throws IOException
     */
    ResultScanner scanByFilter(String tableName, List<HbaseColumnFamily> hbaseColumnFamilyList, List<HbaseColumnFamilyQuery> hbaseColumnFamilyQueryList) throws IOException;

    /**
     * rowKey区间扫描+过滤器扫描
     *
     * @param tableName
     * @param startRow
     * @param endRow
     * @param hbaseColumnFamilyList
     * @param columnFamilyQueryList
     * @return
     * @throws IOException
     */
    List<Map<String, Object>> areaScanByFilter(String tableName, String startRow, String endRow, List<HbaseColumnFamily> hbaseColumnFamilyList, List<HbaseColumnFamilyQuery> columnFamilyQueryList) throws IOException;

}
