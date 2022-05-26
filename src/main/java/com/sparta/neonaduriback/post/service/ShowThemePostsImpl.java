//package com.sparta.neonaduriback.post.service;
//
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import com.sparta.neonaduriback.post.model.Post;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//
//import java.util.List;
//
//import static com.sparta.neonaduriback.post.model.QPost.post;
//
//@RequiredArgsConstructor
//public class ShowThemePostsImpl implements ShowThemePostsCustom{
//
//    private final JPAQueryFactory queryFactory;
//
//    @Override
//    public Page<Post> findAllByThemeAndIspublicTrue(String theme, Pageable pageable) {
//        List<Post> results= queryFactory.
//                selectFrom(post).
//                where(post.theme.eq(theme) , post.ispublic.eq(true)).
//                offset(pageable.getOffset()).
//                limit(pageable.getPageSize()).
//                fetch();
//        return new PageImpl<>(results, pageable, results.size());
//    }
//}
