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
     int id;
    /**
     * 所属BU
     */
     String bu;
    /**
     * 订单号
     */
     String  orderId;
    /**
     * 业务员
     */
     String salesman;

    /**
     * 产品名称
     */
     String productName;

    /**
     * 总部结算价
     */
     double settlementPrice;

    /**
     * 佣金
     */
     double commission;


    /**
     * 供应商结算价
     */
     double settlementPrice1;

    /**
     * 专线返点
     */
     double rebate;


    /**
     * 应付供应商金额
     */
     double payables;

    /**
     * 供应商
     */
     String Supplier;

    /**
     * 出游时间
     */
     Date playTime;

    /**
     * 制单时间
     */
     Date createTime;

    /**
     * 制单人
     */
     String creater;

    /**
     * 客户单位
     */
     String client;

    /**
     * 客户单位，用于总经办第八条分录
     */
     String Supplier1;
    /**
     * 获取项目编码 = 出游时间+产品名称
     *
     * @return 项目编码
     */
    public String getItemCode(){

        if( playTime == null ){
            return "";
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime( playTime );

        int month = cal.get( Calendar.MONTH ) + 1;
        String monthStr = month > 10 ? month + "" : "0" + month;

        int dayOfMonth = cal.get( Calendar.DAY_OF_MONTH );
        String dayOfMonthStr = dayOfMonth > 10 ? dayOfMonth + "" : "0" + dayOfMonth;

        String time = (cal.get( Calendar.YEAR ) -2000)+ monthStr + dayOfMonthStr;
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
