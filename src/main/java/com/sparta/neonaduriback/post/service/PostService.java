package com.sparta.neonaduriback.post.service;

import com.sparta.neonaduriback.like.model.Likes;
import com.sparta.neonaduriback.like.repository.LikeRepository;
import com.sparta.neonaduriback.login.model.User;
import com.sparta.neonaduriback.post.days.model.Days;
import com.sparta.neonaduriback.post.days.repository.DaysRepository;
import com.sparta.neonaduriback.post.dto.*;
import com.sparta.neonaduriback.post.model.Post;
import com.sparta.neonaduriback.post.places.Places;
import com.sparta.neonaduriback.post.places.PlacesRepository;
import com.sparta.neonaduriback.post.repository.PostRepository;
import com.sparta.neonaduriback.review.repository.ReviewRepository;
import com.sparta.neonaduriback.security.UserDetailsImpl;
import com.sparta.neonaduriback.utils.ImageBundle;
import com.sparta.neonaduriback.utils.Paging;
import com.sparta.neonaduriback.utils.QueryDslUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final DaysRepository daysRepository;
    private final PlacesRepository placesRepository;
    private final LikeRepository likeRepository;
    private final ReviewRepository reviewRepository;
    private final ImageBundle imageBundle;
    private final QueryDslUtil queryDslUtil;
    private final Paging paging;

    //방 만들기
    public RoomMakeRequestDto makeRoom(RoomMakeRequestDto roomMakeRequestDto, User user) {

        Post post= new Post(roomMakeRequestDto, user);
        postRepository.save(post);
        Long postId=post.getPostId();
        String postUUID=post.getPostUUID();
        roomMakeRequestDto.setPostId(postId);
        roomMakeRequestDto.setPostUUID(postUUID);
        roomMakeRequestDto.setUser(user);
        return roomMakeRequestDto;
    }

    // 플랜 계획 조회하기
    public RoomMakeRequestDto getPost(String postUUID) {
        System.out.println(postUUID);
        Post post = postRepository.findByPostUUID(postUUID).orElseThrow(
                ()-> new IllegalArgumentException("게시물이 존재하지 않습니다.")
        );
        RoomMakeRequestDto roomMakeRequestDto = new RoomMakeRequestDto(post.getPostId(), post.getPostUUID(),post.getStartDate(),
                post.getEndDate(), post.getDateCnt(), post.getPostTitle(), post.getLocation(), post.getTheme(), post.getUser());
        return roomMakeRequestDto;
    }

    //자랑하기
    @Transactional
    public String showAll(PostRequestDto postRequestDto, User user) {

        postRepository.findByUserAndPostUUID(user, postRequestDto.getPostUUID()).orElseThrow(
                ()->new IllegalArgumentException("방을 생성한 유저만 여행 계획 저장이 가능합니다.")
        );

        List<DayRequestDto> dayRequestDtoList= postRequestDto.getDays();
        List<Days> daysList=new ArrayList<>();
        for(int i=0; i<dayRequestDtoList.size();i++){
            //n일차 구하기
            int dateNumber=i+1;

            List<PlaceRequestDto> placeRequestDtoList=dayRequestDtoList.get(i).getPlaces();
            //위에 리스트를 정렬
            Comparator<PlaceRequestDto> comparator = new Comparator<PlaceRequestDto>() {
                @Override
                public int compare(PlaceRequestDto a, PlaceRequestDto b) {
                    //오름차순(뺄셈이 양수일 시)
                    return a.getPlanTime()- b.getPlanTime();
                }
            };
            Collections.sort(placeRequestDtoList, comparator);

            List<Places> placesList=new ArrayList<>();
            //n일차에 대한 n개의 방문 장소 Places entity에 저장
            for(PlaceRequestDto placeRequestDtos:placeRequestDtoList){
                Places places= new Places(placeRequestDtos);
                placesRepository.save(places);
                placesList.add(places);
            }
            //Days entity에 저장
            Days days= new Days(dateNumber, placesList);
            daysRepository.save(days);
            daysList.add(days);
        }
        Post post=postRepository.findByPostUUID(postRequestDto.getPostUUID()).orElseThrow(
                ()->new NullPointerException("해당 계획이 없습니다")
        );
        postRequestDto.setPostImgUrl(imageBundle.searchImage());
        //전체 여행계획 저장
        post.completeSave(postRequestDto,daysList);
        postRepository.save(post);
        return post.getPostUUID();
    }

    //내가 찜한 게시물 조회
    public Page<?> showMyLike(int pageno, UserDetailsImpl userDetails) {

        //찜한 게시물 리스트
        List<MyLikePostDto> postList=new ArrayList<>();
        //찜 엔티티에서 자신의 id를 통해 찾으면 자기가 찜한 게시물이 뜰것! (최근 찜한거 부터 가져오기)
        List<Likes> likesList=likeRepository.findAllByUserIdOrderByModifiedAtDesc(userDetails.getUser().getId());
        Pageable pageable= getPageable(pageno);

        //리팩토링 필요
        for(Likes likes:likesList){
            Optional<Post> postOptional=postRepository.findById(likes.getPostId());

            //찜한 게시물이 존재할 경우
            if(postOptional.isPresent()){
                //찜한 게시물이니 true값 입력
                Post post=postOptional.get();
                boolean islike=true;
//                int likeCnt=countLike(post.getPostId());
                MyLikePostDto myLikePostDto=new MyLikePostDto(post.getPostId(), post.getPostImgUrl()
                        ,post.getPostTitle(),post.getLocation(),post.getStartDate(),
                        post.getEndDate(),islike, post.getLikeCnt(),post.getTheme());
                postList.add(myLikePostDto);
            }

        }

        int start = pageno * 6;
        int end = Math.min((start + 6), postList.size());

        return paging.overPages(postList, start, end, pageable, pageno);
    }

    //페이징
    private Pageable getPageable(int pageno) {
        Sort.Direction direction = Sort.Direction.DESC;
        Sort sort = Sort.by(direction, "id");
        return PageRequest.of(pageno, 6, sort);
    }

    //totalLike 계산하기
    public int getTotalLike(UserDetailsImpl userDetails) {

        //내가 쓴 게시물 다 조회
        List<Post> posts=postRepository.findAllByUserOrderByCreatedAtDesc(userDetails.getUser());
        int totalLike=0;

        //내가 쓴 게시물이 있다면 찜 엔티티에서 게시물 갯수 카운트 -> 유저들한테 찜받은 갯수를 말함
        for(Post eachPost: posts){
            Long postId=eachPost.getPostId();
            totalLike+=likeRepository.countByPostId(postId);
        }

        return totalLike;
    }

    //BEST 4 게시물 조회
    public List<PlanResponseDto> showBestPosts(UserDetailsImpl userDetails) {

        List<Post> postList=postRepository.findAllByIspublicTrueOrderByLikeCntDesc();
        List<PlanResponseDto> bestList=new ArrayList<>();

        for(int i=0;i<postList.size();i++){

            if(i>3) break;
            Post post=postList.get(i);
            if(post.getDays().size()==0) continue;
            //찜받은 갯수 확인
//            int likeCnt=countLike(post.getPostId());
            Long userId=userDetails.getUser().getId();
            //로그인 유저가 찜한 것인지 여부 확인
            post.setIslike(userLikeTrueOrNot(userId, post.getPostId()));
            //게시물의 reviewCnt 계산
            int reviewCnt=reviewRepository.countByPostId(post.getPostId()).intValue();

            PlanResponseDto planResponseDto =new PlanResponseDto(post.getPostId(), post.getPostImgUrl(),post.getPostTitle(),
                    post.getStartDate(), post.getEndDate(), post.getLocation(),post.getTheme(), post.isIslike(), post.getLikeCnt(), reviewCnt, post.getUser());
            bestList.add(planResponseDto);
        }
        return bestList;
    }

    //로그인한 유저가 찜한 게시물 인지 확인하는 메소드(setIsisLike)
    public boolean userLikeTrueOrNot(Long userId, Long postId){
        Optional<Likes> isUserLike=likeRepository.findByPostIdAndUserId(postId,userId);
        //유저가 찜한 기록이 있디면
        if(isUserLike.isPresent()){
            return true;
        }else{
            //찜한 기록 없다면
            return false;
        }
    }

    //지역별 검색(5개 조회)
    public Page<?> showLocationPosts(String location, int pageno, UserDetailsImpl userDetails) {

        List<Post> locationPostList=postRepository.findAllByLocationOrderByLikeCntDesc(location);

        List<PlanResponseDto> locationList=new ArrayList<>();

        Pageable pageable= getPageableList5(pageno);


        for(int i=0;i<locationPostList.size();i++){
            Post post=locationPostList.get(i);
            //나만보기 상태이면 추가 안함(jpa로 조건 걸 수 있으나 db에 너무 많은 작업이 가는 것 같아서 자바단에서 실행)
            if(!post.isIspublic() || post.getDays().size()==0) continue;
            //찜받은 갯수 확인
//            int likeCnt=countLike(post.getPostId());
            Long userId=userDetails.getUser().getId();
            //로그인 유저가 찜한 것인지 여부 확인
            post.setIslike(userLikeTrueOrNot(userId, post.getPostId()));
            //게시물의 reviewCnt 계산
            int reviewCnt=reviewRepository.countByPostId(post.getPostId()).intValue();

            PlanResponseDto planResponseDto =new PlanResponseDto(post.getPostId(), post.getPostImgUrl(),post.getPostTitle(),
                    post.getStartDate(), post.getEndDate(), post.getLocation(),post.getTheme(), post.isIslike(), post.getLikeCnt(), reviewCnt, post.getUser());
            locationList.add(planResponseDto);
        }

        int start=pageno*5;
        int end=Math.min((start+5), locationList.size());

        return paging.overPages(locationList,start,end,pageable,pageno);
    }

    public Page<PlanResponseDto> testLocationPosts(String location, int page, int size, String sortBy, UserDetailsImpl userDetails) {

        Sort.Direction direction= Sort.Direction.DESC;
        Sort sort= Sort.by(direction, sortBy).and(Sort.by(direction, "postId"));
        Pageable pageable= PageRequest.of(page, size, sort);

        Page<Post> posts=postRepository.findAllByLocationAndIspublicTrue(location, pageable);
        List<PlanResponseDto> locationList=new ArrayList<>();

        for(Post post: posts){
            //나만보기 상태이면 추가 안함(jpa로 조건 걸 수 있으나 db에 너무 많은 작업이 가는 것 같아서 자바단에서 실행)
            if(post.getDays().size()==0) continue;
            //찜받은 갯수 확인
//            int likeCnt=countLike(post.getPostId());
            Long userId=userDetails.getUser().getId();
            //로그인 유저가 찜한 것인지 여부 확인
            post.setIslike(userLikeTrueOrNot(userId, post.getPostId()));
            //게시물의 reviewCnt 계산
            int reviewCnt=reviewRepository.countByPostId(post.getPostId()).intValue();

            PlanResponseDto planResponseDto =new PlanResponseDto(post.getPostId(), post.getPostImgUrl(),post.getPostTitle(),
                    post.getStartDate(), post.getEndDate(), post.getLocation(),post.getTheme(), post.isIslike(), post.getLikeCnt(), reviewCnt, post.getUser());
            locationList.add(planResponseDto);
        }

        Page<PlanResponseDto> planResponseDtos=new PageImpl<>(locationList, pageable, posts.getTotalElements());
        return planResponseDtos;
    }

    //bestList, locationList 페이징
    private Pageable getPageableList(int pageno) {
        Sort.Direction direction = Sort.Direction.DESC;
        Sort sort = Sort.by(direction, "id");
        return PageRequest.of(pageno, 8, sort);
    }

    //bestList, locationList 페이징
    private Pageable getPageableList5(int pageno) {
        Sort.Direction direction = Sort.Direction.DESC;
        Sort sort = Sort.by(direction, "id");
        return PageRequest.of(pageno, 5, sort);
    }

    //테마별 검색조회(8개)
    public Page<?> showThemePosts(String theme, int pageno, UserDetailsImpl userDetails) {

        List<Post> themePostList=postRepository.findAllByThemeOrderByLikeCntDesc(theme);

        List<PlanResponseDto> themeList=new ArrayList<>();

        Pageable pageable= getPageableList(pageno);


        for(int i=0;i<themePostList.size();i++){
            Post post=themePostList.get(i);

            //나만보기 상태이면 추가 안함(jpa로 조건 걸 수 있으나 db에 너무 많은 작업이 가는 것 같아서 자바단에서 실행)
            //추가로 days 길이가 0이면 방만 만들어지고, 여행계획은 세우지 않은 상황이니 마찬가지로 조회 시 걸러준다
            if(!post.isIspublic() || post.getDays().size()==0) continue;
            //찜받은 갯수 확인
//            int likeCnt=countLike(post.getPostId());
            Long userId=userDetails.getUser().getId();
            //로그인 유저가 찜한 것인지 여부 확인
            post.setIslike(userLikeTrueOrNot(userId, post.getPostId()));
            //게시물의 reviewCnt 계산
            int reviewCnt=reviewRepository.countByPostId(post.getPostId()).intValue();

            PlanResponseDto themeAndSearchDto =new PlanResponseDto(post.getPostId(), post.getPostImgUrl(),post.getPostTitle(),
                    post.getStartDate(), post.getEndDate(), post.getLocation(),post.getTheme(), post.isIslike(), post.getLikeCnt(), reviewCnt, post.getUser());
            themeList.add(themeAndSearchDto);
        }

        int start=pageno*8;
        int end=Math.min((start+8), themeList.size());

        return paging.overPages(themeList,start,end,pageable,pageno);
    }
    //테마별 조회 테스트
    public Page<?> testThemePosts(String theme, int page, int size, String sortBy, UserDetailsImpl userDetails) {
        
        Sort.Direction direction = Sort.Direction.DESC;
        System.out.println(direction);
        Sort sort = Sort.by(direction, sortBy).and(Sort.by(direction, "postId"));

        Pageable pageable=PageRequest.of(page, size, sort);
        
        Page<Post> posts=postRepository.findAllByThemeAndIspublicTrue(theme, pageable);

        List<PlanResponseDto> themeList=new ArrayList<>();

        for(Post post: posts){

            System.out.println(post.getDays().size());
            //나만보기 상태이면 추가 안함(jpa로 조건 걸 수 있으나 db에 너무 많은 작업이 가는 것 같아서 자바단에서 실행)
            if(post.getDays().size()==0) continue;

            Long userId=userDetails.getUser().getId();
            //로그인 유저가 찜한 것인지 여부 확인

            post.setIslike(userLikeTrueOrNot(userId, post.getPostId()));
            //게시물의 reviewCnt 계산
            int reviewCnt=reviewRepository.countByPostId(post.getPostId()).intValue();
            PlanResponseDto planResponseDto =new PlanResponseDto(post.getPostId(), post.getPostImgUrl(),post.getPostTitle(),
                    post.getStartDate(), post.getEndDate(), post.getLocation(),post.getTheme(), post.isIslike(), post.getLikeCnt(), reviewCnt, post.getUser());
            themeList.add(planResponseDto);
        }
        Page<PlanResponseDto> planResponseDtos=new PageImpl<>(themeList, pageable, posts.getTotalElements());
        return planResponseDtos;
    }


    //게시물 상세조회
    public Post showDetail(Long postId, UserDetailsImpl userDetails) {

        Post post=postRepository.findById(postId).orElseThrow(
                ()->new IllegalArgumentException("해당 게시물이 없습니다")
        );

        post.setIslike(userLikeTrueOrNot(userDetails.getUser().getId(), postId));

        //전체공개이고
        if(post.isIspublic()){

            // 게시글 조회 수 계산
            post.setViewCnt(post.getViewCnt()+1);
            postRepository.save(post);

            return postRepository.findById(postId).orElseThrow(
                    ()->new IllegalArgumentException("해당 게시물이 없습니다")
            );
        }else{
            // 현재 유저가 작성자와 같으면
            if(post.getUser().getId().equals(userDetails.getUser().getId())){
                return postRepository.findById(postId).orElseThrow(
                        ()->new IllegalArgumentException("해당 게시물이 없습니다")
                );
            }else{
                return null;
            }
        }
    }
