package com.fubon.robot.batch.Data.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fubon.robot.batch.robot.evdata.TBusCustomer;

@Repository
public interface TBusCustomerRepository extends JpaRepository<TBusCustomer, String> {
	/**
	 * 依照分派時間 去排序 抓取 交送機器人的名單總表 (可能會有上萬筆 ) TODO 使用 query @Param
	 * 
	 * @param receiveUserId 分派給機器人的USER ID 
	 * @param listSatus     狀態 會議有說明 (待撥打 ， 未結案)
	 * @param effDate       有效日期
	 * @param Pageable      最大抓取數量。
	 * @return
	 */
	@Query("select t from TBusCustomer t  where t.receiveUserId in :receiveUserId "
			+ " and t.customerStatus in :listSatus  and t.effDate > :effDate " + " and t.isBlackList ='N' "
			+ " and  t.customerId not in (select n.custId from  TTmHistoryResult n ) "
			+ " order by t.distributeTime asc ")
	public List<TBusCustomer> findListNonShot(@Param("receiveUserId") List<String> receiveUserId,
			@Param("listSatus") List<String> listSatus, @Param("effDate") String effDate ,Pageable pagele);

	@Query(nativeQuery = true, value = "Select LPAD(SEQ_MWSTANO.nextval,7,'0')as SEQNo From dual ")
	public BigDecimal getListForbiddenCallSequence();

	@Query("select t from TBusCustomer t  where t.effDate > :effDate  and t.customer_28 = :customerId and "
			+ "t.receiveUserId not in :userIds ")
	public List<TBusCustomer> findByUserFutureList(@Param("effDate") String effDate,
			@Param("customerId") String customerId,@Param("userIds") List<String> userIds);

}
