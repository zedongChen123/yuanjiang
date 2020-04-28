package com.checc.gateway.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "megacorp",type = "employee")
public class PaymentEs implements Serializable {
    private Long id;
    private String serial;

    public String toString(){
        return id+ serial;
    }
}