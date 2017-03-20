package com.srxk.lmm.db;

import com.alibaba.druid.util.JdbcUtils;
import com.srxk.lmm.pojo.ExcelData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liulaoye on 17-3-20.
 */
public class SqlServer{

    public List<String> parseSql( List<ExcelData> datas ){
        /*
          一个excel行会产生8条sql语句，10行excel则会产生80条sql语句，都线性保存到sqls中
          也就是说，生产sql的时候每8条sql语句要执行一个事物
         */
        List<String> sqls = new ArrayList<>();

        for( ExcelData data : datas ) {
            sqls.add( buildFitemss00Sql( data ) );
            sqls.addAll( buildAccvouchSql( data ) );
        }

        return sqls;
    }

    public void printSql( List<String> sqls ){
        System.out.println( "生成的sql语句如下:" );
        int index = 0;
        for( String sql : sqls ) {
            System.out.println( sql );
            if( index++ % 8 == 0 ) {
                System.out.println( "===========================================" );
            }
        }
    }

    /**
     * 生成一个凭证的7条分录的sql语句
     *
     * @param data excel数据
     * @return 7条分录的sql语句
     */
    private List<String> buildAccvouchSql( ExcelData data ){
        final SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd" );
        List<String> accvouchSqls = new ArrayList<>();
        String sqlFormat = "INSERT INTO GL_accvouch" +
                "      (iperiod, csign, isignseq, ino_id, inid, dbill_date, idoc, cbill, ibook, " +
                "      cdigest," +
                "      ccode, md, mc, md_f, mc_f, nfrat, nd_s, nc_s, " +
                "      cn_id, dt_date, ccus_id, csup_id, citem_id, " +
                "      citem_class, cname, ccode_equal,  bdelete,  " +
                "      doutbilldate, coutsign, " +
                "      bvouchedit, bvouchAddordele, bvouchmoneyhold, " +
                "      bvalueedit, bcodeedit, ccodecontrol, bPCSedit, bDeptedit, bItemedit, bCusSupInput, " +
                "      cDefine10)\n" +
                "VALUES (1,'记',1,%d,%d,'%s',-1,'%s',0," +
                "'%s'," +
                "'%s',%f,%f,0,0,0,0,0," +
                "%s,%s,%s,%s,'%s'," +
                "'00',%s,'%s',0," +
                "'%s',''," +
                "1,0,0," +
                "1,1,%s,1,1,1,0," +
                "'=lly')";


        //处理第一条分录
        int inoId = 1000;//自增，对应凭证的 记 。一个项目下所有的分录公用一个ino_id

        /*************************************  分录1  *************************************/
        String sql = String.format( sqlFormat,
                inoId, 1, formatter.format( data.getCreateTime() ), data.getCreater(),
                "确认" + data.getBu() + "线上项目《" + data.getItemCode() + "》收入",
                "1122", data.getSettlementPrice(), 0f,
                "'" + data.getOrderId() + "'", "'" + formatter.format( data.getPlayTime() ) + "'", "'0006'", "NULL", data.getItemCode(),
                "'" + data.getSalesman() + "'", "60010301,60010302,2202",
                formatter.format( data.getCreateTime() ),
                "NULL"
        );
        accvouchSqls.add( sql );

        /*************************************  分录2  *************************************/
        sql = String.format( sqlFormat,
                inoId, 2, formatter.format( data.getCreateTime() ), data.getCreater(),
                "确认" + data.getBu() + "线上项目《" + data.getItemCode() + "》收入",
                "60010301", 0f, data.getSettlementPrice(),
                "NULL", "NULL", "NULL", "NULL", data.getItemCode(),
                "NULL", "1122,64010301,64010302",
                formatter.format( data.getCreateTime() ),
                "NULL"
        );
        accvouchSqls.add( sql );

        /*************************************  分录3  *************************************/
        sql = String.format( sqlFormat,
                inoId, 3, formatter.format( data.getCreateTime() ), data.getCreater(),
                "确认" + data.getBu() + "线上项目《" + data.getItemCode() + "》收入",
                "1122", data.getCommission(), 0f,
                "'" + data.getOrderId() + "'", "'" + formatter.format( data.getPlayTime() ) + "'", "'0003'", "NULL", data.getItemCode(),
                "'" + data.getSalesman() + "'", "60010301,60010302,2202",
                formatter.format( data.getCreateTime() ),
                "'***'"
        );
        accvouchSqls.add( sql );

        /*************************************  分录4  *************************************/
        sql = String.format( sqlFormat,
                inoId, 4, formatter.format( data.getCreateTime() ), data.getCreater(),
                "确认" + data.getBu() + "线上项目《" + data.getItemCode() + "》收入",
                "60010302", 0f, data.getCommission(),
                "NULL", "NULL", "NULL", "NULL", data.getItemCode(),
                "NULL", "1122,64010301,64010302",
                formatter.format( data.getCreateTime() ),
                "'***'"
        );
        accvouchSqls.add( sql );

        /*************************************  分录5  *************************************/
        sql = String.format( sqlFormat,
                inoId, 5, formatter.format( data.getCreateTime() ), data.getCreater(),
                "确认" + data.getBu() + "线上项目《" + data.getItemCode() + "》成本",
                "64010301", data.getSettlementPrice1(), 0f,
                "NULL", "NULL", "NULL", "NULL", data.getItemCode(),
                "NULL", "60010301,60010302,2202",
                formatter.format( data.getCreateTime() ),
                "'***'"
        );
        accvouchSqls.add( sql );

        /*************************************  分录6  *************************************/
        sql = String.format( sqlFormat,
                inoId, 6, formatter.format( data.getCreateTime() ), data.getCreater(),
                "确认" + data.getBu() + "线上项目《" + data.getItemCode() + "》成本",
                "64010302", -data.getRebate(), 0f,
                "NULL", "NULL", "NULL", "NULL", data.getItemCode(),
                "NULL", "1122,64010301,64010302",
                formatter.format( data.getCreateTime() ),
                "'***'"
        );
        accvouchSqls.add( sql );

        String supplyId = "10011";//供应商id
        /*************************************  分录7  *************************************/
        sql = String.format( sqlFormat,
                inoId, 7, formatter.format( data.getCreateTime() ), data.getCreater(),
                "确认" + data.getBu() + "线上项目《" + data.getItemCode() + "》成本",
                "2202", 0f, data.getPayables(),
                "'" + data.getOrderId() + "'", "'" + formatter.format( data.getPlayTime() ) + "'", "NULL", "'" + supplyId + "'", data.getItemCode(),
                "'" + data.getSalesman() + "'", "60010301,60010302,2202",
                formatter.format( data.getCreateTime() ),
                "'***'"
        );
        accvouchSqls.add( sql );
        return accvouchSqls;
    }

