package com.nbsaw.miaohu.repository;

import com.nbsaw.miaohu.entity.ArticleVoteEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface ArticleVoteRepository extends CrudRepository<ArticleVoteEntity,Long> {
    @Query("select count(v) > 0 from ArticleVoteEntity v where v.articleId = :articleId and v.uid = :uid")
    boolean isVoted(@Param("articleId") Long articleId,@Param("uid") String uid);

    @Transactional
    Integer deleteByArticleIdAndUid(Long articleId,String uid);
}