//    public PostDto showDetail(Long postId, UserDetailsImpl userDetails) {
//
//        Post post = postRepository.findById(postId).orElseThrow(
//                () -> new IllegalArgumentException("해당 계획이 없습니다.")
//        );
//
//        post.setIslike(userLikeTrueOrNot(userDetails.getUser().getId(), postId));
//
//        System.out.println("post.getPostTitle() = " + post.getPostTitle());
//
//        Days days = daysRepository.findById(postId).orElseThrow(
//                ()-> new IllegalArgumentException("해당 일차가 없습니다.")
//        );
//
//        Places places = placesRepository.findById(days.getDayId()).orElseThrow(
//                () -> new IllegalArgumentException("해당 장소가 없습니다.")
//        );
//
//        PlacesDto placesDto = new PlacesDto(places.getPlaceId(), places.getPlaceName(),places.getPlaceInfoUrl(), places.getCategory(),places.getAddress(),
//                places.getRoadAddress(),places.getPlaceMemo(), places.getPlanTime(),places.getLat(),places.getLng());
//
//        System.out.println("placesDto.getPlaceName() = " + placesDto.getPlaceName());
//        System.out.println("placesDto.getPlanTime() = " + placesDto.getPlanTime());
//
//
//        DaysDto daysDto = new DaysDto(days.getDayId(), days.getDateNumber(), List<PlacesDto>);
//
//        System.out.println("daysDto.getDateNumber() = " + daysDto.getDateNumber());
//        System.out.println("daysDto.getPlaces() = " + daysDto.getPlaces());
//
//        PostDto postDto = new PostDto(post.getPostId(), post.getPostUUID(), post.getStartDate(), post.getEndDate(),
//                post.getDateCnt(), post.getPostTitle(), post.getLocation(), post.getPostImgUrl(), post.getTheme(),
//                post.isIslike(), post.getLikeCnt(), post.isIspublic(), post.getViewCnt(), post.getUser(), Collections.singletonList(daysDto));
//
//        System.out.println("postDto.getPostTitle() = " + postDto.getPostTitle());
//        System.out.println("postDto.getUser() = " + postDto.getUser().getNickName());
//
//        //전체공개이고
//        if (post.isIspublic()) {
//
//            // 게시글 조회 수 계산
//            post.setViewCnt(post.getViewCnt() + 1);
//            postRepository.save(post);
//
//            return postDto;
//            } else {
//                return null;
//        }
//    }

    //검색결과 조회
    public Page<?> showSearchPosts(int pageno, String keyword, UserDetailsImpl userDetails) {
        String postTitle=keyword;
        String location=keyword;
        String theme=keyword;

        List<Post> postList=postRepository.findByPostTitleContainingOrLocationContainingOrThemeContainingOrderByCreatedAtDesc(
                postTitle,location,theme
        );
        List<PlanResponseDto> searchList=new ArrayList<>();

        Pageable pageable= getPageableList(pageno);


        for(int i=0;i<postList.size();i++){
            Post post=postList.get(i);
            //나만보기 상태이면 추가 안함(jpa로 조건 걸 수 있으나 db에 너무 많은 작업이 가는 것 같아서 자바단에서 실행)
            if(!post.isIspublic() || post.getDays().size()==0) continue;
            //찜받은 갯수 확인
//            int likeCnt=countLike(post.getPostId());
            Long userId=userDetails.getUser().getId();
            //로그인 유저가 찜한 것인지 여부 확인
            post.setIslike(userLikeTrueOrNot(userId, post.getPostId()));
            //게시물의 reviewCnt 계산
            int reviewCnt=reviewRepository.countByPostId(post.getPostId()).intValue();

            PlanResponseDto themeAndSearchDto =new PlanResponseDto(post.getPostId(), post.getPostImgUrl(),post.getPostTitle(),
                    post.getStartDate(), post.getEndDate(), post.getLocation(),post.getTheme(), post.isIslike(), post.getLikeCnt(), reviewCnt, post.getUser());
            searchList.add(themeAndSearchDto);
        }

        int start=pageno*8;
        int end=Math.min((start+8), searchList.size());

        return paging.overPages(searchList,start,end,pageable,pageno);
    }
    //검색테스트
    public Page<?> testSearchPosts(String keyword, int page, int size, String sortBy,UserDetailsImpl userDetails) {
        Sort.Direction direction=Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy).and(Sort.by(direction, "postId"));
        Pageable pageable=PageRequest.of(page, size,sort);
        Page<Post> searchResults=postRepository.keywordSearch(keyword, pageable);
        System.out.println("totalelements"+searchResults.getTotalElements());

        List<PlanResponseDto> searchList=new ArrayList<>();
        for(Post post: searchResults){
//            if(!post.isIspublic() || post.getDays().size()==0) continue;
            Long userId=userDetails.getUser().getId();
            //로그인 유저가 찜한 것인지 여부 확인
            post.setIslike(userLikeTrueOrNot(userId, post.getPostId()));
            //게시물의 reviewCnt 계산
            int reviewCnt=reviewRepository.countByPostId(post.getPostId()).intValue();
            PlanResponseDto themeAndSearchDto =new PlanResponseDto(post.getPostId(), post.getPostImgUrl(),post.getPostTitle(),
                    post.getStartDate(), post.getEndDate(), post.getLocation(),post.getTheme(), post.isIslike(), post.getLikeCnt(), reviewCnt, post.getUser());
            searchList.add(themeAndSearchDto);
        }
        System.out.println(pageable.getSort());
        return new PageImpl<>(searchList, pageable, searchResults.getTotalElements());

    }

