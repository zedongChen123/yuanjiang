package com.checc.hbase.service.impl;

import com.checc.hbase.model.*;
import com.checc.hbase.service.HBaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
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
@Slf4j
@Service
public class HBaseServiceImpl implements HBaseService {

    @Autowired
    private Connection connection;

    /**
     * 创建表，创建表只需要指定列族，不需要指定列
     * 直接用命令会更快，如create 'user','info1','info2'
     */
    @Override
    public boolean createTable(String tableName, List<String> columnFamily) {
        try {
            Admin admin = connection.getAdmin();
            HTableDescriptor tableDesc = new HTableDescriptor(TableName.valueOf(tableName));
            tableDesc.setMemStoreFlushSize(64 * 1024 * 1024);
            tableDesc.setMaxFileSize(2073741824);
            //设置columnFamily
            if (columnFamily.size() > 0) {
                setColumnFamily(columnFamily, tableDesc);
            }
            admin.createTable(tableDesc);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean createTable(String tableName, String startKey, String endKey, List<String> columnFamily) {
        try {
            Admin admin = connection.getAdmin();
            HTableDescriptor tableDesc = new HTableDescriptor(TableName.valueOf(tableName));
            //还需建立预分区，否则写入数据很慢
//            tableDesc.setRegionSplitPolicyClassName("10");
            tableDesc.setMemStoreFlushSize(64 * 1024 * 1024);
            tableDesc.setMaxFileSize(2073741824);
            //设置columnFamily
            if (columnFamily.size() > 0) {
                setColumnFamily(columnFamily, tableDesc);
            }
//            byte[][] splitKeys = new byte[][] { Bytes.toBytes("154803500"),
//                    Bytes.toBytes("154803600"), Bytes.toBytes("154803734"),
//                    Bytes.toBytes("154803534") };
//            startKey = "154803500";
//            endKey = "154803500";
            byte[] startKeyByte = Bytes.toBytes(startKey);
            byte[] endKeyByte = Bytes.toBytes(endKey);
            admin.createTable(tableDesc, startKeyByte, endKeyByte, 10);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**设置columnFamily*/
    private void setColumnFamily(List<String> columnFamily, HTableDescriptor tableDesc) {
        for (String s : columnFamily) {
            tableDesc.addFamily(new HColumnDescriptor(s));
        }
    }

    @Override
    public boolean deleteTable(String tableName) {
        try {
            Admin admin = connection.getAdmin();
            TableName hTableName = TableName.valueOf(tableName);
            admin.disableTable(hTableName);
            admin.deleteTable(hTableName);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean insertUpdate(String tableName, HbaseEntityQuery hbaseEntity) {
        try {
            HTable table = (HTable) connection.getTable(TableName.valueOf(tableName));
            //构造参数是row key，必传
            Put put = new Put(Bytes.toBytes(hbaseEntity.getRowKey()));
            put = addColumn(hbaseEntity, put);
            //添加一个插入集合
            table.put(put);
            log.info("{}：录入成功", hbaseEntity.getRowKey());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 添加数据
     * 对同一个row key进行重新put同一个cell就是修改数据
     */
    @Override
    public boolean insertUpdateBatch(String tableName, List<HbaseEntityQuery> hbaseEntityList) {
        try {
            HTable table = (HTable) connection.getTable(TableName.valueOf(tableName));
            //激活缓冲区
            table.setAutoFlushTo(false);
            //设置buffer的容量，这里设置了6MB的buffer容量
            table.setWriteBufferSize(6 * 1024 * 1024);
            List<Put> puts = new ArrayList<>();
            for (HbaseEntityQuery hbaseEntity : hbaseEntityList) {
                //构造参数是row key，必传
                Put put = new Put(Bytes.toBytes(hbaseEntity.getRowKey()));
                put = addColumn(hbaseEntity, put);
                puts.add(put);
            }
            //添加一个插入集合
            table.put(puts);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private Put addColumn(HbaseEntityQuery hbaseEntity, Put put) {
        //这里的参数依次为，列族名，列名，值
        List<HbaseColumnFamilyQuery> hbaseColumnFamilyList = hbaseEntity.getColumnFamilies();
        for (HbaseColumnFamilyQuery columnFamily : hbaseColumnFamilyList) {
            List<HbaseColumnQuery> columnList = columnFamily.getColumns();
            for (HbaseColumnQuery hbaseColumn : columnList) {
                put.addColumn(Bytes.toBytes(columnFamily.getColumnFamilyCode()), Bytes.toBytes(hbaseColumn.getName()), Bytes.toBytes(hbaseColumn.getValue()));
            }
        }
        return put;
    }

    /**
     * 删除单个数据
     *
     * @param tableName
     * @param hbaseEntity
     */
    @Override
    public boolean delete(String tableName, HbaseEntity hbaseEntity) {
        try {
            HTable table = (HTable) connection.getTable(TableName.valueOf(tableName));
            if (hbaseEntity != null) {
                //构造参数是row key，必传
                Delete delete = new Delete(Bytes.toBytes(hbaseEntity.getRowKey()));
                delete = addColumn(hbaseEntity, delete);
                table.delete(delete);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 批量删除数据
     *
     * @param tableName
     * @param hbaseEntityList
     * @return
     */
    @Override
    public boolean deleteBatch(String tableName, List<HbaseEntity> hbaseEntityList) {
        try {
            HTable table = (HTable) connection.getTable(TableName.valueOf(tableName));
            List<Delete> deletes = new ArrayList<>();
            for (HbaseEntity hbaseEntity : hbaseEntityList) {
                //构造参数是row key，必传
                Delete delete = new Delete(Bytes.toBytes(hbaseEntity.getRowKey()));
                delete = addColumn(hbaseEntity, delete);
                deletes.add(delete);
            }
            //添加一个删除集合
            table.delete(deletes);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private Delete addColumn(HbaseEntity hbaseEntity, Delete delete) {
        //这里的参数依次为，列族名，列名
        //删除指定的一个单元
        List<HbaseColumnFamily> hbaseColumnFamilyList = hbaseEntity.getColumnFamilies();
        for (HbaseColumnFamily columnFamily : hbaseColumnFamilyList) {
            List<HbaseColumn> columnList = columnFamily.getColumns();
            for (HbaseColumn hbaseColumn : columnList) {
                delete.addColumn(Bytes.toBytes(columnFamily.getColumnFamilyCode()), Bytes.toBytes(hbaseColumn.getName()));
            }
        }
        return delete;
    }

    /**
     * 全表扫描
     * 查询的多种情况还需进一步设计，这里是demo
     */
    @Override
    public ResultScanner scanData(String tableName, boolean isReversed) throws IOException {
        HTable table = (HTable) connection.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        // 倒序扫描
        scan.setReversed(isReversed);
        return table.getScanner(scan);
    }

    /**
     * 根据rowKey查询
     *
     * @param tableName
     * @param rowKeys
     * @return
     * @throws IOException
     */
    @Override
    public List<Map<String, Object>> getByRowKey(String tableName, List<String> rowKeys) throws IOException {
        List<Map<String, Object>> resultList = new ArrayList<>();
        List<Get> getList = new ArrayList<>();
        HTable table = (HTable) connection.getTable(TableName.valueOf(tableName));
        // 根据主键查询
        for (String rowKey : rowKeys) {
            Get get = new Get(rowKey.getBytes());
            getList.add(get);
        }
        Result[] results = table.get(getList);
        for (Result result : results) {
            List<Cell> ceList = result.listCells();
            if (ceList != null && ceList.size() > 0) {
                resultList.add(getResiltMap(ceList));
            }
        }
        return resultList;
    }

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
    @Override
    public List<Map<String, Object>> scanByTime(String tableName, String startTime, String endTime, List<HbaseColumnFamily> hbaseColumnFamilyList, HbaseColumnQuery hbaseColumnQuery) throws IOException {
        HTable table = (HTable) connection.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();

        //确定需要返回的列族及其子列
        scan = addScanColumnFamilyAndScanColumn(hbaseColumnFamilyList, scan);

        SingleColumnValueFilter startTimeValueFilter = new SingleColumnValueFilter(Bytes.toBytes(hbaseColumnQuery.getColumnFamilyCode()), Bytes.toBytes(hbaseColumnQuery.getName()), CompareFilter.CompareOp.GREATER_OR_EQUAL, Bytes.toBytes(String.valueOf(startTime)));
        SingleColumnValueFilter endTimeValueFilter = new SingleColumnValueFilter(Bytes.toBytes(hbaseColumnQuery.getColumnFamilyCode()), Bytes.toBytes(hbaseColumnQuery.getName()), CompareFilter.CompareOp.LESS_OR_EQUAL, Bytes.toBytes(String.valueOf(endTime)));
        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
        filterList.addFilter(startTimeValueFilter);
        filterList.addFilter(endTimeValueFilter);
        scan.setFilter(filterList);
        ResultScanner resultScanner = table.getScanner(scan);
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Result result = resultScanner.next(); result != null; result = resultScanner.next()) {
            List<Cell> ceList = result.listCells();
            if (ceList != null && ceList.size() > 0) {
                resultList.add(getResiltMap(ceList));
            }
        }
        return resultList;
    }

    private Map<String, Object> getResiltMap(List<Cell> ceList) {
        Map<String, Object> resultMap = new HashMap<>();
        String rowKey = "";
        for (Cell cell : ceList) {
            //必须使用CellUtil 否则会乱码
            rowKey = new String(CellUtil.cloneRow(cell), StandardCharsets.UTF_8);
            String columnName = new String(CellUtil.cloneQualifier(cell), StandardCharsets.UTF_8);
            String value = new String(CellUtil.cloneValue(cell), StandardCharsets.UTF_8);
            resultMap.put(columnName, value);
        }
        resultMap.put("rowKey", rowKey);
        return resultMap;
    }

    /**
     * 添加列族及其子列
     *
     * @param hbaseColumnFamilyList
     * @param scan
     * @return
     */
    private Scan addScanColumnFamilyAndScanColumn(List<HbaseColumnFamily> hbaseColumnFamilyList, Scan scan) {
        for (HbaseColumnFamily hbaseColumnFamily : hbaseColumnFamilyList) {
            scan.addFamily(Bytes.toBytes(hbaseColumnFamily.getColumnFamilyCode()));
            List<HbaseColumn> columns = hbaseColumnFamily.getColumns();
            for (HbaseColumn column : columns) {
                //指定列
                scan.addColumn(Bytes.toBytes(hbaseColumnFamily.getColumnFamilyCode()), Bytes.toBytes(column.getName()));
            }
        }
        return scan;
    }

    /**
     * 区间扫描
     * 查询的多种情况还需进一步设计，这里是demo
     */
    @Override
    public ResultScanner areaScan(String tableName, String startRow, String stopRow, List<HbaseColumnFamily> hbaseColumnFamilyList) throws IOException {
        try {
            HTable table = (HTable) connection.getTable(TableName.valueOf(tableName));
            Scan scan = new Scan();
            //设置开始行
            scan.setStartRow(Bytes.toBytes(startRow));
            //设置结束行
            scan.setStopRow(Bytes.toBytes(stopRow));
            scan = addScanColumnFamilyAndScanColumn(hbaseColumnFamilyList, scan);
            return table.getScanner(scan);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 全表扫描时加过滤器 --> 列值过滤器
     * 查询的多种情况还需进一步设计（枚举类型），这里是demo
     */
    @Override
    public ResultScanner scanByFilter(String tableName, List<HbaseColumnFamily> hbaseColumnFamilyList, List<HbaseColumnFamilyQuery> hbaseColumnFamilyQueryList) throws IOException {
        try {
            HTable table = (HTable) connection.getTable(TableName.valueOf(tableName));

            Scan scan = new Scan();
            //添加列族及其子列
            scan = addScanColumnFamilyAndScanColumn(hbaseColumnFamilyList, scan);
            /*
             * 第一个参数： 列族
             * 第二个参数： 列名
             * 第三个参数： 是一个枚举类型
             *              CompareOp.EQUAL  等于
             *              CompareOp.LESS  小于
             *              CompareOp.LESS_OR_EQUAL  小于或等于
             *              CompareOp.NOT_EQUAL  不等于
             *              CompareOp.GREATER_OR_EQUAL  大于或等于
             *              CompareOp.GREATER  大于
             */
            FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
            for (HbaseColumnFamilyQuery hbaseColumnFamilyQuery : hbaseColumnFamilyQueryList) {
                List<HbaseColumnQuery> columns = hbaseColumnFamilyQuery.getColumns();
                for (HbaseColumnQuery column : columns) {
                    filterList.addFilter(getSingleColumnValueFilter(hbaseColumnFamilyQuery, column));
                }
            }
            scan.setFilter(filterList);
            return table.getScanner(scan);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

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
    @Override
    public List<Map<String, Object>> areaScanByFilter(String tableName, String startRow, String endRow, List<HbaseColumnFamily> hbaseColumnFamilyList, List<HbaseColumnFamilyQuery> columnFamilyQueryList) throws IOException {
        HTable table = (HTable) connection.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();

        List<Map<String, Object>> resultList = new ArrayList<>();
        //设置开始行
        scan.setStartRow(Bytes.toBytes(startRow));
        //设置结束行
        scan.setStopRow(Bytes.toBytes(endRow));
        //确定需要返回哪些列族中的哪些列
        scan = addScanColumnFamilyAndScanColumn(hbaseColumnFamilyList, scan);
        //确定查询的过滤条件
        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
        for (HbaseColumnFamilyQuery hbaseColumnFamily : columnFamilyQueryList) {
            List<HbaseColumnQuery> columns = hbaseColumnFamily.getColumns();
            if (columns != null && columns.size() > 0) {
                for (HbaseColumnQuery column : columns) {
                    filterList.addFilter(getSingleColumnValueFilter(hbaseColumnFamily, column));
                }
            }
        }
        if (filterList.getFilters() != null) {
            scan.setFilter(filterList);
        }
        ResultScanner resultScanner = table.getScanner(scan);
        for (Result result = resultScanner.next(); result != null; result = resultScanner.next()) {
            List<Cell> cellList = result.listCells();
            if (cellList != null && cellList.size() > 0) {
                resultList.add(getResiltMap(cellList));
            }
        }
        return resultList;
    }

    private SingleColumnValueFilter getSingleColumnValueFilter(HbaseColumnFamilyQuery hbaseColumnFamily, HbaseColumnQuery column) {
        SingleColumnValueFilter singleColumnValueFilter = new SingleColumnValueFilter(Bytes.toBytes(hbaseColumnFamily.getColumnFamilyCode()), Bytes.toBytes(column.getName()), column.getCompareOp(), Bytes.toBytes(column.getValue()));
        /*
         * 这个方法很重要，需要注意，当此过滤器过滤时，如果遇到该列值为NULL的情况，
         * 如果设置的参数为true，则会过滤掉这一行，如果设置的参数为false，
         * 那么则会把这一行的结果返回，默认为false
         */
        singleColumnValueFilter.setFilterIfMissing(true);
        return singleColumnValueFilter;
    }
}
