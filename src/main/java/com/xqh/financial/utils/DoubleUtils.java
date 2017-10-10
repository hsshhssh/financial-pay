package com.xqh.financial.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by hssh on 2017/5/13.
 */
public class DoubleUtils
{

    /**
     * double 相乘
     * @param a
     * @param b
     * @return
     */
    public static double mul(double a, double b)
    {
        if(a == 0 || b == 0)
        {
            return 0;
        }

        BigDecimal abd = new BigDecimal(a);
        BigDecimal bbd = new BigDecimal(b);

        return abd.multiply(bbd).setScale(4, RoundingMode.HALF_UP).doubleValue();

    }


    /**
     * a + b
     * @param a
     * @param b
     * @return
     */
    public static double add(double a, double b)
    {
        if(a == 0)
        {
            return b;
        }

        if(b == 0)
        {
            return a;
        }

        BigDecimal abd = new BigDecimal(a);
        BigDecimal bbd = new BigDecimal(b);

        return abd.add(bbd).setScale(4, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * a - b
     * @param a
     * @param b
     * @return
     */
    public static double sub(double a, double b)
    {
        BigDecimal abd = new BigDecimal(a);
        BigDecimal bbd = new BigDecimal(b);

        return abd.subtract(bbd).setScale(4, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * a / b
     * @param a
     * @param b
     * @return
     */
    public static double div(double a, double b)
    {
        if(b == 0)
        {
            throw new IllegalArgumentException("a/b => b != 0");
        }

        BigDecimal abd = new BigDecimal(a);
        BigDecimal bbd = new BigDecimal(b);

        return abd.divide(bbd, 4, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * a / b
     * @param a
     * @param b
     * @return
     */
    public static double sub(double a, double... b)
    {
        if(b.length == 0)
        {
            throw new IllegalArgumentException("a/b => b != 0");
        }

        BigDecimal abd = new BigDecimal(a);
        for (double v : b)
        {
            BigDecimal vbd = new BigDecimal(v);
            abd = abd.subtract(vbd).setScale(4, RoundingMode.HALF_UP);
        }


        return abd.doubleValue();
    }

}
