package cn.zyp.datasource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class PoolDataSource implements DataSource {
    private UnpoolDataSource unpoolDataSource = new UnpoolDataSource();
    private PoolState poolState = new PoolState();
    private int maxConnections = 10;
    private int idleConnections = 5;

    private static final int COUNT = 30;

    public PoolDataSource() {
    }

    public PoolDataSource(int maxConnections, int idleConnections) {
        this.maxConnections = maxConnections;
        this.idleConnections = idleConnections;
    }

    /**
     * 获取连接
     *
     * @return
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException {
        //同步

        int i = 0;

        synchronized (poolState) {
            //先判断是否idles是否有空闲的线程
            if (poolState.getIdleConnections().size() > 0) {
                PoolConnection remove = poolState.getIdleConnections().remove(0);
                System.out.println(remove + "从idle中拿出");
                poolState.getActiveConnections().add(remove);
                System.out.println(remove + "被放入active");
                return remove.getProxyConnection();
            }

            //判断actives否已满
            if (poolState.getActiveConnections().size() < maxConnections) {
                System.out.println("active有空闲");
                Connection newConnection = unpoolDataSource.getConnection();
                PoolConnection poolConnection = new PoolConnection(newConnection, this);
                poolState.getActiveConnections().add(poolConnection);
                System.out.println(poolConnection + "被放入active");
                return poolConnection.getProxyConnection();
            }

            //判断actives是否已经超时

            //只能等待
            try {
                System.out.println(Thread.currentThread().getName() + "等待");
                poolState.wait();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        /**
         * 最多调用count
         *  如果得不到  抛出异常
         */
        i++;
        if (i == COUNT) {
            throw new RuntimeException("connection not obtain");
        }
        return getConnection();
    }


    /**
     * 将连接返回池中
     *
     * @param connection
     */
    public void ConnectionToPool(PoolConnection connection) throws SQLException {
        if (connection != null) {
            synchronized (poolState) {
                poolState.getActiveConnections().remove(connection);
                System.out.println(connection + "从活跃池中移除");

                poolState.notify();

                if (poolState.getIdleConnections().size() < idleConnections) {
                    PoolConnection poolConnection = new PoolConnection(connection.getRealConnection(), this);
                    System.out.println(connection.hashCode() + "  " + poolConnection.hashCode() + "    " + connection.equals(poolConnection));
                    poolState.getIdleConnections().add(poolConnection);
                    System.out.println(connection + "返回空闲池中");
                    poolState.notify();

                } else {
                    connection.getRealConnection().close();
                    System.out.println(connection + "被释放");
                }
            }
        }
    }


    public Connection getConnection(String username, String password) throws SQLException {
        return null;
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    public void setLoginTimeout(int seconds) throws SQLException {

    }

    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    public PoolState getPoolState() {
        return poolState;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public int getIdleConnections() {
        return idleConnections;
    }

    public void setIdleConnections(int idleConnections) {
        this.idleConnections = idleConnections;
    }

    public UnpoolDataSource getUnpoolDataSource() {
        return unpoolDataSource;
    }

    public static int getCOUNT() {
        return COUNT;
    }
}
