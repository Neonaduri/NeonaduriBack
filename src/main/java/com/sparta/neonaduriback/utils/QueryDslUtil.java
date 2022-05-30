package com.sparta.neonaduriback.utils;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.sparta.neonaduriback.post.model.QPost;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class QueryDslUtil {

    public static OrderSpecifier<?> getSortedColumn(Order order, Path<?> parent, String fieldName) {
        Path<Object> fieldPath = Expressions.path(Object.class, parent, fieldName);
        return new OrderSpecifier(order, fieldPath);
    }

    public List<OrderSpecifier> getAllOrderSpecifiers(Pageable pageable) {

        List<OrderSpecifier> SORTING = new ArrayList<>();

        if (!pageable.getSort().isEmpty()) {
            for (Sort.Order order : pageable.getSort()) {
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                switch (order.getProperty()) {
                    case "postId":
                        OrderSpecifier<?> postId = QueryDslUtil.getSortedColumn(direction, QPost.post, "postId");
                        SORTING.add(postId);
                        break;
                    case "viewCnt":
                        OrderSpecifier<?> viewCnt = QueryDslUtil.getSortedColumn(direction, QPost.post, "viewCnt");
                        SORTING.add(viewCnt);
                        break;
                    case "likeCnt":
                        OrderSpecifier<?> likeCnt = QueryDslUtil.getSortedColumn(direction, QPost.post, "likeCnt");
                        SORTING.add(likeCnt);
                        break;
                    default:
                        break;
                }
            }
        }

        return SORTING;
    }
}