package com.checc.es.service;

import com.alibaba.fastjson.JSON;
import com.checc.es.entity.ElasticEntity;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Component
public class BaseElasticService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * @param idxName   索引名称
     * @param idxSQL    索引描述
     * @return void
     * @throws
     * @since
     */
    public void createIndex(String idxName,String idxSQL){
        try {
            if (this.indexExist(idxName)) {
                log.error(" idxName={} 已经存在,idxSql={}",idxName,idxSQL);
                return;
            }else {
                CreateIndexRequest request = new CreateIndexRequest(idxName);
                buildSetting(request);
                request.mapping(idxSQL, XContentType.JSON);
                CreateIndexResponse res = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
                if (!res.isAcknowledged()) {
                    throw new RuntimeException("初始化失败");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    /** 断某个index是否存在
     * @param idxName index名
     * @return boolean
     * @throws
     * @since
     */
    public boolean indexExist(String idxName) throws Exception {
        GetIndexRequest request = new GetIndexRequest(idxName);
        return  restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
    }

    /** 断某个index是否存在
     * @param idxName index名
     * @return boolean
     * @throws
     * @since
     */
    public boolean isExistsIndex(String idxName) throws Exception {
        return restHighLevelClient.indices().exists(new GetIndexRequest(idxName),RequestOptions.DEFAULT);
    }

    /** 设置分片
     * @param request
     * @return void
     * @throws
     * @since
     */
    public void buildSetting(CreateIndexRequest request){
        request.settings(Settings.builder().put("index.number_of_shards",3)
                .put("index.number_of_replicas",2));
    }

    /**
     * @param idxName index
     * @param entity    对象
     * @return void
     * @throws
     * @since
     */
    public String insertOrUpdateOne(String idxName,String type, ElasticEntity entity) {
        IndexRequest request = new IndexRequest(idxName);
        log.error("Data : id={},entity={}",entity.getId(), JSON.toJSONString(entity.getData()));
        request.type(type);
        request.id(entity.getId());
        request.source(entity.getData(), XContentType.JSON);
        try {
            IndexResponse indexResponse = restHighLevelClient.index(request, RequestOptions.DEFAULT);
            return indexResponse.getIndex()+"/"+indexResponse.getType()+"/"+indexResponse.getId();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** 批量插入数据
     * @param idxName index
     * @param list 带插入列表
     * @return void
     * @throws
     * @since
     */
    public void insertBatch(String idxName, String type, List<ElasticEntity> list) {
        BulkRequest request = new BulkRequest();
        list.forEach(item -> request.add(new IndexRequest(idxName).id(item.getId()).type(type)
                .source(item.getData(), XContentType.JSON)));
        try {
            restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** 批量删除
     * @param idxName index
     * @param idList    待删除列表
     * @return void
     * @throws
     * @since
     */
    public <T> void deleteBatch(String idxName, String type, Collection<T> idList) {
        BulkRequest request = new BulkRequest();
        idList.forEach(item -> {
            request.add(new DeleteRequest(idxName,type,item.toString()));
        });
        try {
            restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param idxName index
     * @param builder   查询参数
     * @param c 结果类对象
     * @return java.util.List<T>
     * @throws
     * @since
     */
    public <T> List<T> search(String idxName, String type, SearchSourceBuilder builder, Class<T> c) {
        SearchRequest request = new SearchRequest(idxName);
        request.source(builder).types(type);
        try {
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            SearchHit[] hits = response.getHits().getHits();
            List<T> res = new ArrayList<>(hits.length);
            for (SearchHit hit : hits) {
                res.add(JSON.parseObject(hit.getSourceAsString(), c));
            }
            return res;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** 删除index
     * @param idxName
     * @return void
     * @throws
     * @since
     */
    public void deleteIndex(String idxName) {
        try {
            if (!this.indexExist(idxName)) {
                log.error(" idxName={} 已经存在",idxName);
                return;
            }
            restHighLevelClient.indices().delete(new DeleteIndexRequest(idxName), RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param idxName
     * @param builder
     * @return void
     * @throws
     * @since
     */
    public void deleteByQuery(String idxName, QueryBuilder builder ,String ...types) {
        DeleteByQueryRequest request = new DeleteByQueryRequest(idxName);
        request.setQuery(builder);
        request.setDocTypes(types);
        //设置批量操作数量,最大为10000
        request.setBatchSize(10000);
        request.setConflicts("proceed");
        try {
            restHighLevelClient.deleteByQuery(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
