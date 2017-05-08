package com.srxk.lmm.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liulaoye on 17-3-27.
 * 基类
 */
public class AbstractSqlServer{
    private int inoId = -1;
    List<List<String>> sqls = new ArrayList<>();

    public void printSql(){
        System.out.println( "生成的sql语句如下:" );
        int index = 1;
        for( List<String> sql : sqls ) {
            for( String s : sql ) {
                System.out.println( s );
            }
            System.out.println( "==================================================" );
        }
    }

    /**
     * 要取出当前月份的最大ino_id
     *
     * @param period
     * @return
     */
    protected int getMaxInoId( int period ){
        if( inoId == -1 ) {
            PreparedStatement pst = null;
            ResultSet rs = null;
            Connection con = DatabaseUtil.INSTANCE.getConnection();
            String sql = "SELECT TOP 1 ino_id FROM GL_accvouch where iperiod=" + period + " ORDER BY ino_id DESC";
            try {
                pst = con.prepareStatement( sql );

                rs = pst.executeQuery();
//            JdbcUtils.printResultSet(rs);
                if( rs.next() ) {
                    this.inoId = rs.getInt( 1 ) + 1;
                }else{
                    this.inoId = 1;
                }

            } catch( SQLException e ) {
                e.printStackTrace();
            } finally {
                DatabaseUtil.INSTANCE.close( rs, pst, con );
            }
            return inoId;

        } else {
            return ++inoId;
        }
    }

    /**
     * 查取供应商id
     *
     * @param supplierName
     * @return
     */
    String getSupplierIdFromName( String supplierName ){
        PreparedStatement pst = null;
        ResultSet rs = null;
        Connection con = DatabaseUtil.INSTANCE.getConnection();
        String sql = "SELECT cVenCode FROM Vendor WHERE (cVenName = '" + supplierName + "')";

        try {
            pst = con.prepareStatement( sql );
            rs = pst.executeQuery();

            while( rs.next() ) {
                return rs.getString( 1 );
            }

        } catch( SQLException e ) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.INSTANCE.close( rs, pst, con );
        }
        return "";
    }

    /**
     * 运行传入的sql语句
     */
    public void run(){


        int row = 1;
        Connection con = DatabaseUtil.INSTANCE.getConnection();
        PreparedStatement pst = null;
        try {
            con.setAutoCommit( false );
//            con = DatabaseUtil.INSTANCE.getConnection();

            for( List<String> rowSql : sqls ) {

                System.out.println( "开始处理第" + row++ + "条excel数据！" );

                System.out.println();


                for( String sql : rowSql ) {
                    System.out.println( "开始执行sql写入\n" + sql );
                    pst = con.prepareStatement( sql );
                    pst.executeUpdate();
//                break;//测试环境，仅仅执行第一条语句
                }


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
                con.setAutoCommit( true );//设置事务提交方式为自动提交：

            } catch( SQLException e ) {
                e.printStackTrace();
            }

            DatabaseUtil.INSTANCE.close( null, pst, con );
        }

    }

}
