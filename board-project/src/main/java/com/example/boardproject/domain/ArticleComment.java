package com.example.boardproject.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@ToString(callSuper = true)
@Table(indexes = {

        @Index(columnList = "content"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@Entity
public class ArticleComment extends AuditingFields{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

//ManyToOne 은 N : 1 관계라고 볼 수 있다. 즉 댓글은 게시글을 바라본다면 ManyToOne이라는 관계형성(게시글 하나에 댓글 여러개)
  @Setter @ManyToOne(optional = false) private Article article; // 게시글 (ID)

  @Setter @ManyToOne(optional = false) private UserAccount userAccount;
  @Setter @Column(nullable = false, length= 500) private String content; // 본문


  protected ArticleComment() {}

  private ArticleComment(Article article, UserAccount userAccount, String content) {
    this.userAccount = userAccount;
    this.article = article;
    this.content = content;
  }

  public static ArticleComment of(Article article, UserAccount userAccount , String content) {
    return new ArticleComment(article, userAccount, content);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ArticleComment that)) return false;
    return id != null && id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
