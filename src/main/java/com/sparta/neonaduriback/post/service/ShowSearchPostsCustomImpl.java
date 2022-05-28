package com.sparta.neonaduriback.post.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.neonaduriback.post.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.sparta.neonaduriback.post.model.QPost.post;


@RequiredArgsConstructor
public class ShowSearchPostsCustomImpl implements ShowSearchPostsCustom {


    private final JPAQueryFactory queryFactory;


    @Override
    public Page<Post> keywordSearch(String keyword, Pageable pageable) {
//        QUser quser= new QUser("user");
//        QReview qReview = new QReview("review");
//        QLikes qLikes= new QLikes("likes");
//
//
//        List<PlanResponseDto> results= queryFactory.
//                select(Projections.fields(PlanResponseDto.class,
//                        post.postId,
//                        post.postImgUrl,
//                        post.postTitle,
//                        post.startDate,
//                        post.endDate,
//                        post.location,
//                        post.theme,
//                        post.islike,
//                        post.likeCnt, //찜한 여부 확인
//                        countReview(post.postId, user),
//                        post.user
//                        )).
//                where(post.theme.contains(keyword).
//                        or(post.postTitle.contains(keyword).
//                                or(post.theme.contains(keyword))).
//                        and(post.ispublic.eq(true)).
//                        and(post.days.size().gt(0))).
//                offset(pageable.getOffset()).
//                limit(pageable.getPageSize()).
//                fetch();
//        for(PlanResponseDto responseDto: results){
//            responseDto.get
//        }
        //-----------------------------------------------
        List<Post> results = queryFactory.selectFrom(
                post).
//                ExpressionUtils.as(
//                        JPAExpressions.selectFrom(post)
//                                .where(post.ispublic.eq(true)),"public").
                where(post.theme.contains(keyword).
                        or(post.postTitle.contains(keyword).
                                or(post.location.contains(keyword)))).
                offset(pageable.getOffset()).
                limit(pageable.getPageSize()).
                fetch();

        //-----------------------------------------------
//        List<PlanResponseDto> searchList=new ArrayList<>();
//        for(Post post: results){
//            Long userId=user.getId();
//            Long postId=post.getPostId();
//            //스크랩 여부 확인
//            post.setIslike(postService.userLikeTrueOrNot(userId, postId));
//            //댓글 수 확인
//            int reviewCnt=reviewRepository.countByPostId(postId).intValue();
//            PlanResponseDto planResponseDto=new PlanResponseDto(post.getPostId(), post.getPostImgUrl(),post.getPostTitle(),
//                    post.getStartDate(), post.getEndDate(), post.getLocation(), post.getTheme(),
//                    post.isIslike(), post.getLikeCnt(), reviewCnt, post.getUser());
//            searchList.add(planResponseDto);
//        }
        return new PageImpl<>(results, pageable, results.size());
    }

//    //리뷰 수
//    public int countReview(NumberPath<Long> postId, User user){
//        postId= post.postId;
//        QReview review=new QReview("review");
//        List<Review> result=queryFactory.
//                selectFrom(review).
//                where(review.user.eq(user).and(review.postId.eq(postId))).
//                fetch();
//        return result.size();
////                innerJoin(review.user).
////                on(review.user.eq(user))


//    }
}
