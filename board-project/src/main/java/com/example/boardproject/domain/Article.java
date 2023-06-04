package com.example.boardproject.domain;

import jakarta.persistence.*;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter//모든 게터 설정
@ToString(callSuper = true)
@Table(indexes = {//@Index 본문을 제외한 것들에 인덱싱을 생성한 것 Index란 추가적인 작업들을 통해서 테이블에서 데이터의 조회 속도를 향상시켜줄 수 있는 자료구조
        @Index(columnList = "title"),
        @Index(columnList = "hashtag"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@Entity//위에 것들이 Entity 이라고 명시
//Auditing이라는 것은 Spring Data JPA에서 시간에 대해서 자동으로 값을 넣어주는 기능입니다
//hibernate를 사용하면 자동으로 테이블을 생성해 주는 것이다
//AuditingFields는 상속을 통해 추가할 수도 있다 또는 Article 클래스 안에 생성할 수도 있다
public class Article extends AuditingFields {

  @Id//기본키 설정
  @GeneratedValue(strategy= GenerationType.IDENTITY)//자동으로 오토인크리먼트를 걸어줌
  private Long id;

  //Setter를 모두 걸지 않은 이유는 id, 생성일시 등은 setter를 만들지 않아 접근 자체를 불허하게 하기 위함
  //Column 어노테이션을 통해 NOT NULL, 등을 넣어줄 수 있다
  @Setter @ManyToOne(optional = false) private UserAccount userAccount;
  //id는 접근하게 하고 싶지 않아 클래스 전체에 setter를 걸지 않는다.
  @Setter @Column(nullable = false) private String title; // 제목
  @Setter @Column(nullable = false, length = 10000) private String content; // 본문

  @Setter private String hashtag; // 해시태그

  //관계를 맺은 두 엔티티가 서로 toString을 호출하면서 무한 반복
  @ToString.Exclude//연결을 끊는 것. ArticleComment-> Article을 바라보는 것은 자연스어우나 반대는 어울리지 않는 것을 참고
  @OrderBy("createdAt DESC")
  @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)//새로운 테이블 생성을 하지 않기 위해 mappedBy 사용 -> 양방향 바인딩
  private final Set<ArticleComment> articleComments = new LinkedHashSet<>();


  protected Article() {}

  //메타데이터를 제외하고 실제 사용할 것들만 오픈해 생성자 생성
  //new를 사용하지 않고 팩토리를 사용하기 위해 private로 설정
  private Article(UserAccount userAccount,String title, String content, String hashtag) {
    this.userAccount = userAccount;
    this.title = title;
    this.content = content;
    this.hashtag = hashtag;
  }

  //의도를 전달
  public static Article of(UserAccount userAccount,String title, String content, String hashtag) {
    return new Article(userAccount,title, content, hashtag);
  }

  //리스트등 컬렉션에 위 Article을 넣었을 때 값의 동등성을 파악하기 위해 아래과 같은 equals를 오버라이딩했다
  //엔티티에 들어있는 두 객체의 동등성을 모든 컬럼을 비교하지 않고 유니크한 아이디만 비교해 검색의 부담을 줄인다
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Article article)) return false;
    return id != null && id.equals(article.id); // && id != null 을 추가하면 아이디가 null인경우(데이터를 데이터베이스에 연결 안함->id null)를 동등성검사를 탈락하게 한다
  }
  //추가 : 아직 아이디가 영속화 되지 않았다면(insert 전)모든 값이 같더라도 동등성 검사에서 다른 것으로 판단한다

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
