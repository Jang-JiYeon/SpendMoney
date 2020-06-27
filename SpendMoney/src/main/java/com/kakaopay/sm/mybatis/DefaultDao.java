package com.kakaopay.sm.mybatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class DefaultDao {

    @Autowired
    @Qualifier("sqlSession")
    private SqlSession sqlSession;
    
    /**
     * 입력 처리 SQL mapping 을 실행한다.
     * @param queryId
     *        - 입력 처리 SQL mapping 쿼리 ID
     * @param parameterObject
     *        - 입력 처리 SQL mapping 입력 데이터를 세팅한 파라메터 객체(보통 VO 또는 Map)
     * @return 입력 시 selectKey 를 사용하여 key 를 딴 경우 해당 key
     */
    public Object insert(String queryId, Object parameterObject) {
        return sqlSession.insert(queryId, parameterObject);
    }

    /**
     * 수정 처리 SQL mapping 을 실행한다.
     * @param queryId
     *        - 수정 처리 SQL mapping 쿼리 ID
     * @param parameterObject
     *        - 수정 처리 SQL mapping 입력 데이터(key 조건 및 변경 데이터)를 세팅한 파라메터 객체(보통 VO 또는 Map)
     * @return DBMS가 지원하는 경우 update 적용 결과 count
     */
    public int update(String queryId, Object parameterObject) {
        return sqlSession.update(queryId, parameterObject);
    }

    /**
     * 삭제 처리 SQL mapping 을 실행한다.
     * @param queryId
     *        - 삭제 처리 SQL mapping 쿼리 ID
     * @param parameterObject
     *        - 삭제 처리 SQL mapping 입력 데이터(일반적으로 key 조건)를 세팅한 파라메터 객체(보통 VO 또는 Map)
     * @return DBMS가 지원하는 경우 delete 적용 결과 count
     */
    public int delete(String queryId, Object parameterObject) {
        return sqlSession.delete(queryId, parameterObject);
    }
    
    public Object selectByPk(String queryId, Object parameterObject) {
        return sqlSession.selectOne(queryId, parameterObject);
    }
    
    public Object selectByPk(String queryId) {
        return sqlSession.selectOne(queryId);
    }

    @SuppressWarnings("rawtypes")
    public List list(String queryId, Object parameterObject) {
        return sqlSession.selectList(queryId, parameterObject);
    }

    @SuppressWarnings("rawtypes")
    public List listWithPaging(String queryId, Object parameterObject, int pageIndex, int pageSize) {
        int skipResults = pageIndex * pageSize;
        int maxResults = (pageIndex * pageSize) + pageSize;
        
        RowBounds rowBounds = new RowBounds(maxResults, skipResults);
        
        return sqlSession.selectList(queryId, parameterObject, rowBounds);
    }

    /**
     * 데이터 등록한다.
     * @param hm - 등록할 정보가 담긴 HashMap
     * @return 등록 건수
     * @exception Exception
     */
    @SuppressWarnings("rawtypes") 
    public int insert(String id, HashMap hm) throws Exception {
        return (Integer)insert(id, (Object)hm);
    }

    /**
     * 데이터 수정한다.
     * @param hm - 수정할 정보가 담긴 HashMap
     * @return 수정 건수
     * @exception Exception
     */
    @SuppressWarnings("rawtypes") 
    public int update(String id, HashMap hm) throws Exception {
        return update(id, (Object)hm);
    }

    /**
     * 데이터 삭제한다.
     * @param hm - 삭제할 정보가 담긴 HashMap
     * @return 삭제 건수 
     * @exception Exception
     */
    @SuppressWarnings("rawtypes")
    public int delete(String id, HashMap hm) throws Exception {
        return delete(id, (Object)hm);
    }
    
    
    @SuppressWarnings("rawtypes")
    public Object select(String id, HashMap param) throws Exception {
        return selectByPk(id, param);
    }
    
    @SuppressWarnings("rawtypes")
    public Object select(String id, String param) throws Exception {
        return selectByPk(id, param);
    }

    /**
     * 데이터 목록을 조회한다.
     * @param hm - 조회할 정보가 담긴 HashMap
     * @return 조회된 데이터 리스트
     * @exception Exception
     */
    @SuppressWarnings("rawtypes")
    public List selectList(String id, HashMap hm) throws Exception {
        return list(id, hm);
    }
    
    /**
     * 데이터 목록을 조회한다.
     * @param param - 조회할 정보 param
     * @return 조회된 데이터 리스트
     * @exception Exception
     */
    @SuppressWarnings("rawtypes")
    public List selectList(String id, String param) throws Exception {
        return list(id, param);
    }
    
    /**
     * 데이터 목록을 조회한다.
     * @param hm - 조회할 정보가 담긴 Map
     * @return 조회된 데이터 리스트
     * @exception Exception
     */
    @SuppressWarnings("rawtypes")
    public List selectList(String id, Map hm) throws Exception {
        return list(id, hm);
    }

    /**
     * @return the sqlSession
     */
    public SqlSession getSqlSession() {
        return sqlSession;
    }

    /**
     * @param sqlSession the sqlSession to set
     */
    public void setSqlSession(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

	public String select(String id) {
		 return (String) selectByPk(id);
	}
}
