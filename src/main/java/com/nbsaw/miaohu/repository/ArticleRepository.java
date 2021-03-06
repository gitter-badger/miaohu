package com.nbsaw.miaohu.repository;

import com.nbsaw.miaohu.entity.ArticleEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface ArticleRepository extends PagingAndSortingRepository<ArticleEntity,Long> {

    /**
     * ---------------------------------------------------------------------------
     *
     *                                 查 找
     *
     * ---------------------------------------------------------------------------
     */

    // 根据uid和帖子id查找问题归属
    @Query("select count(a) > 0 from ArticleEntity a where a.id = :id and a.uid = :uid")
    boolean belong(@Param("id")Long id, @Param("uid")String uid);

    /**
     * ---------------------------------------------------------------------------
     *
     *                                 修 改
     *
     * ---------------------------------------------------------------------------
     */


    /**
     * ---------------------------------------------------------------------------
     *
     *                                 删 除
     *
     * ---------------------------------------------------------------------------
     */
}
