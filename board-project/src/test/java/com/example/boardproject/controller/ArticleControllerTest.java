package com.example.boardproject.controller;

import com.example.boardproject.config.SecurityConfig;
import com.example.boardproject.domain.type.SearchType;
import com.example.boardproject.dto.ArticleWithCommentsDto;
import com.example.boardproject.dto.UserAccountDto;
import com.example.boardproject.service.ArticleService;
import com.example.boardproject.service.PaginationService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("View 컨트롤러 - 게시글")
@Import(SecurityConfig.class)
@WebMvcTest(ArticleController.class)//입력한 컨트롤러만 읽어 테스트할 수 있다
class ArticleControllerTest {

  private final MockMvc mvc;
  @MockBean private ArticleService articleService;
  @MockBean private PaginationService paginationService;
  public ArticleControllerTest(@Autowired MockMvc mvc) {
    this.mvc = mvc;
  }//테스트패키지 안에서는 AutoWired 생략이 불가능하다

  //    @Disabled("구현 중")
  @DisplayName("[view] [GET] 게시글 리스트 (게시판) 페이지 - 정상호출")
  @Test
  public void givenNothing_whenRequestingArticlesView_thenReturnsArticlesView() throws Exception {
    //Given
    given(articleService.searchArticles(eq(null),eq(null),any(Pageable.class))).willReturn(Page.empty());
    given(paginationService.getPaginationBarNumbers(anyInt(),anyInt())).willReturn(List.of(0,1,2,3,4));
    // When & Then
    mvc.perform(get("/articles"))
            .andExpect(status().isOk())//200ok가 되었는가
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))//contentType이 올바른가 contentTypeCompatibleWith는 동일 계열이 맞으면 통과한다
            .andExpect(view().name("articles/index"))//실제 배치될 뷰의 이름 테스트(해당 이름의 뷰 존재 여부 파악)
            .andExpect(model().attributeExists("articles"))
            .andExpect(model().attributeExists("paginationBarNumbers"))
            .andExpect(model().attributeExists("searchTypes"));
    then(articleService).should().searchArticles(eq(null),eq(null),any(Pageable.class));
    then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
  }

  @DisplayName("[view][GET] 게시글 리스트 (게시판) 페이지 - 검색어와 함께 호출")
  @Test
  public void givenSearchKeyword_whenSearchingArticlesView_thenReturnsArticlesView() throws Exception {
    // Given
    SearchType searchType = SearchType.TITLE;
    String searchValue = "title";
    given(articleService.searchArticles(eq(searchType), eq(searchValue), any(Pageable.class))).willReturn(Page.empty());
    given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(0, 1, 2, 3, 4));

    // When & Then
    mvc.perform(
                    get("/articles")
                            .queryParam("searchType", searchType.name())
                            .queryParam("searchValue", searchValue)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(view().name("articles/index"))
            .andExpect(model().attributeExists("articles"))
            .andExpect(model().attributeExists("searchTypes"));
    then(articleService).should().searchArticles(eq(searchType), eq(searchValue), any(Pageable.class));
    then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
  }

  @DisplayName("[view] [GET] 게시글 리스트 (게시판) 페이지 - 페이징, 정렬 기능")
  @Test
  void givenPagingAndSortingParams_whenSearchingArticlesPage_thenReturnsArticlesPage() throws Exception{
    String sortName = "title";
    String direction = "desc";
    int pageNumber = 0;
    int pageSize= 5;
    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc(sortName)));
    List<Integer> barNumbers = List.of(1,2,3,4,5);
    given(articleService.searchArticles(null,null,pageable)).willReturn(Page.empty());
    given(paginationService.getPaginationBarNumbers(pageable.getPageNumber(),
            Page.empty().getTotalPages())).willReturn(barNumbers);
    mvc.perform(
            get("/articles")
                    .queryParam("page",String.valueOf(pageNumber))
                    .queryParam("size",String.valueOf(pageSize))
                    .queryParam("sort",sortName + "," + direction)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(view().name("articles/index"))
            .andExpect(model().attributeExists("articles"))
            .andExpect(model().attribute("paginationBarNumbers",barNumbers));
    then(articleService).should().searchArticles(null,null, pageable);
    then(paginationService).should().getPaginationBarNumbers(pageable.getPageNumber(),
            Page.empty().getTotalPages());

  }

  //@Disabled("구현 중")
  @DisplayName("[view] [GET] 게시글 상세 페이지 - 정상호출")
  @Test
  public void givenNothing_whenRequestingArticleView_thenReturnsArticleView() throws Exception {
    //Given
    Long articleId = 1L;
    given(articleService.getArticle(articleId)).willReturn(createArticleWithCommentsDto());
    // When & Then
    mvc.perform(get("/articles/"+articleId))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(view().name("articles/detail"))
            .andExpect(model().attributeExists("article"))
            .andExpect(model().attributeExists("articleComments"));
    then(articleService).should().getArticle(articleId);
  }

  @Disabled("구현 중")
  @DisplayName("[view] [GET] 게시글 검색 전용 페이지 - 정상호출")
  @Test
  public void givenNothing_whenRequestingArticleSearchView_thenReturnsArticleSearchView() throws Exception {
    //Given

    // When & Then
    mvc.perform(get("/articles/search"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(view().name("articles/search"));
  }


  @DisplayName("[view] [GET] 게시글 해시태그 검색 페이지 - 정상호출")
  @Test
  public void givenNothing_whenRequestingArticleHashtagSearchView_thenReturnsArticleHashtagSearchView() throws Exception {
    //Given
    List<String> hashtags = List.of("#java,#spring","#boot");
    given(articleService.searchArticlesViaHashtag(eq(null),any(Pageable.class))).willReturn(Page.empty());
    given(paginationService.getPaginationBarNumbers(anyInt(),
            anyInt())).willReturn(List.of(1,2,3,4,5));
    given(articleService.getHashtags()).willReturn(hashtags);
    // When & Then
    mvc.perform(get("/articles/search-hashtag"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(view().name("articles/search-hashtag"))
            .andExpect(model().attribute("articles",Page.empty()))
            .andExpect(model().attribute("hashtags",hashtags))
            .andExpect(model().attributeExists("paginationBarNumbers"))
            .andExpect(model().attribute("searchType",SearchType.HASHTAG));

    then(articleService).should().searchArticlesViaHashtag(eq(null),any(Pageable.class));
    then(articleService).should().getHashtags();
    then(paginationService).should().getPaginationBarNumbers(anyInt(),anyInt());
  }

  @DisplayName("[view] [GET] 게시글 해시태그 검색 페이지 - 정상호출, 해시태그 입력")
  @Test
  public void givenHashtag_whenRequestingArticleHashtagSearchView_thenReturnsArticleHashtagSearchView() throws Exception {
    //Given
    String hashtag = "#java";
    List<String> hashtags = List.of("#java,#spring","#boot");
    given(articleService.searchArticlesViaHashtag(eq(hashtag),any(Pageable.class))).willReturn(Page.empty());
    given(paginationService.getPaginationBarNumbers(anyInt(),
            anyInt())).willReturn(List.of(1,2,3,4,5));
    given(articleService.getHashtags()).willReturn(hashtags);
    // When & Then
    mvc.perform(
            get("/articles/search-hashtag")
                    .queryParam("searchValue",hashtag))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(view().name("articles/search-hashtag"))
            .andExpect(model().attribute("articles",Page.empty()))
            .andExpect(model().attributeExists("hashtags"))
            .andExpect(model().attributeExists("paginationBarNumbers"))
            .andExpect(model().attribute("searchType",SearchType.HASHTAG));
    then(articleService).should().getHashtags();
    then(paginationService).should().getPaginationBarNumbers(anyInt(),anyInt());
    then(articleService).should().searchArticlesViaHashtag(eq(hashtag),any(Pageable.class));
  }

  private ArticleWithCommentsDto createArticleWithCommentsDto() {
    return ArticleWithCommentsDto.of(
            1L,
            createUserAccountDto(),
            Set.of(),
            "title",
            "content",
            "#java",
            LocalDateTime.now(),
            "uno",
            LocalDateTime.now(),
            "uno"
    );
  }

  private UserAccountDto createUserAccountDto() {
    return UserAccountDto.of(
            1L,
            "uno",
            "pw",
            "uno@mail.com",
            "Uno",
            "memo",
            LocalDateTime.now(),
            "uno",
            LocalDateTime.now(),
            "uno"
    );

  }

}