package com.example.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 金额工具类
 *
 * @author suhai
 * @since 2023-11-09
 */
public class AmountUtils {
    /**
     * 元/分换算比例
     */
    private static final int FEN_FACTORS = 100;

    /**
     * 取整策略，默认为四舍五入
     */
    private static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_UP;

    /**
     * 金额单位转换：元转分（四舍五入）
     *
     * @param amount 金额
     * @return 以分为单位的BigDecimal
     */
    public static BigDecimal yuanToFen(String amount) {
        BigDecimal yuan = new BigDecimal(amount);
        BigDecimal fen = yuan.multiply(new BigDecimal(FEN_FACTORS));
        return fen.setScale(0, DEFAULT_ROUNDING_MODE);
    }

    /**
     * 金额单位转换：分转元（保留2位小数位精度）
     *
     * @param amount 金额
     * @return 以元为单位的BigDecimal
     */
    public static BigDecimal fenToYuan(int amount) {
        BigDecimal fen = new BigDecimal(amount);
        return fen.divide(new BigDecimal(FEN_FACTORS), 2, DEFAULT_ROUNDING_MODE);
    }

    /**
     * 金额单位转换：分转元（保留2位小数位精度，消除尾随的0）
     *
     * @param amount 金额
     * @return 以元为单位的BigDecimal
     */
    public static BigDecimal fenToYuanAndStripZeros(int amount) {
        BigDecimal yuan = fenToYuan(amount);
        return yuan.stripTrailingZeros();
    }
}
