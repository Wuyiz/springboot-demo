package com.example.common.util;

/**
 * 预测算法工具类-指数平滑算法
 *
 * @author suhai
 * @since 2024-09-24
 */
public class ExponentialSmoothingUtils {
    private final double alpha;

    private double lastSmooth;

    public ExponentialSmoothingUtils(double alpha, double initialSmooth) {
        if (alpha <= 0 || alpha > 1) {
            throw new IllegalArgumentException("Alpha must be between 0 and 1");
        }
        this.alpha = alpha;
        this.lastSmooth = initialSmooth;
    }

    /**
     * 更新平滑值并返回下一个预测值
     *
     * @param observation 当前观测值
     * @return 下一个数据点的预测值
     */
    public double next(double observation) {
        double newSmooth = alpha * observation + (1 - alpha) * lastSmooth;
        lastSmooth = newSmooth;
        return newSmooth;
    }
}