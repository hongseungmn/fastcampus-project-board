package com.example.boardproject.repository;

import com.example.boardproject.config.JpaConfig;
import com.example.boardproject.domain.Article;
import com.example.boardproject.domain.UserAccount;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("JPA 연결 테스트")
@Import(JpaConfig.class) //Import를 하지 않으면 Auditing이 동작을 하지 않는다
@DataJpaTest
class JpaRepositoryTest {

  private final ArticleRepository articleRepository;
  private final ArticleCommentRepository articleCommentRepository;

  private final UserAccountRepository userAccountRepository;

  public JpaRepositoryTest(
          @Autowired ArticleRepository articleRepository,
          @Autowired ArticleCommentRepository articleCommentRepository,
          @Autowired UserAccountRepository userAccountRepository
  ) {
    this.articleRepository = articleRepository;
    this.articleCommentRepository = articleCommentRepository;
    this.userAccountRepository = userAccountRepository;
  }

  @DisplayName("select 테스트")
  @Test
  void givenTestData_whenSelecting_thenWorksFine() {
    // Given

    // When
    List<Article> articles = articleRepository.findAll();


    // Then - 테스트 코드를 작성할 때 System.out.println()로 매번 출력해야하는 어려움을 줄이기 위해 assertThat()을 종종 사용
    assertThat(articles)
            .isNotNull()
            .hasSize(123);

  }

  @DisplayName("insert 테스트")
  @Test // Insert의 경우 기존의 레코드 개수와 Insert 한 후의 개수가 1 차이나는 것을 이용한다
  void givenTestData_whenInserting_thenWorksFine() {
    // Given
    long previousCount = articleRepository.count();//기존 Count
    UserAccount userAccount = userAccountRepository.save(UserAccount.of("Hong","pw",null,null,null));
    Article article = Article.of(userAccount,"new article","new content","#spring");

    // When
    articleRepository.save(article);


    // Then
    assertThat(articleRepository.count()).isEqualTo(previousCount + 1);
  }

  @DisplayName("update 테스트")
  @Test
  void givenTestData_whenUpdating_thenWorksFine() {
    // Given
    Article article = articleRepository.findById(1L).orElseThrow();//아무 엔티티를 가져온다 - id를 통해
    String updatedHashtag = "#springboot";
    article.setHashtag(updatedHashtag);//HashTag를 가공한다 - springboot로

    // When
    Article savedArticle = articleRepository.saveAndFlush(article);//이를 해줘야 DB에 반영해준다-당연히 이는 롤백되므로 실제로 반영되진 않는다

    // Then
    assertThat(savedArticle).hasFieldOrPropertyWithValue("hashtag" ,updatedHashtag);//update된 hashtag로 바뀌어 있는가
  }

  @DisplayName("delete 테스트")
  @Test
  void givenTestData_whenDeleting_thenWorksFine() {
    // Given
    Article article = articleRepository.findById(1L).orElseThrow();
    long previousArticleCount = articleRepository.count();
    long previousArticleCommentCount = articleCommentRepository.count();
    int deletedCommentsSize = article.getArticleComments().size();

    // When
    articleRepository.delete(article);

    // Then
    assertThat(articleRepository.count()).isEqualTo(previousArticleCount -1);
    assertThat(articleCommentRepository.count()).isEqualTo(previousArticleCommentCount - deletedCommentsSize);
  }
}
