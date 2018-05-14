package cn.zyp.datasource;

import java.util.LinkedList;
import java.util.List;

public class PoolState {
    private List<PoolConnection> activeConnections = new LinkedList<PoolConnection>();
    private List<PoolConnection> idleConnections = new LinkedList<PoolConnection>();

    public List<PoolConnection> getActiveConnections() {
        return activeConnections;
    }
    public List<PoolConnection> getIdleConnections() {
        return idleConnections;
    }

}
