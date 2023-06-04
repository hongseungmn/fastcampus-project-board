package com.example.boardproject.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

// Auditing 관련 속성을 따로 분리한 것이다
@Getter
@ToString
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class AuditingFields {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)

  //updatable = false 값의 업데이트는 불가하게 설정
  @CreatedDate
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt; // 생성일시

  @CreatedBy
  @Column(nullable = false, updatable = false,length = 100)
  private String createdBy; // 생성자

  @LastModifiedDate
  @Column(nullable = false)
  private LocalDateTime modifiedAt; // 수정일시

  @LastModifiedBy
  @Column(nullable = false, length = 100)
  private String modifiedBy; // 수정자
}