//--------------------------------------------------------------------------------------
    // 내가 작성한 플랜조회
    public Page<?> getMyPosts(int pageno, UserDetailsImpl userDetails) {

        // 유저가 작성한 글 조회
        List<Post> posts = postRepository.findAllByUserOrderByCreatedAtDesc(userDetails.getUser());

        Pageable pageable = getPageableList5(pageno);

        List<PostListDto> myplanList = new ArrayList<>();

        Long userId=userDetails.getUser().getId();

        for (Post post : posts) {

            post.setIslike(userLikeTrueOrNot(userId,post.getPostId()));
            int reviewCnt=reviewRepository.countByPostId(post.getPostId()).intValue();

            if(post.getDays().size()==0) continue;
            PostListDto postListDto = new PostListDto(post.getPostId(), post.getPostImgUrl(),
                    post.getPostTitle(), post.getStartDate(), post.getEndDate(),
                    post.getLocation(), post.getTheme(),post.isIslike(), post.isIspublic(),
                    post.getLikeCnt(), reviewCnt);
            myplanList.add(postListDto);

        }

        int start = pageno * 5;
        int end = Math.min((start + 5), myplanList.size());

        return paging.overPages(myplanList, start, end, pageable, pageno);
    }


    //플랜 저장 안함.(새로고침 뒤로가기)
    @Transactional
    public ResponseEntity<String> leavePost(String postUUID, User user) {
        Post post = postRepository.findByPostUUID(postUUID).orElse(null);
        if (post == null) {
            return new ResponseEntity<>("없는 게시글입니다.", HttpStatus.BAD_REQUEST);
        }
        if (!Objects.equals(post.getUser().getUserName(), user.getUserName())) {
            return new ResponseEntity<>("없는 사용자이거나 다른 사용자의 게시글입니다.", HttpStatus.BAD_REQUEST);
        }
        postRepository.deleteByPostUUID(postUUID);
        return new ResponseEntity<>("삭제 완료.",HttpStatus.OK);
    }

    // 여행 게시물 삭제
    @Transactional
    public Long deletePost(UserDetailsImpl userDetails, Long postId) {
        Post post=postRepository.findById(postId).orElseThrow(
                ()->new IllegalArgumentException("해당 게시물이 없으므로 삭제할 수 없습니다")
        );
        if(!post.getUser().getId().equals(userDetails.getUser().getId())){
            throw new IllegalArgumentException("게시물 작성자만 삭제가 가능합니다");
        }
        reviewRepository.deleteAllByPostId(postId);
        likeRepository.deleteAllByPostId(postId);
        postRepository.deleteById(postId);
        return postId;
    }


}
