package com.srxk.lmm.pojo;

import lombok.Data;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by liulaoye on 17-3-17.
 * 用来保存excel数据的pojo
 */

@Data
public class ExcelData{
    private int id;
    /**
     * 所属BU
     */
    private String bu;
    /**
     * 订单号
     */
    private int orderId;
    /**
     * 业务员
     */
    private String salesman;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 总部结算价
     */
    private double settlementPrice;

    /**
     * 佣金
     */
    private double commission;


    /**
     * 供应商结算价
     */
    private double settlementPrice1;

    /**
     * 专线返点
     */
    private double rebate;


    /**
     * 应付供应商金额
     */
    private double payables;

    /**
     * 供应商
     */
    private String Supplier;

    /**
     * 出游时间
     */
    private Date playTime;

    /**
     * 制单时间
     */
    private Date createTime;

    /**
     * 制单人
     */
    private String creater;

    /**
     * 获取项目编码 = 出游时间+产品名称
     *
     * @return 项目编码
     */
    public String getItemCode(){

        Calendar cal = Calendar.getInstance();
        cal.setTime( playTime );

        int month = cal.get( Calendar.MONTH ) + 1;
        String monthStr = month > 10 ? month + "" : "0" + month;

        int dayOfMonth = cal.get( Calendar.DAY_OF_MONTH ) + 1;
        String dayOfMonthStr = dayOfMonth > 10 ? dayOfMonth + "" : "0" + dayOfMonth;

        String time = cal.get( Calendar.YEAR ) + monthStr + dayOfMonthStr;
        return time + this.productName;
    }

    @Override
    public String toString(){
        return "ExcelData{" +
                "id=" + id +
                ", bu='" + bu + '\'' +
                ", orderId=" + orderId +
                ", salesman='" + salesman + '\'' +
                ", productName='" + productName + '\'' +
                ", settlementPrice=" + settlementPrice +
                ", commission=" + commission +
                ", settlementPrice1=" + settlementPrice1 +
                ", rebate=" + rebate +
                ", payables=" + payables +
                ", Supplier='" + Supplier + '\'' +
                ", playTime=" + playTime +
                ", createTime=" + createTime +
                ", creater='" + creater + '\'' +
                ", 项目编码='" + this.getItemCode() + '\'' +
                '}';
    }
}
