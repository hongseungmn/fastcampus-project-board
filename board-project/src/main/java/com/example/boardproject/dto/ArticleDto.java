package com.example.boardproject.dto;

import com.example.boardproject.domain.Article;
import com.example.boardproject.domain.ArticleComment;

import java.time.LocalDateTime;




//모든 필드를 가지고 있고 나중에 응답을 할 때 무엇을 보내줄지 선택해서 다시 가공하게끔 만듬
public record ArticleDto(
        //엔티티로부터 받아올 정보들을 나열, 엔티티의 모든 정보를 들고 있는다
        Long id,
        UserAccountDto userAccountDto,
        String title,
        String content,
        String hashtag,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {

  /*
  가독성 및 의도 전달성: 팩토리 메서드는 이름을 통해 인스턴스가 어떤 목적으로 생성되는지 명확하게 전달할 수 있습니다. of와 같은 이름을 사용하면 객체 생성의 의도가 코드에서 명시적으로 드러나므로 가독성이 향상됩니다.

유연성: 생성자와는 달리 팩토리 메서드는 자신의 구현을 변경하지 않고도 객체 생성 방식을 조절할 수 있는 유연성을 제공합니다. 예를 들어, 나중에 생성자의 변경이 필요한 경우, 이 변경을 캡슐화한 팩토리 메서드를 수정하면 됩니다.

객체 재사용 및 캐싱: 팩토리 메서드를 사용하면 객체 생성 전에 어떤 작업을 수행하거나 이미 생성된 객체를 재사용할 수 있습니다. 이를 통해 성능을 최적화하거나 메모리를 절약할 수 있습니다.

하위 클래스 반환 가능: 생성자는 항상 해당 클래스의 인스턴스를 반환하지만, 팩토리 메서드는 하위 클래스의 인스턴스를 반환할 수 있습니다. 이것은 인터페이스 기반 프로그래밍과 다형성을 지원하며, 클라이언트 코드가 구체적인 클래스에 의존하지 않도록 도와줍니다.

생성자 오버로딩의 복잡성 감소: 여러 생성자가 존재할 때, 각각에 대한 오버로딩이 증가할 수 있습니다. 팩토리 메서드를 사용하면 특정한 목적에 맞는 이름을 부여할 수 있어 오버로딩의 복잡성을 줄일 수 있습니다.
   */
  /**
   * of 메서드는 주어진 매개변수를 사용하여 ArticleDto 클래스의 인스턴스를 생성하는 정적 팩토리 메서드입니다. 이러한 메서드는 클래스의 객체 생성 세부 정보를 캡슐화하고 객체를 편리하고 가독성 있게 생성하는 데 사용됩니다.
   * @param id
   * @param userAccountDto
   * @param title
   * @param content
   * @param hashtag
   * @param createdAt
   * @param createdBy
   * @param modifiedAt
   * @param modifiedBy
   * @return
   */
  public static ArticleDto of(Long id, UserAccountDto userAccountDto, String title, String content, String hashtag, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
    return new ArticleDto(id, userAccountDto, title,content, hashtag,createdAt, createdBy, modifiedAt, modifiedBy);
  }

  //아래와 같이 할 경우 아티클은 DTO의 존재를 몰라도 된다
  /*
   * osiv 개념
   * Open Session In View
   * 현재 프로젝트는 리포지토리를 서비스가 접근해서 도메인 코드를 가지고 오면 dto로 바꿔 컨트롤러에 넘겨주고 컨트롤러가 그걸 서빙해 웹 서비스를 완
   * 트랜젝션은 범위는 Servie와 Repository에서 수행햐애 한다
   * 그런데 Open session in view는 트랜잭션을 넘어간 레이어에서도 여전히 하이버네트 세션이 살아 있다. 본래 영속성 컨텍스트 생존 범위는 Service와 Repository 레이어 내에 존재해야한다.
   * 하지만 이는 대용량 트래픽 처리 이슈 등으로 찬반양론이 많다
   * 따라서 이는 판단에 따라 사용할지 말지 정해야 한다
   *
   * 엔티티와 DTO의 완전한 분리로 이를 끄면 서비스가 나간 뒤에는 트랜젝션이 종료하고 레이지로딩을 하거나 다른 이슈가 있어도 아무 문제가 없게끔 한다
   */
  /**
   * 엔티티를 입력하면 DTO로 변환해주는 함수 like Mapper
   * @param entity
   * @return
   */
  public static ArticleDto from(Article entity) {
    return new ArticleDto(
            entity.getId(),
            UserAccountDto.from(entity.getUserAccount()),
            entity.getTitle(),
            entity.getContent(),
            entity.getHashtag(),
            entity.getCreatedAt(),
            entity.getCreatedBy(),
            entity.getModifiedAt(),
            entity.getModifiedBy()
    );
  }

  /**
   * 이는 위 함수의 반대의 기능을 하는 함수
   * @return
   */
  public Article toEntity() {
    return Article.of(
            userAccountDto.toEntity(),
            title,
            content,
            hashtag
    );
  }
}