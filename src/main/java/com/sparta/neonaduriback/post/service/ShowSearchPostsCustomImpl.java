package com.sparta.neonaduriback.post.service;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.neonaduriback.post.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static com.sparta.neonaduriback.post.model.QPost.post;


@RequiredArgsConstructor
public class ShowSearchPostsCustomImpl implements ShowSearchPostsCustom {


    private final JPAQueryFactory queryFactory;
//    private final QueryDslUtil queryDslUtil;


    @Override
    public Page<Post> keywordSearch(String keyword, Pageable pageable) {

//동적 sorting 적용한 방법 1
//        List<OrderSpecifier> SORTING = queryDslUtil.getAllOrderSpecifiers(pageable);

//        List<Post> results = queryFactory.selectFrom(
//                post).
////                ExpressionUtils.as(
////                        JPAExpressions.selectFrom(post)
////                                .where(post.ispublic.eq(true)),"public").
//                where(post.theme.contains(keyword).and(post.ispublic.eq(true)).
//                        or(post.postTitle.contains(keyword).and(post.ispublic.eq(true)).
//                                or(post.location.contains(keyword).and(post.ispublic.eq(true))
//                                ))).
//                offset(pageable.getOffset()).
//                limit(pageable.getPageSize()).
////                orderBy(SORTING.stream().toArray(OrderSpecifier[]::new)).
//                fetch();

//        JPAQuery<Post> countQuery = queryFactory
//                .select(post)
//                .from(post)
//                .where(post.theme.contains(keyword).and(post.ispublic.eq(true)).
//                        or(post.postTitle.contains(keyword).and(post.ispublic.eq(true)).
//                                or(post.location.contains(keyword).and(post.ispublic.eq(true))
//                                )));
//        System.out.println("개수"+countQuery.fetchCount());
//
//        return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchCount);


//동적 sorting 적용한 방법2
        QueryResults<Post> results = queryFactory.selectFrom(post).
                                    where(post.theme.contains(keyword).and(post.ispublic.eq(true)).
                                        or(post.postTitle.contains(keyword).and(post.ispublic.eq(true)).
                                                or(post.location.contains(keyword).and(post.ispublic.eq(true))
                                    ))).
                                    orderBy(getOrderSpecifier(pageable.getSort()).stream().toArray(OrderSpecifier[]::new)).
                                    offset(pageable.getOffset()).
                                    limit(pageable.getPageSize()).
                                    fetchResults();
        List<Post> content=results.getResults();
        long totalCount=results.getTotal();

        return new PageImpl<>(content, pageable, totalCount);

    }

    //OrderSpecifier 구현
    private List<OrderSpecifier> getOrderSpecifier(Sort sort) {
        List<OrderSpecifier> orders = new ArrayList<>();
        // Sort
        sort.stream().forEach(order -> {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            System.out.println("디렉션:" + direction);
            String prop = order.getProperty();
            System.out.println("prop:" + prop);
            PathBuilder orderByExpression = new PathBuilder(Post.class, "post");
            System.out.println("orderByExpression:" + orderByExpression.get(prop));
            orders.add(new OrderSpecifier(direction, orderByExpression.get(prop)));
        });
        return orders;
    }
}


