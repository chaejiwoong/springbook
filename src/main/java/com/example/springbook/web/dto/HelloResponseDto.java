package com.example.springbook.web.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor    // final이 붙은 필드가 포함된 생성자를 생성해준다.
public class HelloResponseDto {

    private final String name;
    private final int amount;


}
