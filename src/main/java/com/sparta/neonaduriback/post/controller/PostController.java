package com.sparta.neonaduriback.post.controller;

import com.sparta.neonaduriback.login.model.User;
import com.sparta.neonaduriback.post.dto.*;
import com.sparta.neonaduriback.post.model.Post;
import com.sparta.neonaduriback.post.service.PostService;
import com.sparta.neonaduriback.review.dto.MyReviewListDto;
import com.sparta.neonaduriback.review.service.ReviewService;
import com.sparta.neonaduriback.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostService postService;
    private final ReviewService reviewService;

    //방 만들어주기
    @PostMapping("/plans")
    public ResponseEntity<RoomMakeRequestDto> makeRoom(@RequestBody RoomMakeRequestDto roomMakeRequestDto,
                                                       @AuthenticationPrincipal UserDetailsImpl userDetails) {

        User user= userDetails.getUser();
        return ResponseEntity.status(201).body(postService.makeRoom(roomMakeRequestDto, user));
    }

    //만들어진 방에서 계획 다시 조회하기
    @GetMapping("/plans/{postUUID}")
    public RoomMakeRequestDto getPost(@PathVariable String postUUID) {
        return postService.getPost(postUUID);
    }


    //자랑하기, 나만보기 저장
    @PutMapping("/plans/save")
    public ResponseEntity<String > showAll(@RequestBody PostRequestDto postRequestDto,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user=userDetails.getUser();
        String postUUID=postService.showAll(postRequestDto, user);
        if(postRequestDto.getPostUUID().equals(postUUID)){
            return ResponseEntity.status(201).body("201");
        }else{
            return ResponseEntity.status(400).body("400");
        }
    }

    //내가 찜한 여행목록 조회
    @GetMapping("/user/plans/like/{pageno}")
    public MyLikePagingDto showMyLike(@PathVariable("pageno") int pageno, @AuthenticationPrincipal UserDetailsImpl userDetails){

        //MyLikePostDto
        Page<?> postList=postService.showMyLike(pageno-1,userDetails);

        //totalLike
        int totalLike=postService.getTotalLike(userDetails);

        //islastPage
        boolean islastPage=false;
        if(postList.getTotalPages()==postList.getNumber()+1){
            islastPage=true;
        }
        System.out.println(postList.getNumber()+1);
        MyLikePagingDto myLikePagingDto = new MyLikePagingDto(totalLike, postList, islastPage);
        return myLikePagingDto;
    }

    //인기 게시물 4개 조회
    @GetMapping("/best")
    public List<PlanResponseDto> showBestPosts(@AuthenticationPrincipal UserDetailsImpl userDetails){

        return postService.showBestPosts(userDetails);
    }


    //지역별 조회(5개)
    @GetMapping("/plans/location/{location}/{pageno}")
    public PlanPagingDto showLocationPosts(@PathVariable("location") String location, @PathVariable("pageno") int pageno,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails){

        Page<?> postList=postService.showLocationPosts(location,pageno-1,userDetails);
        return new PlanPagingDto(postList);
    }


    //지역별 조회 테스트
    @GetMapping("/plans/location/{location}/{pageno}/{sortBy}")
    public PlanPagingDto testLocationPosts(@PathVariable("location") String location,
                                           @PathVariable("pageno") int pageno,
                                           @PathVariable("sortBy") String sortBy,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails
                                           ){
        int page=pageno-1;
        int size=8;
        Page<?> postList=postService.testLocationPosts(location, page,size, sortBy, userDetails);
        return new PlanPagingDto(postList);
    }

    //테마별 조회
    @GetMapping("/plans/theme/{theme}/{pageno}")
    public PlanPagingDto showThemePosts(@PathVariable("theme") String theme, @PathVariable("pageno") int pageno,
                                                  @AuthenticationPrincipal UserDetailsImpl userDetails){
        Page<?> postList=postService.showThemePosts(theme,pageno-1,userDetails);
        return new PlanPagingDto(postList);
    }

    //테마별 조회 테스트
    @GetMapping("/plans/theme/{theme}/{pageno}/{sortBy}")//?theme=맛집&pageno=1&sortBy=postId
    public PlanPagingDto testThemePosts(@PathVariable("theme") String theme,
                                        @PathVariable("pageno") int pageno,
                                        @PathVariable("sortBy") String sortBy,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails){

        int page=pageno-1;
        int size=8;
        Page<?> postList=postService.testThemePosts(theme, page, size, sortBy, userDetails);
        return new PlanPagingDto(postList);
    }

    //검색 결과 조회 => 에러발생 수정하자아아아 (requesetParam으로 dto를 받거나 ModelAttribute로 받는다,,?)
    @GetMapping("/plans/keyword/{keyword}/{pageno}")
    public PlanPagingDto showSearchPosts(@PathVariable("pageno") int pageno, @PathVariable("keyword") String keyword,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails){
        Page<?> postList=postService.showSearchPosts(pageno-1, keyword, userDetails);
        return new PlanPagingDto(postList);
    }

    //검색 querydsl
    @GetMapping("/plans/keyword/results")//?keyword=액티&pageno=1&sortBy=viewCnt
    public PlanPagingDto showSearchPosts(@RequestParam("keyword") String keyword,
                                         @RequestParam("pageno") int pageno,
                                         @RequestParam("sortBy") String sortBy,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails){
        int page= pageno-1;
        int size= 8;
        System.out.println(keyword);
        System.out.println(pageno);
        System.out.println(sortBy);
        System.out.println(page);
        Page<?> postList = postService.testSearchPosts(keyword, page, size, sortBy, userDetails);
        return new PlanPagingDto(postList);
    }

    //상세조회
    @GetMapping("/plans/detail/{postId}")
    public ResponseEntity<Object> showDetail(@PathVariable("postId") Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        Post post=postService.showDetail(postId, userDetails);

        if(post == null){
            return ResponseEntity.status(200).body("비공개 게시물입니다");
        }
        return ResponseEntity.ok(post);
    }

    //내가 등록한 여행 계획 삭제
    @DeleteMapping("/user/plans/{postId}")
    public ResponseEntity<String> deletePost(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                    @PathVariable("postId") Long postId){
        Long deletedPostId=postService.deletePost(userDetails, postId);

        if(postId.equals(deletedPostId)) {
            return ResponseEntity.status(200).body("삭제가 정상적으로 완료됨");
        } else {
            return ResponseEntity.status(400).body("삭제 실패!!");
        }
    }


    //플랜 저장 안함.(새로고침 뒤로가기)
    @DeleteMapping("/plans/{postUUID}")
    public ResponseEntity<String> leavePost(@PathVariable String postUUID,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return postService.leavePost(postUUID, user);
    }


    //내가 작성한 플랜조회
    @GetMapping("/user/plans/{pageno}")
    public PlanPagingDto getMyPost(@PathVariable("pageno") int pageno,
                                @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Page<?> myplanList = postService.getMyPosts(pageno-1,userDetails);
        return new PlanPagingDto(myplanList);
    }

    // 내가 쓴 후기 조회
    @GetMapping("/user/review")
    public List<MyReviewListDto> showMyReviews(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return reviewService.showMyReviews(userDetails);
    }


}
