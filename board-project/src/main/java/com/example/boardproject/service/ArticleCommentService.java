package com.example.boardproject.service;

import com.example.boardproject.dto.ArticleCommentDto;
import com.example.boardproject.dto.ArticleDto;
import com.example.boardproject.repository.ArticleCommentRepository;
import com.example.boardproject.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class ArticleCommentService {

  private final ArticleCommentRepository articleCommentRepository;
  private final ArticleRepository articleRepository;

  @Transactional(readOnly = true)
  public List<ArticleDto> searchArticleComments(Long articleId) {
    return List.of();
  }


  public void saveArticleComment(ArticleCommentDto dto) {
  }

  public void updateArticleComment(ArticleCommentDto dto) {

  }

  public void deleteArticleComment(Long dto) {

  }
}
