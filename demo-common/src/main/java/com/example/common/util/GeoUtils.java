package com.example.common.util;

/**
 * geo工具类
 *
 * @author suhai
 * @since 2024-10-10
 */
public class GeoUtils {
    /**
     * 地球半径的默认值（单位：公里）
     */
    private static final double DEFAULT_EARTH_RADIUS = 6371.0;

    /**
     * 根据两个点的经纬度计算它们之间的球面距离（大圆距离）
     * <p>
     * 使用默认的地球半径。
     *
     * @param lngA 第一个点的经度
     * @param latA 第一个点的纬度
     * @param lngB 第二个点的经度
     * @param latB 第二个点的纬度
     * @return 两个点之间的球面距离（单位：公里）
     */
    public static double earthDistanceOfHaversine(double lngA, double latA, double lngB, double latB) {
        return earthDistanceOfHaversine(lngA, latA, lngB, latB, DEFAULT_EARTH_RADIUS);
    }


    /**
     * 根据两个点的经纬度计算它们之间的球面距离（大圆距离）。
     *
     * @param lngA        第一个点的经度
     * @param latA        第一个点的纬度
     * @param lngB        第二个点的经度
     * @param latB        第二个点的纬度
     * @param earthRadius 地球半径（单位：公里）
     * @return 两个点之间的球面距离（单位：公里）
     */
    public static double earthDistanceOfHaversine(double lngA, double latA, double lngB, double latB, double earthRadius) {
        double lngARad = Math.toRadians(lngA);
        double latARad = Math.toRadians(latA);
        double lngBRad = Math.toRadians(lngB);
        double latBRad = Math.toRadians(latB);
        double lngRadDelta = lngBRad - lngARad;
        double latRadDelta = latBRad - latARad;

        double a = Math.pow(Math.sin(latRadDelta / 2), 2) +
                Math.cos(latARad) * Math.cos(latBRad) * Math.pow(Math.sin(lngRadDelta / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadius * c;
    }
}