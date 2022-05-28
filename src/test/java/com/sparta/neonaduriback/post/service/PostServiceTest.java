package com.sparta.neonaduriback.post.service;

import com.sparta.neonaduriback.post.model.Post;
import com.sparta.neonaduriback.post.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//@SpringBootTest
//class PostServiceTest {
//
//    private final PostRepository postRepository;
//
//    @Autowired
//    public PostServiceTest(PostRepository postRepository){
//        this.postRepository=postRepository;
//    }
//
//    @Test
//    void showTheme(){
//        String theme="맛집";
//        List<Post> postList=postRepository.findAllByThemeOrderByLikeCntDesc(theme);
//
//    }
//
//}