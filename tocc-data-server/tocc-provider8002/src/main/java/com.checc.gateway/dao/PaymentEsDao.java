package com.checc.gateway.dao;

import com.checc.gateway.entities.PaymentEs;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PaymentEsDao extends ElasticsearchRepository<PaymentEs,Long> {

}
