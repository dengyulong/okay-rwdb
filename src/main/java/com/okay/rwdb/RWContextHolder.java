package com.okay.rwdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.okay.rwdb.enums.RWType;

/**
 * 上下文 
 * @author dengyulong
 *
 */
public class RWContextHolder {

	private static final Logger LOG = LoggerFactory.getLogger(RWContextHolder.class);
			
    // 默认数据源为主库
	public static final String MASTER_NAME = "master";
	

	// 利用ThreadLocal保障线程安全
	private static final ThreadLocal<RWType> rwContextHolder = new ThreadLocal<RWType>();
	private static final ThreadLocal<Boolean> manualContextHolder = new ThreadLocal<Boolean>();

	/**
	 * 标记为主库
	 */
	public static void markMaster(){
		rwContextHolder.set(RWType.MASTER);
	}
	
	/**
	 * 标记为从库
	 */
	public static void markSlave(){
		rwContextHolder.set(RWType.SLAVE);
	}
	
    /**
     * 	标记为已手动设置，即通过注解RWSource或Transactional，后续阻止自动再设置
     */
	public static void markManual(){
		manualContextHolder.set(true);
	}
	
	/**
	 * 是否为主库
	 * @return
	 */
	public static boolean isMaster() {
		RWType dbType = rwContextHolder.get();
		return dbType == null ? true : dbType == RWType.MASTER;
	}
	
	/**
	 * 是否为从库 
	 * @return
	 */
	public static boolean isSlave(){
		RWType dbType = rwContextHolder.get();
		return dbType == null ? false : dbType == RWType.SLAVE;
	}
	
	/**
	 * 是否已手动设置
	 * @return
	 */
	public static boolean isManual(){
		Boolean manual = manualContextHolder.get();
		return manual == null?false:true;
	}
	
	
	
//	// 设置数据源名
//	public static void setDB(String dataSourceName) {
//		LOG.debug("切换到{}数据源", dataSourceName);
//		rwContextHolder.set(dataSourceName);
//	}
//
//	// 获取数据源名
//	public static String getDB() {
//		return rwContextHolder.get();
//	}

	// 已手动设置，即通过注解RWSource或Transactional
	public static void setManual(){
		manualContextHolder.set(true);
	}
	
	public static Boolean getManual(){
		Boolean flag = manualContextHolder.get();
		return flag == null?false:flag;
	}
	

	// 清除数据源名,用完清掉防止内存泄漏
	public static void clear() {
		rwContextHolder.remove();
		manualContextHolder.remove();
	}
	

}
