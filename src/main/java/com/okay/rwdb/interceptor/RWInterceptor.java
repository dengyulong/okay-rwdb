package com.okay.rwdb.interceptor;
import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.okay.rwdb.RWContextHolder;


/**
 * 读写分离：查询走从库，新增、删除、修改走主库
 * @author dengyulong
 */
@Intercepts({
		@Signature(type = Executor.class, method = "update", args = {
				MappedStatement.class, Object.class }),
		@Signature(type = Executor.class, method = "query", args = {
				MappedStatement.class, Object.class, RowBounds.class,
				ResultHandler.class }) })
public class RWInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(RWInterceptor.class);

    @SuppressWarnings("unused")
    private Properties properties;
    

    @SuppressWarnings("unchecked")
    public Object intercept(Invocation invocation) throws Throwable {
    	//已通过注解RWSource或Transactional设置
    	if(RWContextHolder.isManual()){
    		return invocation.proceed();
    	}
    	
    	MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
    	//查询操作走从库
    	if(mappedStatement.getSqlCommandType() == SqlCommandType.SELECT){
    		RWContextHolder.markSlave();
    	}else{
    		RWContextHolder.markMaster();
    	}
        return invocation.proceed(); //继续执行下一个拦截器,如果没有则返回执行结果
    }

    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    public void setProperties(Properties properties0) {
        this.properties = properties0;
    }
    
}