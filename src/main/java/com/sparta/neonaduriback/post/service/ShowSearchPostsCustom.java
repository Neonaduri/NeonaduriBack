package com.sparta.neonaduriback.post.service;

import com.sparta.neonaduriback.post.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ShowSearchPostsCustom {

    //검색 querydsl
    Page<Post> keywordSearch(String keyword, Pageable pageable);

}
