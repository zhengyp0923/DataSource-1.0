package cn.zyp.datasource;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

public class PoolConnection implements InvocationHandler {
    private Connection realConnection;
    private Connection proxyConnection;

    private PoolDataSource poolDataSource;

    private static final Class[] interfaces = new Class[]{Connection.class};
    private static final String CLOSE = "close";


    public PoolConnection(Connection realConnection, PoolDataSource poolDataSource) {
        this.poolDataSource = poolDataSource;
        this.realConnection = realConnection;
        this.proxyConnection = (Connection) Proxy.newProxyInstance(realConnection.getClass().getClassLoader(), interfaces, this);
    }


    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (CLOSE.equals(method.getName())) {
            poolDataSource.ConnectionToPool(this);
            return null;
        }
        return method.invoke(realConnection, args);
    }

    public Connection getRealConnection() {
        return realConnection;
    }

    public void setRealConnection(Connection realConnection) {
        this.realConnection = realConnection;
    }

    public Connection getProxyConnection() {
        return proxyConnection;
    }

    public PoolDataSource getPoolDataSource() {
        return poolDataSource;
    }

    public void setPoolDataSource(PoolDataSource poolDataSource) {
        this.poolDataSource = poolDataSource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PoolConnection that = (PoolConnection) o;

        if (realConnection != null ? !realConnection.equals(that.realConnection) : that.realConnection != null)
            return false;
        if (proxyConnection != null ? !proxyConnection.equals(that.proxyConnection) : that.proxyConnection != null)
            return false;
        return poolDataSource != null ? poolDataSource.equals(that.poolDataSource) : that.poolDataSource == null;
    }

    @Override
    public int hashCode() {
        int result = realConnection != null ? realConnection.hashCode() : 0;
        result = 31 * result + (proxyConnection != null ? proxyConnection.hashCode() : 0);
        result = 31 * result + (poolDataSource != null ? poolDataSource.hashCode() : 0);
        return result;
    }
}
