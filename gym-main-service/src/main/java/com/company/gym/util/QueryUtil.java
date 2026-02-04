package com.company.gym.util;

import com.company.gym.entity.Training;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.Date;

public class QueryUtil {

    private QueryUtil() {}

    public static Query<Training> getTrainingQuery(String username, Date fromDate, Date toDate, Session session, StringBuilder hql) {

        Query<Training> query = session.createQuery(hql.toString(), Training.class);

        query.setParameter("username", username);

        if (fromDate != null) {
            query.setParameter("fromDate", fromDate);
        }
        if (toDate != null) {
            query.setParameter("toDate", toDate);
        }

        return query;
    }
}