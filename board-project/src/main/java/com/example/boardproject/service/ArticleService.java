package com.example.boardproject.service;

import com.example.boardproject.domain.Article;
import com.example.boardproject.domain.type.SearchType;
import com.example.boardproject.dto.ArticleDto;
import com.example.boardproject.dto.ArticleWithCommentsDto;
import com.example.boardproject.repository.ArticleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ArticleService {
  //DTO와 도메인 존재를 모두 알고 있는 것은 서비스 레이어가 된다

  private final ArticleRepository articleRepository;

  @Transactional(readOnly = true)
  public Page<ArticleDto> searchArticles(SearchType searchType, String searchKeyword, Pageable pageable) {
    //serach - keyword가 없을 때
    if(searchKeyword == null || searchKeyword.isBlank()) {
      return articleRepository.findAll(pageable).map(ArticleDto::from);
    }
    //search - keyword가 있을 경우 (switch의 새로운 타입)
    return switch (searchType) {
      case TITLE -> articleRepository.findByTitleContaining(searchKeyword, pageable).map(ArticleDto::from);
      case CONTENT -> articleRepository.findByContentContaining(searchKeyword, pageable).map(ArticleDto::from);
      case ID -> articleRepository.findByUserAccount_UserIdContaining(searchKeyword, pageable).map(ArticleDto::from);
      case NICKNAME -> articleRepository.findByUserAccount_NicknameContaining(searchKeyword, pageable).map(ArticleDto::from);
      case HASHTAG -> articleRepository.findByHashtag("#" + searchKeyword, pageable).map(ArticleDto::from);//#을 자동으로 넣어주는 코드
    };
  }

  @Transactional(readOnly = true)
  public ArticleWithCommentsDto getArticle(Long articleId) {
    return articleRepository.findById(articleId)
            .map(ArticleWithCommentsDto::from)
            .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다 - articleId: " + articleId));
  }

  public void saveArticle(ArticleDto dto) {
    articleRepository.save(dto.toEntity());
  }

  public void updateArticle(ArticleDto dto) {
    try {
      Article article = articleRepository.getReferenceById(dto.id()); //findById는 select 쿼리를 날리는데 이미 있다고 가정을 하므로 select 쿼리를 날리지 않게 하기 위해 getReferenceById를 사용
      if (dto.title() != null) { article.setTitle(dto.title()); }//java record 에서는 getter setter를 만들어주고 이는 get, set이 없고 바로 필드명으로 사용한다
      if (dto.content() != null) { article.setContent(dto.content()); }
      article.setHashtag(dto.hashtag()); //현재 이 클래스 안은 class level transactional에 의해서 이 메소드 단위로 트랜지션이 묶여 있다. 따라서 트랜지션이 끝ㄴ날 때 영속성 컨텍스트는 article이 변한 것을 감지해 낸다. 따라서 save를 명시할 필요가 없다
    } catch (EntityNotFoundException e) { // 수정글이 없을 때 에러를 알기 위함
      log.warn("게시글 업데이트 실패. 게시글을 찾을 수 없습니다. - dto: {}", dto);
    }
  }

  public void deleteArticle(long articleId) {
    articleRepository.deleteById(articleId);
  }

  public long getArticleCount() {
    return articleRepository.count();
  }

  @Transactional(readOnly = true)
  public Page<ArticleDto> searchArticlesViaHashtag(String hashtag, Pageable pageable) {
    if(hashtag == null || hashtag.isBlank()) {
      return Page.empty(pageable);
    }
    return articleRepository.findByHashtag(hashtag,pageable).map(ArticleDto::from);
  }

  public List<String> getHashtags() {
    return articleRepository.findAllDistinctHashtags();
  }
}
