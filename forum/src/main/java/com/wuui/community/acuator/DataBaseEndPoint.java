package com.wuui.community.acuator;

import com.wuui.community.util.CommunityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @author Dee
 * @create 2022-06-05-15:58
 * @describe 自定义endpoint
 */
@Component
@Endpoint(id = "database")
@Slf4j
public class DataBaseEndPoint {

    @Autowired
    DataSource dataSource;

    //只允许读请求
    @ReadOperation
    public String isConnected(){
        try(
                Connection connection = dataSource.getConnection();
                ) {
            return CommunityUtil.getJSONString(200, "数据库连接成功！连接池为：" + dataSource.getClass());
        } catch (Exception e){
            log.error("数据库连接失败", e.getMessage());
            return CommunityUtil.getJSONString(404, "数据库连接失败！");
        }
    }

}
