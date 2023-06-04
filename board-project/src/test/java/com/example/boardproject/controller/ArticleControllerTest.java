package com.example.boardproject.controller;

import com.example.boardproject.config.SecurityConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("View 컨트롤러 - 게시글")
@Import(SecurityConfig.class)
@WebMvcTest(ArticleController.class)//입력한 컨트롤러만 읽어 테스트할 수 있다
class ArticleControllerTest {

  private final MockMvc mvc;

  public ArticleControllerTest(@Autowired MockMvc mvc) {
    this.mvc = mvc;
  }//테스트패키지 안에서는 AutoWired 생략이 불가능하다

  //    @Disabled("구현 중")
  @DisplayName("[view] [GET] 게시글 리스트 (게시판) 페이지 - 정상호출")
  @Test
  public void givenNothing_whenRequestingArticlesView_thenReturnsArticlesView() throws Exception {
    //Given

    // When & Then
    mvc.perform(get("/articles"))
            .andExpect(status().isOk())//200ok가 되었는가
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))//contentType이 올바른가 contentTypeCompatibleWith는 동일 계열이 맞으면 통과한다
            .andExpect(view().name("articles/index"))//실제 배치될 뷰의 이름 테스트(해당 이름의 뷰 존재 여부 파악)
            .andExpect(model().attributeExists("articles"));
  }

  @Disabled("구현 중")
  @DisplayName("[view] [GET] 게시글 상세 페이지 - 정상호출")
  @Test
  public void givenNothing_whenRequestingArticleView_thenReturnsArticleView() throws Exception {
    //Given

    // When & Then
    mvc.perform(get("/articles/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(view().name("articles/detail"))
            .andExpect(model().attributeExists("article"))
            .andExpect(model().attributeExists("articleComments"));
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

  @Disabled("구현 중")
  @DisplayName("[view] [GET] 게시글 해시태그 페이지 - 정상호출")
  @Test
  public void givenNothing_whenRequestingArticleHashtagSearchView_thenReturnsArticleHashtagSearchView() throws Exception {
    //Given

    // When & Then
    mvc.perform(get("/articles/search-hashtag"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(view().name("articles/search-hashtag"));
  }

}