    private String buildFitemss00Sql( ExcelData data ){
        final SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd" );

        return "INSERT INTO fitemss00 (citemcode,citemname,bclose,citemccode,日期,供应商) " +
                "VALUES ('" + data.getItemCode() + "'," +
                "'" + data.getOrderId() + "', " +
                0 + ", " +
                "'01', " +
                "'" + format.format( data.getPlayTime() ) + "', " +
                "'" + data.getSupplier() + "' " +
                ")";
    }

    /**
     * 运行传入的sql语句
     *
     * @param sqls
     */
    public void run( List<String> sqls ){

        String sql = "select 1";
        PreparedStatement pst = null;
//        String sql = "insert into Rainfall ( Timestamps,ClientId,RainfallAmount,CreateTime ) values ( ?,?,?,? )";
        Connection con = DatabaseUtil.INSTANCE.getConnection();

        try {
            pst = con.prepareStatement( sql );

//            pst.executeUpdate();

            ResultSet rs = pst.executeQuery();
            JdbcUtils.printResultSet( rs );
//            JdbcUtils.printResultSet(  );

        } catch( SQLException e ) {
            e.printStackTrace();
        } finally {

            DatabaseUtil.INSTANCE.close( null, pst, con );
//            DatabaseUtil.INSTANCE.close( null, pst, con );
        }
    }
}
