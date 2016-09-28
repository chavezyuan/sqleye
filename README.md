# Sqleye Readme

##Introduction
Sqleye是基于Spring和Mybatis的客户端sql执行时间监控工具。

##Usage

###maven
    <dependency>
		<groupId>com.sqleye</groupId>
		<artifactId>sqleye</artifactId>
		<version>1.0.0-SNAPSHOT</version>
	</dependency>
###Simple Config
只需在spring入口的xml文件中引入

    <import resource="classpath:sqleye.xml"/>
时间监控就能运行了

默认监控开启，且`time threshold=10ms`
当`sql`执行时间未超过`time threshold`，打印`debug`日志

    DEBUG - [SQL Info][threshold=10][actual=9][SQL]
当`sql`执行时间超过`time threshold`，打印出`warn`日志

    WARN - [SQL Alert][threshold=10][actual=21][SQL]

###Custom Config     
1. 配置文件中可以配置两项内容

        sqleye.time.threshold=50 //修改默认time threshold
        sqleye.on=true  //开关监控，true开，false关
    
2. 指定mapper或者mapper特定方法的time threshold
    例如：

        @SelfEye(threthold = 200)
        public interface CityMapper {
        
            @SelfEye(threthold = 500)
            City selectByPrimaryKey(Integer id);
            
            List<City> selectAll();
        }
        
 `selectByPrimaryKey`的`time threshold=500`
 `selectAll`的 `time threshold=200`
 



