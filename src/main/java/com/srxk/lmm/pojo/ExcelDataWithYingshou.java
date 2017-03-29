package com.srxk.lmm.pojo;

import lombok.Data;

/**
 * Created by liulaoye on 17-3-17.
 * 用来保存excel数据的pojo
 */

@Data
public class ExcelDataWithYingshou extends ExcelData{
    /**
     * 摘要
     */
    private String summary;

    @Override
    public String toString(){
        return "ExcelDataWithYingshou{" +
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
                ", summary=" + summary +
                ", creater='" + creater + '\'' +
                ", 项目编码='" + this.getItemCode() + '\'' +
                '}';
    }
}
