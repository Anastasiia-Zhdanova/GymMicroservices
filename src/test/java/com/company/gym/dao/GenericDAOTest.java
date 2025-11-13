package com.company.gym.dao;

import com.company.gym.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TestEntity implements Serializable {
    private Long id;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}

@ExtendWith(MockitoExtension.class)
public class GenericDAOTest {

    @Mock
    private Session session;
    @Mock
    private Transaction transaction;
    @Mock
    private Query<TestEntity> query;

    private TestGenericDAO dao;
    private TestEntity testEntity;
    private final Long testId = 1L;

    private static class TestGenericDAO extends GenericDAO<TestEntity, Long> {
        public TestGenericDAO(Class<TestEntity> entityClass) {
            super(entityClass);
        }
        @Override
        protected Long getEntityId(TestEntity entity) {
            return entity.getId();
        }
    }

    @BeforeEach
    void setUp() {
        dao = new TestGenericDAO(TestEntity.class);
        testEntity = new TestEntity();
        testEntity.setId(testId);

        lenient().when(session.beginTransaction()).thenReturn(transaction);
        lenient().when(session.merge(any(TestEntity.class))).thenReturn(testEntity);
    }

    private MockedStatic<HibernateUtil> mockHibernateUtil() {
        MockedStatic<HibernateUtil> mockedHibernateUtil = mockStatic(HibernateUtil.class);
        mockedHibernateUtil.when(HibernateUtil::getSessionFactory).thenReturn(mock(org.hibernate.SessionFactory.class));
        when(HibernateUtil.getSessionFactory().openSession()).thenReturn(session);
        return mockedHibernateUtil;
    }

    @Test
    void save_Success() {
        try (MockedStatic<HibernateUtil> mockedUtil = mockHibernateUtil()) {
            TestEntity result = dao.save(testEntity);

            verify(session).beginTransaction();
            verify(session).persist(testEntity);
            verify(transaction).commit();
            verify(session).close();
            assertEquals(testEntity, result);
        }
    }

    @Test
    void save_RollbackOnException() {
        doThrow(new RuntimeException("DB error")).when(session).persist(any());

        try (MockedStatic<HibernateUtil> mockedUtil = mockHibernateUtil()) {
            RuntimeException exception = assertThrows(RuntimeException.class, () -> dao.save(testEntity));
            assertTrue(exception.getMessage().contains("Could not save"));

            verify(session).beginTransaction();
            verify(transaction).rollback();
            verify(session).close();
        }
    }

    @Test
    void update_Success() {
        try (MockedStatic<HibernateUtil> mockedUtil = mockHibernateUtil()) {
            TestEntity result = dao.update(testEntity);

            verify(session).beginTransaction();
            verify(session).merge(testEntity);
            verify(transaction).commit();
            verify(session).close();
            assertEquals(testEntity, result);
        }
    }

    @Test
    void update_RollbackOnException() {
        when(session.merge(any())).thenThrow(new RuntimeException("DB error"));

        try (MockedStatic<HibernateUtil> mockedUtil = mockHibernateUtil()) {
            RuntimeException exception = assertThrows(RuntimeException.class, () -> dao.update(testEntity));
            assertTrue(exception.getMessage().contains("Could not update"));

            verify(session).beginTransaction();
            verify(transaction).rollback();
            verify(session).close();
        }
    }

    @Test
    void delete_Success_NotContained() {
        when(session.contains(testEntity)).thenReturn(false);

        try (MockedStatic<HibernateUtil> mockedUtil = mockHibernateUtil()) {
            dao.delete(testEntity);

            verify(session).beginTransaction();
            verify(session).merge(testEntity);
            verify(session).remove(testEntity);
            verify(transaction).commit();
            verify(session).close();
        }
    }

    @Test
    void delete_Success_Contained() {
        when(session.contains(testEntity)).thenReturn(true);

        try (MockedStatic<HibernateUtil> mockedUtil = mockHibernateUtil()) {
            dao.delete(testEntity);

            verify(session).beginTransaction();
            verify(session, never()).merge(any());
            verify(session).remove(testEntity);
            verify(transaction).commit();
            verify(session).close();
        }
    }

    @Test
    void delete_RollbackOnException() {
        when(session.contains(any())).thenThrow(new RuntimeException("DB error"));

        try (MockedStatic<HibernateUtil> mockedUtil = mockHibernateUtil()) {
            RuntimeException exception = assertThrows(RuntimeException.class, () -> dao.delete(testEntity));
            assertTrue(exception.getMessage().contains("Could not delete"));

            verify(session).beginTransaction();
            verify(transaction).rollback();
            verify(session).close();
        }
    }

    @Test
    void findById_Found() {
        when(session.find(TestEntity.class, testId)).thenReturn(testEntity);

        try (MockedStatic<HibernateUtil> mockedUtil = mockHibernateUtil()) {
            TestEntity result = dao.findById(testId);

            assertEquals(testEntity, result);
            verify(session).close();
        }
    }

    @Test
    void findById_NotFound() {
        when(session.find(TestEntity.class, testId)).thenReturn(null);

        try (MockedStatic<HibernateUtil> mockedUtil = mockHibernateUtil()) {
            TestEntity result = dao.findById(testId);

            assertNull(result);
            verify(session).close();
        }
    }

    @Test
    void findAll_ReturnsList() {
        List<TestEntity> expectedList = Collections.singletonList(testEntity);
        when(session.createQuery(anyString(), eq(TestEntity.class))).thenReturn(query);
        when(query.list()).thenReturn(expectedList);

        try (MockedStatic<HibernateUtil> mockedUtil = mockHibernateUtil()) {
            List<TestEntity> result = dao.findAll();

            assertEquals(expectedList, result);
            verify(session).close();
        }
    }

    @Test
    void findAll_ReturnsEmptyList() {
        List<TestEntity> expectedList = Collections.emptyList();
        when(session.createQuery(anyString(), eq(TestEntity.class))).thenReturn(query);
        when(query.list()).thenReturn(expectedList);

        try (MockedStatic<HibernateUtil> mockedUtil = mockHibernateUtil()) {
            List<TestEntity> result = dao.findAll();

            assertEquals(expectedList, result);
            verify(session).close();
        }
    }
}