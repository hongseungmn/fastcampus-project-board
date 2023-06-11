package com.example.boardproject.repository;

import com.example.boardproject.domain.Article;
import com.example.boardproject.domain.QArticle;
import com.example.boardproject.repository.querydsl.ArticleRepositoryCustom;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ArticleRepository extends
        JpaRepository<Article, Long>,
        ArticleRepositoryCustom,
        QuerydslPredicateExecutor<Article>, // Article에 들어가 있는 모든 검색 기능을 추가해준다  단, 정확한 검색만 가능하다. 또한 이는 자동으로 Q클래스를 생성해준다
        QuerydslBinderCustomizer<QArticle> {//QuerydslBinderCustomizer는 Q 클래스가 들어가야 한다

    Page<Article> findByTitleContaining(String title, Pageable pageable);
    Page<Article> findByContentContaining(String content, Pageable pageable);
    Page<Article> findByUserAccount_UserIdContaining(String userId, Pageable pageable);
    Page<Article> findByUserAccount_NicknameContaining(String nickname, Pageable pageable);
    Page<Article> findByHashtag(String hashtag, Pageable pageable);


    @Override
    default void customize(QuerydslBindings bindings, QArticle root) {//인터페이스이지만 default 메소드를 통해 호환성을 키울 수 있다
        bindings.excludeUnlistedProperties(true);
        bindings.including(root.title, root.content, root.hashtag, root.createdAt, root.createdBy);//원하는 필드를 추가
        // bindings.bind(root.title).first(StringExpression::likeIgnoreCase); // like '${v}'
        bindings.bind(root.title).first(StringExpression::containsIgnoreCase); // like '%${v}%' - 부분검색에선 이와같은 형태를 사용하므로
        bindings.bind(root.content).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.hashtag).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.createdAt).first(DateTimeExpression::eq);//문자열이 아닌 Date 타입이므로 DateTimeExpression을 사용
        bindings.bind(root.createdBy).first(StringExpression::containsIgnoreCase);
    }
}