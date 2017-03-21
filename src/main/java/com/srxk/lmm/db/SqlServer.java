package com.srxk.lmm.db;

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
 *
 */
public class SqlServer{

    private List<String> sqls;

    public void parseSql( List<ExcelData> datas ){
        /*
          一个excel行会产生8条sql语句，10行excel则会产生80条sql语句，都线性保存到sqls中
          也就是说，生产sql的时候每8条sql语句要执行一个事物
         */
        sqls = new ArrayList<>();

        for( ExcelData data : datas ) {
            sqls.add( buildFitemss00Sql( data ) );
            sqls.addAll( buildAccvouchSql( data ) );
        }
    }

    public void printSql(){
        System.out.println( "生成的sql语句如下:" );
        int index = 1;
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
                "      cDefine10) " +
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
        int inoId = getMaxInoId();//自增，对应凭证的 记 。一个项目下所有的分录公用一个ino_id
        System.out.println( "inoId is " + inoId );

        //查询供应商id
        String supplyId = this.getSupplierIdFromName( data.getSupplier() ) + "";

        /**********************************  分录1  *************************************/
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

        /************************************  分录2  *************************************/
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

        /************************************  分录5  *************************************/
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

    private int getMaxInoId(){
        PreparedStatement pst = null;
        ResultSet rs = null;
        Connection con = DatabaseUtil.INSTANCE.getConnection();
        String sql = "SELECT TOP 1 ino_id FROM GL_accvouch ORDER BY ino_id DESC";
        try {
            pst = con.prepareStatement( sql );

            rs = pst.executeQuery();
//            JdbcUtils.printResultSet(rs);
            while( rs.next() ) {
                return rs.getInt( 1 );
            }
            return 1;

        } catch( SQLException e ) {
            e.printStackTrace();
        } finally {

            DatabaseUtil.INSTANCE.close( rs, pst, con );
        }
        return -1;

    }

    /**
     * 查取供应商id
     *
     * @param supplierName
     * @return
     */
    private int getSupplierIdFromName( String supplierName ){
        PreparedStatement pst = null;
        ResultSet rs = null;
        Connection con = DatabaseUtil.INSTANCE.getConnection();
        String sql = "SELECT TOP 1 id FROM supplier ORDER BY ino_id DESC";
        try {
            pst = con.prepareStatement( sql );
            rs = pst.executeQuery();

            while( rs.next() ) {
                return rs.getInt( 1 );
            }

        } catch( SQLException e ) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.INSTANCE.close( rs, pst, con );
        }
        return -1;
    }

    /**
     * 运行传入的sql语句
     */
    public void run(){
        List<String> excelRowSql = new ArrayList<>(  );

        int index = 1;
        for( String sql : sqls ) {
            excelRowSql.add( sql );
            if( index++ % 8 == 0){
                run( excelRowSql );
                excelRowSql.clear();
            }
        }

    }

    /**
     * 采用事物的方式处理一行excel数据
     *
     * @param excelRowSql   一行excel数据所需要的8条sql语句
     */
    private void run( List<String> excelRowSql ){
        System.out.println( "开始执行sql写入" + excelRowSql);
        System.out.println();
        Connection con = DatabaseUtil.INSTANCE.getConnection();
        PreparedStatement pst = null;
        try {
            con.setAutoCommit( false );
            for( String sql : excelRowSql ) {
                pst = con.prepareStatement( sql );
                pst.executeUpdate();
                break;//测试环境，仅仅执行第一条语句
            }
            con.commit();

        } catch( SQLException e ) {
            e.printStackTrace();
            try {
                con.rollback();
            } catch( SQLException e1 ) {
                e1.printStackTrace();
            }
        } finally {
            try {
                con.setAutoCommit(true);//设置事务提交方式为自动提交：

            } catch (SQLException e) {
               e.printStackTrace();
            }

            DatabaseUtil.INSTANCE.close( null, pst, con );
        }
    }
}
