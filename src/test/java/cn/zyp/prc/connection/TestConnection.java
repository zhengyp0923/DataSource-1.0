package cn.zyp.prc.connection;

import cn.zyp.datasource.PoolConnection;
import cn.zyp.datasource.PoolDataSource;
import cn.zyp.datasource.UnpoolDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) throws SQLException {
        UnpoolDataSource unpoolDataSource=new UnpoolDataSource();
        Connection connection = unpoolDataSource.getConnection();
        System.out.println(connection);

         PoolDataSource dataSource=new PoolDataSource();
        PoolConnection poolConnection=new PoolConnection(connection,dataSource);
        PoolConnection poolConnection2=new PoolConnection(connection,dataSource);
        System.out.println(poolConnection.equals(poolConnection2));

        System.out.println("-------------------------------------------");

        PoolDataSource poolDataSource=new PoolDataSource();
        poolDataSource.setIdleConnections(0);

        Connection connection1 = poolDataSource.getConnection();
        connection1.close();

//        for (int i = 0; i <11 ; i++) {
//            Connection connection1 = poolDataSource.getConnection();
//            if(i==10){
//                connection1.close();
//            }
//            System.out.println(connection1);
//        }


    }
}
