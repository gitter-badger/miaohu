package com.nbsaw.miaohu.entity;

import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "answer_vote_map")
@Data
public class AnswerVoteMapEntity {
    @Id
    @GeneratedValue
    private Long id;

    private Long answerId;

    private Long questionId;

    private String uid;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date date = new Date();
}
