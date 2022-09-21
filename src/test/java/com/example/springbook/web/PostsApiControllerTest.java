package com.example.springbook.web;

import com.example.springbook.domain.posts.Posts;
import com.example.springbook.domain.posts.PostsRepository;
import com.example.springbook.web.dto.PostsSaveRequestDto;
import com.example.springbook.web.dto.PostsUpdateRequestDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // 호스트가 사용하지 않는 포트번호로 내장서버 랜덤 포트로 띄우기
class PostsApiControllerTest {

    @LocalServerPort    // 랜덤으로 띄워진 포트번호가 저장된다.
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;  //Http Request/Response가 이루어질 때 Http 헤더와 바디를 포함하는 클래스이다.

    @Autowired
    private PostsRepository postsRepository;

    @AfterEach
    public void tearDown() throws  Exception {
        postsRepository.deleteAll();
    }

    @Test
    public void Posts_등록된다() throws Exception {
        //given
        String title = "title";
        String content = "content";
        PostsSaveRequestDto requestDto = PostsSaveRequestDto.builder()
                .title(title)
                .content(content)
                .author("author")
                .build();

        String url = "http://localhost:" + port + "/api/v1/posts";

        //when
        // postForEntity(요청 url, 요청데이터, 반환 데이터 타입) => responseEntity에는 헤더정보와 Body의 내용이 담긴다.
        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(url, requestDto, Long.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        List<Posts> all = postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(title);
        assertThat(all.get(0).getContent()).isEqualTo(content);
    }

    @Test
    public void Posts_update() throws Exception{
        //given
        Posts savedPosts = postsRepository.save(Posts.builder()
                .title("title")
                .content("content")
                .author("author")
                .build());

        Long updateId = savedPosts.getId();
        String expectedTitle = "title2";
        String expectedContent = "content2";

        //파라미터 정보
        PostsUpdateRequestDto requestDto = PostsUpdateRequestDto.builder()
                .title(expectedTitle)
                .content(expectedContent)
                .build();

        //요청 url
        String url = "http://localhost:" + port + "/api/v1/posts/" + updateId;

        //requestBody로 만들기 위해 dto를 HttpEntity로 변환
        HttpEntity<PostsUpdateRequestDto> requestEntity = new HttpEntity<>(requestDto);

        //when
        //put 메서드를 요청 후 ResponseEntity로 반환받기 위해 restTemplate.exchange(url, Http메서드, 요청 데이터, 반환타입) 사용
        ResponseEntity<Long> responseEntity = restTemplate
                .exchange(url, HttpMethod.PUT, requestEntity, Long.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        List<Posts> all = postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(expectedTitle);
        assertThat(all.get(0).getContent()).isEqualTo(expectedContent);
    }

}