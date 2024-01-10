package com.example.boardproject.dto.response;

import com.example.boardproject.dto.ArticleCommentDto;

import java.io.Serializable;
import java.time.LocalDateTime;

public record ArticleCommentResponse (
        //필요 부분만 가지고 있다
        //해당
        Long id,
        String content,
        LocalDateTime createdAt,
        String email,
        String nickname
) implements Serializable {
  public static ArticleCommentResponse of(Long id, String content, LocalDateTime createdAt, String email, String nickname) {
    return new ArticleCommentResponse(id, content, createdAt, email, nickname);
  }

  public static ArticleCommentResponse from(ArticleCommentDto dto) {
    //닉네임은 따로 분러온다.
    //그 후 닉네임이 비어 있을 경우 유저 아이디를 불러와 유저 아이디나 닉네임으로, 다만 우선순위는 닉네임으로 한다
    String nickname = dto.userAccountDto().nickname();
    if(nickname == null || nickname.isBlank()) {
      nickname = dto.userAccountDto().userId();
    }

    return new ArticleCommentResponse(
            dto.id(),
            dto.content(),
            dto.createdAt(),
            dto.userAccountDto().email(),
            nickname
    );
  }
}
