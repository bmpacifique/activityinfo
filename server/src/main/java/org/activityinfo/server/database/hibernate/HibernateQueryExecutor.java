package org.activityinfo.server.database.hibernate;

import org.activityinfo.store.mysql.cursor.QueryExecutor;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.jdbc.AbstractReturningWork;
import org.hibernate.jdbc.ReturningWork;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import java.sql.*;
import java.util.List;
import java.util.logging.Logger;


public class HibernateQueryExecutor implements QueryExecutor {
    
    private static final Logger LOGGER = Logger.getLogger(HibernateQueryExecutor.class.getName());
    
    private final Provider<EntityManager> entityManager;

    @Inject
    public HibernateQueryExecutor(Provider<EntityManager> entityManager) {
        this.entityManager = entityManager;
    }

    private <T> T doWork(ReturningWork<T> worker) {
        HibernateEntityManager hibernateEntityManager = (HibernateEntityManager) entityManager.get();
        return hibernateEntityManager.getSession().doReturningWork(worker);
    }

    @Override
    public ResultSet query(final String sql) {
        LOGGER.fine("Executing query: " + sql);

        return doWork(new AbstractReturningWork<ResultSet>() {
            @Override
            public ResultSet execute(Connection connection) throws SQLException {
                try {
                    Statement statement = connection.createStatement();
                    return statement.executeQuery(sql);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public int update(final String sql, final List<?> parameters) {
        return doWork(new AbstractReturningWork<Integer>() {
            @Override
            public Integer execute(Connection connection) throws SQLException {

                PreparedStatement statement = connection.prepareStatement(sql);
                for (int i = 0; i < parameters.size(); i++) {
                    statement.setObject(i + 1, parameters.get(i));
                }
                return statement.executeUpdate();
            }
        });
    }
}

