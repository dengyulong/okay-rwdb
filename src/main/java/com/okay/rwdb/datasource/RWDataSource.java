package com.okay.rwdb.datasource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.okay.rwdb.RWContextHolder;

/**
 * 配置数据源
 * @author deng
 *
 */
public class RWDataSource extends AbstractRoutingDataSource{
	private static final Logger LOG = LoggerFactory.getLogger(RWDataSource.class);
	//主库
	private DataSource master;
	//从库key列表
	private List<String> slaveList = new ArrayList<String>();
	//从库的数量
	private int slaveCount;
	//轮询计数,初始为-1
	private AtomicInteger counter = new AtomicInteger(-1);
	

	@Override
	protected Object determineCurrentLookupKey() {
		//LOG.debug("数据源为{}", RWContextHolder.getDB());
		if(RWContextHolder.isMaster()){
			return RWContextHolder.MASTER_NAME; //主库
		}
        return pollingSlaveKey();  //从库
	}


	/**
	 * 设置主库
	 * @param master
	 */
	public void setMaster(DataSource master) {
		setDefaultTargetDataSource(master);
		this.master = master;
	}


	/**
	 * 设置从库列表
	 * @param slaves
	 */
	public void setSlaves(Map<Object, Object> slaves) {
		if(MapUtils.isEmpty(slaves)){
			return;
		}
		//把key列表填加到不可变集合里
		for(Object o: slaves.keySet()){
			slaveList.add(String.valueOf(o));
		}
		slaveCount = slaveList.size();
		//不可变List
		slaveList = Collections.unmodifiableList(slaveList);
		
		slaves.put(RWContextHolder.MASTER_NAME, master);
		setTargetDataSources(slaves);
	}

	
	/**
	  * 随机路由：随机选择一个slave
	  * @return
	  */
	 public String randomSlaveKey(){
		 Random random = new Random();
		 return slaveList.get(random.nextInt(slaveList.size()));
	 }
	 
	 /**
	  * 轮询路由
	  * @return
	  */
	public String pollingSlaveKey() {
		// 得到的下标为：0、1、2、3……
		int index = counter.incrementAndGet() % slaveCount;
		if (counter.get() > 9999) { // 以免超出Integer范围
			counter.set(-1); // 还原
		}
		return slaveList.get(index);
	 }
	

}
