package com.wuui.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.util.Date;

/**
 * @author
 * @create 2022-05-08-22:30
 * @discribe 社区首页实体类
 */

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
/**
 * 使用的es版本是7.15.2, shards = 1, replicas = 1改到了setting中
 */
@Setting(shards = 6, replicas = 3)
@Document(indexName = "discusspost"/*, type = "_doc", shards = 1, replicas = 1*/)
public class DiscussPost {
    @TableId(type = IdType.AUTO)
    @Id
    private Integer id;

    @Field(type = FieldType.Integer)
    private int userId;

    //以最多分词的方式存储，以最聪明的分词方式查询
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String title;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String content;

    //0；普通帖；1：置顶帖
    @Field(type = FieldType.Integer)
    private int type;

    //0：正常；1：精华；2：拉黑
    @Field(type = FieldType.Integer)
    private int status;

    @Field(type = FieldType.Date)
    private Date createTime;

    @Field(type = FieldType.Integer)
    private int commentCount;

    @Field(type = FieldType.Double)
    private double score;

}
