package com.okay.rwdb.enums;

/**
 * 操作主库还是操作从库
 * @author dengyulong
 *
 */
public enum RWType {
	/**
	 * 主库操作
	 */
	MASTER,
	
	/**
	 * 从库操作
	 */
	SLAVE;
}
