package com.okay.rwdb.datasource;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.okay.rwdb.RWContextHolder;
import com.okay.rwdb.annotions.RWSource;
import com.okay.rwdb.enums.RWType;

@Aspect
@Component
public class RWAspect {
	
	private static final Logger LOG = LoggerFactory.getLogger(RWAspect.class);
	
	@Before("@annotation(RWSource)")
	public void beforeSwitchDS(JoinPoint point) {
		//获得当前访问的class
		Class<?> className = point.getTarget().getClass();
		//获得访问的方法名
		String methodName = point.getSignature().getName();
		//得到方法的参数的类型
		Class[] argClass = ((MethodSignature) point.getSignature()).getParameterTypes();
		
		//String dataSource = RWContextHolder.MASTER_NAME;
		RWContextHolder.markManual();
		
		try {
			//得到访问的方法对象
			Method method = className.getMethod(methodName, argClass);
			// 判断是否存在@RWSource注解
			if(method.isAnnotationPresent(RWSource.class)){
				RWSource annotation = method.getAnnotation(RWSource.class);
				RWType dbType = annotation.value();
				if(dbType == RWType.SLAVE){
					//dataSource = DBRoutingUtil.getRandomSlave(DynamicDataSource.getSlaveList());
					RWContextHolder.markSlave();
					return;
				}
			}
			
		} catch (Exception e) {
			LOG.error("routing datasource exception, " + methodName, e);
		}
		
		 // 切换数据源
		//RWContextHolder.setDB(dataSource);
		RWContextHolder.markMaster();
		
	}
	
	 @AfterReturning("@annotation(RWSource)")
	 public void afterSwitchDS(JoinPoint point){
		 RWContextHolder.clear();
	 }
	 

}
