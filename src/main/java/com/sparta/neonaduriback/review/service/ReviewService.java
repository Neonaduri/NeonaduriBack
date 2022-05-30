package com.sparta.neonaduriback.review.service;

/**
 * [Service] - 후기 등록 ReviewService
 *
 * @class   : ReviewService
 * @author  : 오예령
 * @since   : 2022.05.07
 * @version : 1.0
 *
 *   수정일     수정자             수정내용
 *  --------   --------    ---------------------------
 *
 */

import com.sparta.neonaduriback.common.image.model.Image;
import com.sparta.neonaduriback.common.image.repository.ImageRepository;
import com.sparta.neonaduriback.common.image.service.S3Uploader;
import com.sparta.neonaduriback.login.model.User;
import com.sparta.neonaduriback.post.repository.PostRepository;
import com.sparta.neonaduriback.review.dto.MyReviewListDto;
import com.sparta.neonaduriback.review.dto.ReviewListDto;
import com.sparta.neonaduriback.review.dto.ReviewRequestDto;
import com.sparta.neonaduriback.review.dto.ReviewResponseDto;
import com.sparta.neonaduriback.review.model.Review;
import com.sparta.neonaduriback.review.repository.ReviewRepository;
import com.sparta.neonaduriback.security.UserDetailsImpl;
import com.sparta.neonaduriback.utils.Paging;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final PostRepository postRepsitory;
    private final ImageRepository imageRepository;
    private final Paging paging;
    private final S3Uploader s3Uploader;

    // 후기 등록
    public ResponseEntity<ReviewResponseDto> createReview(Long postId, ReviewRequestDto reviewRequestDto, User user) {

        String reviewContents = reviewRequestDto.getReviewContents();
        String reviewImgUrl = reviewRequestDto.getReviewImgUrl();

        Review review = new Review(reviewContents, reviewImgUrl, user, postId);

        // 후기 저장
        reviewRepository.save(review);

        ReviewResponseDto reviewRequestDto1 = new ReviewResponseDto(review.getId(), reviewContents, reviewImgUrl, review.getCreatedAt(), review.getModifiedAt(), user);
        return ResponseEntity.status(201).body(reviewRequestDto1);
    }

    // 후기 내용만 등록 시
    public ResponseEntity<ReviewResponseDto> createReviewOnlyContents(Long postId, String reviewContetns, User user) {

        Review review = new Review(reviewContetns, user, postId);
        reviewRepository.save(review);

        ReviewResponseDto reviewRequestDto = new ReviewResponseDto(review.getId(), reviewContetns, review.getCreatedAt(), review.getModifiedAt(), user);
        return ResponseEntity.status(201)
                .body(reviewRequestDto);
    }

    // 후기 조회
    public Page<?> getReviews(Long postId, int pageno) {

        // 후기 list 빈 배열 선언
        List<ReviewListDto> reviewList = new ArrayList<>();

        // postId로 해당 post의 후기 list 가져오기
        List<Review> reviews = reviewRepository.findAllByPostIdOrderByCreatedAtDesc(postId);

        Pageable pageable = getPageable(pageno);

        for (Review review : reviews) {

            ReviewListDto reviewListDto = new ReviewListDto(review.getId(), review.getUser().getNickName(), review.getUser().getProfileImgUrl(), review.getReviewContents(),
                    review.getReviewImgUrl(), review.getCreatedAt(), review.getModifiedAt());

            reviewList.add(reviewListDto);
        }

        int start = pageno * 8;
        int end = Math.min((start + 8), reviewList.size());

        return paging.overPages(reviewList, start, end, pageable, pageno);
    }

    //페이징
    private Pageable getPageable(int pageno) {
        Sort.Direction direction = Sort.Direction.DESC;
        Sort sort = Sort.by(direction, "id");
        return PageRequest.of(pageno, 8, sort);
    }

    //후기수정(URL)
    @Transactional
    public ResponseEntity<ReviewResponseDto> updateReview(Long reviewId, String reviewImgUrl, String reviewContents, UserDetailsImpl userDetails) {

        Review review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new IllegalArgumentException("해당 리뷰가 없습니다")
        );
        if (!review.getUser().getId().equals(userDetails.getUser().getId())) {
            throw new IllegalArgumentException("리뷰 작성자만 수정이 가능합니다");
        }
        //이미지 유알엘도 없다 -> 사진도 지운다
        if (reviewImgUrl.equals("")) {

            review.update(reviewContents, reviewImgUrl);
            reviewRepository.save(review);
            ReviewResponseDto reviewRequestDto = new ReviewResponseDto(reviewId, reviewContents, reviewImgUrl, review.getCreatedAt(), review.getModifiedAt(), userDetails.getUser());
            return ResponseEntity.status(201).body(reviewRequestDto);

        } else {
            //이미지 수정 x 기존값 그대로
            review.update(reviewContents, reviewImgUrl);
            reviewRepository.save(review);
            ReviewResponseDto reviewRequestDto = new ReviewResponseDto(reviewId, reviewContents, review.getReviewImgUrl(), review.getCreatedAt(), review.getModifiedAt(), userDetails.getUser());
            return ResponseEntity.status(201).body(reviewRequestDto);
        }
    }

    //후기수정(사진 파일)
    @Transactional
    public ResponseEntity<ReviewResponseDto> updateReviewWithFile(Long reviewId, MultipartFile multipartFile, String reviewContents, UserDetailsImpl userDetails) throws IOException {

        Review review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new IllegalArgumentException("해당 리뷰가 없습니다")
        );
        if (!review.getUser().getId().equals(userDetails.getUser().getId())) {
            throw new IllegalArgumentException("리뷰 작성자만 수정이 가능합니다");
        }

        Long userId = userDetails.getUser().getId();

        String reviewImgUrl = s3Uploader.updateReviewImage(multipartFile, "static", reviewId, userId);
        System.out.println("url" + reviewImgUrl);
        review.update(reviewContents, reviewImgUrl);
        reviewRepository.save(review);
        ReviewResponseDto reviewRequestDto = new ReviewResponseDto(reviewId, reviewContents, reviewImgUrl, review.getCreatedAt(), review.getModifiedAt(), userDetails.getUser());
        return ResponseEntity.status(201).body(reviewRequestDto);
    }

    // 후기 수정 전 다시 조회
    public ReviewListDto getReviewAgain(Long reviewId, UserDetailsImpl userDetails) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new IllegalArgumentException("해당 리뷰가 없습니다")
        );

        String nickName = review.getUser().getNickName();
        String profileImgUrl = review.getUser().getProfileImgUrl();
        String reviewContents = review.getReviewContents();
        String reviewImgUrl = review.getReviewImgUrl();
        LocalDateTime createdAt = review.getCreatedAt();
        LocalDateTime modifiedAt = review.getModifiedAt();

        ReviewListDto reviewListDto = new ReviewListDto(reviewId, nickName, profileImgUrl, reviewContents,
                reviewImgUrl, createdAt, modifiedAt);
        return reviewListDto;
    }

    @Transactional
    //후기 삭제
    public ResponseEntity<String> deleteReview(Long reviewId, UserDetailsImpl userDetails) {

        try {
            Review review = reviewRepository.findById(reviewId).orElseThrow(
                    () -> new IllegalArgumentException("해당 리뷰가 없습니다")
            );
            if (!review.getUser().getId().equals(userDetails.getUser().getId())) {
                throw new IllegalArgumentException("리뷰 작성자만 삭제가 가능합니다");
            }

            else if (review.getReviewImgUrl() == null || review.getReviewImgUrl().equals("")) {
                reviewRepository.deleteById(reviewId);
            }

            String reviewImgUrl = review.getReviewImgUrl();
            Optional<Image> image = imageRepository.findByImageUrlAndUserId(reviewImgUrl, userDetails.getUser().getId());

            if (image.isPresent()) {
                String filename = image.get().getFilename();

                s3Uploader.deleteImage(filename);
                imageRepository.deleteByImageUrl(reviewImgUrl);
                reviewRepository.deleteById(reviewId);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body("400");
        }
        return ResponseEntity.status(200).body("200");
    }


    //내가 쓴 후기 조회
    public List<MyReviewListDto> showMyReviews(UserDetailsImpl userDetails) {
        User user=userDetails.getUser();
        List<Review> reviewList=reviewRepository.findAllByUserOrderByCreatedAtDesc(user);

        List<MyReviewListDto> myReviewList=new ArrayList<>();

        for(Review eachReview:reviewList){
            MyReviewListDto myReviewListDto=new MyReviewListDto(eachReview.getId(), eachReview.getPostId(),
                    eachReview.getUser().getNickName(), eachReview.getReviewContents(), eachReview.getReviewImgUrl(),
                    eachReview.getCreatedAt(), eachReview.getModifiedAt());
            myReviewList.add(myReviewListDto);
        }
        return myReviewList;
    }
}

