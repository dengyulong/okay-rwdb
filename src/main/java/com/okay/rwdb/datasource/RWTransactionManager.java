package com.okay.rwdb.datasource;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;

import com.okay.rwdb.RWContextHolder;

public class RWTransactionManager extends DataSourceTransactionManager {

	private static final long serialVersionUID = 7249759565030317450L;

	/**
	 * 如果service加了@Transactional，则强制走主库
	 */
	@Override
	protected void doBegin(Object transaction, TransactionDefinition definition) {
		RWContextHolder.markMaster();
		super.doBegin(transaction, definition);
	}
	
	@Override
	protected void doCleanupAfterCompletion(Object transaction) {
		RWContextHolder.clear();
		super.doCleanupAfterCompletion(transaction);
	}
	
}
