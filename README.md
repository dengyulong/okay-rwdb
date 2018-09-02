# okay-rwdb
基于Spring AOP和Mybatis实现的数据库读写分离
多个从库支持随机路由和轮询路由

使用说明：
1、spring配置文件参考
	<bean id="masterDataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource"
		destroy-method="close">
		<property name="driverClass" value="${jdbc.driver}" />
		<property name="jdbcUrl" value="${jdbc.url}" />
		<property name="user" value="${jdbc.username}" />
		<property name="password" value="${jdbc.password}" />
		<property name="initialPoolSize" value="${pool.initialPoolSize}" />
		<property name="minPoolSize" value="${pool.minPoolSize}" />
		<property name="maxPoolSize" value="${pool.maxPoolSize}" />
		<property name="maxIdleTime" value="${pool.maxIdleTime}" />
		<property name="acquireIncrement" value="${pool.acquireIncrement}" />
		<property name="checkoutTimeout" value="${pool.checkoutTimeout}" />
		<property name="maxIdleTimeExcessConnections" value="${pool.maxIdleTimeExcessConnections}" />
	</bean>	
	
	<bean id="slave01DataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource"
		destroy-method="close">
		<property name="driverClass" value="${jdbc.driver}" />
		<property name="jdbcUrl" value="jdbc:mysql://127.0.0.1:3306/club?useUnicode=true&amp;characterEncoding=UTF-8" />
		<property name="user" value="${jdbc.username}" />
		<property name="password" value="${jdbc.password}" />
		<property name="initialPoolSize" value="${pool.initialPoolSize}" />
		<property name="minPoolSize" value="${pool.minPoolSize}" />
		<property name="maxPoolSize" value="${pool.maxPoolSize}" />
		<property name="maxIdleTime" value="${pool.maxIdleTime}" />
		<property name="acquireIncrement" value="${pool.acquireIncrement}" />
		<property name="checkoutTimeout" value="${pool.checkoutTimeout}" />
		<property name="maxIdleTimeExcessConnections" value="${pool.maxIdleTimeExcessConnections}" />
	</bean>	
	
	
	<bean id="dataSource" class="com.mycompany.club.datasource.RWDataSource">
	   <!-- 配置主库 -->
	   <property name="master" ref="masterDataSource"/>
	   
	   <!-- 配置从库列表 -->
	   <property name="slaves">
	       <map key-type="java.lang.String">
	           <entry key="slave01DataSource" value-ref="slave01DataSource"/>
	       </map>
	   </property>
	</bean>
	


    <!-- MyBatis配置 -->
	<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
		<property name="dataSource" ref="dataSource" />
		<!-- 自动扫描entity目录, 省掉Configuration.xml里的手工配置 -->
		<property name="typeAliasesPackage" value="com.mycompany.club.entity" />
		<!-- 显式指定Mapper文件位置 -->
		<property name="mapperLocations" value="classpath:/mapper/*Mapper.xml"/>
		<property name="plugins">
		    <list>
		        <!-- 插件会按照配置的从下往上的顺序执行 -->	
		        <bean class="com.mycompany.club.interceptor.RWInterceptor"/>
		    </list>
		</property>
	</bean>
	 
	<bean id="transactionManager" class="com.mycompany.club.datasource.RWTransactionManager">
		<property name="dataSource" ref="dataSource" />
	</bean>

	<!-- 使用annotation定义事务 -->
	<tx:annotation-driven transaction-manager="transactionManager" proxy-target-class="true" />
	
	<!-- 定义aspectj -->
	<aop:aspectj-autoproxy proxy-target-class="true"/>
	
2、
默认情况下，查询会从从库查询，修改、删除、插入会走主库；
可以在service方法上增加注解	@RWSource(DBType.SLAVE)来指定主库或从库；
如果service方法上有事务注解@Transactional则会走主库
	
