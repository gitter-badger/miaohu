package com.nbsaw.miaohu.vo;

import lombok.Data;
import org.hibernate.annotations.NamedNativeQuery;

import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.SqlResultSetMapping;
import java.util.Date;

@Data
//@SqlResultSetMapping(
//        name="resultVo",
//        classes={
//                @ConstructorResult(
//                        targetClass=QAResultVo.class,
//                        columns={
//                                @ColumnResult(name="id"),
//                                @ColumnResult(name="title"),
//                                @ColumnResult(name = "content"),
//                                @ColumnResult(name = "uid"),
//                                @ColumnResult(name = "date")
//                        }
//                )
//        }
//)
//
//@NamedNativeQuery(name="TagMapRepository.selectById", query="select r.id,r.title,r.content,r.uid,r.date as QAResultVo from (select id,title,content,uid,date from question union all select id,title,content,uid,date from article) r order by date DESC ", resultSetMapping="resultVo")
public class QAResultVo {
    private Long id;

    private String title;

    private String content;

    private String uid;

    private Date date;

    public QAResultVo(Long id,String title,String content,String uid,Date date){
        this.id = id;
        this.title = title;
        this.content = content;
        this.uid = uid;
        this.date = date;
    }
}
