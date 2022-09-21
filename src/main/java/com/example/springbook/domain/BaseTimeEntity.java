package com.example.springbook.domain;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass   //JPA Entity 클래스들이 BaseTimeEntity를 상속할 경우 필드들도 컬럼으로 인식
@EntityListeners(AuditingEntityListener.class)  // Auditing 기능을 포함
public class BaseTimeEntity {

    @CreatedDate    //생성시간 자동 저장
    private LocalDateTime createDate;

    @LastModifiedDate   //수정시간 자동저장
    private LocalDateTime modifiedDate;
}
