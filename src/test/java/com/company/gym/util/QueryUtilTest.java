package com.company.gym.util;

import com.company.gym.entity.Training;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@MockitoSettings
public class QueryUtilTest {

    @Mock
    private Session session;

    @Mock
    private Query<Training> query;

    private final String USERNAME = "test.user";
    private final Date MOCK_DATE = new Date();

    @Test
    void getTrainingQuery_AllParameters_SetsAllParameters() {
        StringBuilder hql = new StringBuilder("SELECT t FROM Training t WHERE u.username = :username AND t.trainingDate >= :fromDate AND t.trainingDate <= :toDate");

        when(session.createQuery(anyString(), Mockito.eq(Training.class))).thenReturn(query);

        when(query.setParameter(anyString(), any())).thenReturn(query);

        Query<Training> result = QueryUtil.getTrainingQuery(USERNAME, MOCK_DATE, MOCK_DATE, session, hql);

        assertNotNull(result);
        verify(query).setParameter("username", USERNAME);
        verify(query).setParameter("fromDate", MOCK_DATE);
        verify(query).setParameter("toDate", MOCK_DATE);
    }

    @Test
    void getTrainingQuery_NoDateParameters_SetsOnlyUsername() {
        StringBuilder hql = new StringBuilder("SELECT t FROM Training t WHERE u.username = :username");

        when(session.createQuery(anyString(), Mockito.eq(Training.class))).thenReturn(query);

        when(query.setParameter(anyString(), Mockito.eq(USERNAME))).thenReturn(query);

        Query<Training> result = QueryUtil.getTrainingQuery(USERNAME, null, null, session, hql);

        assertNotNull(result);
        verify(query).setParameter("username", USERNAME);
        verify(query, never()).setParameter("fromDate", MOCK_DATE);
        verify(query, never()).setParameter("toDate", MOCK_DATE);
    }
}