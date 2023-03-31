package com.example.netty.server.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BraceletMessage implements Serializable {
    private Long id;

    private Date createTime;

    private String originalMsg;

    private String manufacturerName;

    private String deviceId;

    private String dataLen;

    private String dataType;

    private String dataContent;

    private static final long serialVersionUID = 1L;
}