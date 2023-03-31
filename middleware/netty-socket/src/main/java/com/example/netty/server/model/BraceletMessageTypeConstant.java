package com.example.netty.server.model;

/**
 * 协议报文数据类型，即报文中第三组数据
 */
public class BraceletMessageTypeConstant {
    /**
     * 心跳检测，链路保持协议，此报文类型包含日期，步数，翻滚次数，电量百分数
     * 收到此报文类型，服务器必须响应内容
     */
    public static final String HEARTBEAT_KA = "KA";

    /**
     * 位置数据上报
     */
    public static final String UD = "UD";

    /**
     * 盲点补传数据
     */
    public static final String UD2 = "UD2";

    /**
     * 报警数据上报
     */
    public static final String AL = "AL";

    /**
     * 心率数据
     */
    public static final String HEART = "heart";

    /**
     * 血压数据
     */
    public static final String BLOOD = "blood";
}
