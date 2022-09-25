package com.example.springbook.web;

import com.example.springbook.config.auth.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = HelloController.class,
excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
})    // Web(Spring MVC)에 집중할 수 있는 어노테이션
class HelloControllerTest {

    @Autowired      // 스프링이 관리하는 빈을 주입 받는다.
    private MockMvc mvc;    // 웹 API를 테스트할 떄 사용한다. 스프링 MVC 테스트의 시작점. 이 클래스를 통해 API 테스트 가능.

    @Test
    @WithMockUser(roles = "USER")
    public void hello_return() throws Exception {
        String hello = "hello";

        mvc.perform(get("/hello"))  // 주소 요청
                .andExpect(status().isOk())     // HTTP 상태코드 검증
                .andExpect(content().string(hello));    // 본문의 내용 검증

    }

    @Test
    @WithMockUser(roles = "USER")
    public void helloDto_return() throws Exception {
        String name = "hello";
        int amount = 1000;

        mvc.perform(get("/hello/dto")
                        .param("name", name)    // API 테스트할 때 사용될 요청 파라미터(값은 String만 허용)
                        .param("amount", String.valueOf(amount)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(name)))     //JSON 응답값을 필드별로 검증할 수 있는 메서드
                .andExpect(jsonPath("$.amount", is(amount)));
    }
}