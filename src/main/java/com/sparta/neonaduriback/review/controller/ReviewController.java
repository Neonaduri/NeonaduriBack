package com.sparta.neonaduriback.review.controller;

/**
 * [controller] - reviewController
 *
 * @class   : reviewController
 * @author  : 오예령
 * @since   : 2022.05.07
 * @version : 1.0
 *
 *   수정일     수정자             수정내용
 *  --------   --------    ---------------------------
 *
 */

import com.sparta.neonaduriback.common.image.service.S3Uploader;
import com.sparta.neonaduriback.review.dto.MyReviewListDto;
import com.sparta.neonaduriback.review.dto.ReviewListDto;
import com.sparta.neonaduriback.review.dto.ReviewRequestDto;
import com.sparta.neonaduriback.review.dto.ReviewResponseDto;
import com.sparta.neonaduriback.review.service.ReviewService;
import com.sparta.neonaduriback.security.UserDetailsImpl;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@AllArgsConstructor
@RequestMapping("/detail/reviews")
@RestController
public class ReviewController {
    private final ReviewService reviewService;
    private final S3Uploader s3Uploader;

    // 후기 등록
    @PostMapping("/{postId}")
    public ResponseEntity<String> createReview(@PathVariable("postId") Long postId,
                                               @RequestParam(value = "reviewContents") String reviewContetns,
                                               @RequestParam(value = "reviewImgFile")MultipartFile multipartFile,
                                               @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws IOException {


        if(multipartFile.isEmpty()){
            reviewService.createReviewOnlyContents(postId, reviewContetns, userDetails.getUser());
        }else{
            String reviewImgUrl = s3Uploader.upload(multipartFile, "static");
            ReviewRequestDto reviewRequestDto = new ReviewRequestDto(reviewContetns, reviewImgUrl);
            reviewService.createReview(postId, reviewRequestDto, userDetails.getUser());
        }
        return ResponseEntity.status(201)
                .body("201");
    }

    // 후기 조회 - 페이징 처리
    @GetMapping("/{postId}/{pageno}")
    public ResponseEntity<ReviewResponseDto> getReviews(@PathVariable(value = "postId") Long postId, @PathVariable(value = "pageno") int pageno, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // reviewService.getReviews(postId, pageno-1, userDetails);
        Page<ReviewListDto> reviewList = reviewService.getReviews(postId, pageno-1);

        // islastPage
        boolean islastPage=false;
        if(reviewList.getTotalPages()==reviewList.getNumber()+1){
            islastPage=true;
        }
        ReviewResponseDto reviewResponseDto = new ReviewResponseDto(reviewList, islastPage);
        return ResponseEntity.status(200)
                .body(reviewResponseDto);
    }

    //후기 수정
    @PutMapping("/{reviewId}")
    public ResponseEntity<String> updateReviews(@PathVariable("reviewId") Long reviewId,
                                                @RequestParam("reviewImgUrl") String reviewImgUrl,
                                                @RequestParam("reviewImgFile") MultipartFile multipartFile,
                                                @RequestParam("reviewContents") String reviewContents,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        //파일을 보내지 않은 경우 -> url만 보냈거나, url도 보내지 않은 경우
        if(multipartFile.isEmpty()){
            reviewService.updateReview(reviewId,reviewImgUrl,reviewContents,userDetails);
        }else{
            reviewService.updateReviewWithFile(reviewId, multipartFile, reviewContents, userDetails);
        }
        return ResponseEntity.status(201).body("201");
    }

    //후기 수정 전 다시 조회
    @GetMapping("/edit/{reviewId}")
    public ResponseEntity<ReviewListDto> getReviewAgain(@PathVariable("reviewId") Long reviewId,
                                                        @AuthenticationPrincipal UserDetailsImpl userDetails){
        ReviewListDto reviewListDto=reviewService.getReviewAgain(reviewId, userDetails);

        return ResponseEntity.status(200).body(reviewListDto);
    }

    //후기 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable("reviewId") Long reviewId,
                                               @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long deletedId=reviewService.deleteReview(reviewId,userDetails);
        if(reviewId==deletedId){
            return ResponseEntity.status(200).body("200");
        }else{
            return  ResponseEntity.status(400).body("400");
        }
    }

}
