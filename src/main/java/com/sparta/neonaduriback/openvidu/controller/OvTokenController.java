//package com.sparta.neonaduriback.openvidu.controller;
//
//import com.sparta.neonaduriback.login.repository.UserRepository;
//import com.sparta.neonaduriback.openvidu.AudioChatRole;
//import com.sparta.neonaduriback.openvidu.dto.AudioChatEntryDto;
//import com.sparta.neonaduriback.openvidu.dto.AudioChatLeaveDto;
//import com.sparta.neonaduriback.security.UserDetailsImpl;
//import io.openvidu.java.client.*;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Slf4j
//@RestController
//@RequestMapping("/auth/api/openvidu")
//
////@CrossOrigin(allowedHeaders = "*", exposedHeaders = {"*"},origins = {"https://localhost:3000","http://localhost:3000",
////                                              "https://127.0.0.1:3000", "http://127.0.0.1:3000"},allowCredentials ="true")
//public class OvTokenController {
//
//    private final UserRepository userRepository;
//
//    private OpenVidu openVidu;
//    private Map<Long, Session> mapSessions = new ConcurrentHashMap<>();
//    private Map<Long, Map<String, OpenViduRole>> mapSessionNamesTokens = new ConcurrentHashMap<>();
//
//    private String OPENVIDU_URL;
//    private String SECRET;
//
//    @Autowired
//    public OvTokenController(@Value("${openvidu.secret}") String secret, @Value("${openvidu.url}") String openviduUrl
//                            , UserRepository userRepository) {
//        this.SECRET = secret;
//        this.OPENVIDU_URL = openviduUrl;
//        this.openVidu = new OpenVidu(OPENVIDU_URL, SECRET);
//        this.userRepository = userRepository;
//    }
//
//    @PostMapping(value = "/getToken")
//    public ResponseEntity<Object> getToken(@RequestHeader Map<String, String> header, @RequestBody AudioChatEntryDto chatEntryDto,
//                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
//
//
//        //이부분.. 방을 만들어주기 위한 공간이라고 생각해, 그럼 우리 프로젝트 api/makeplan과 뭐가 달라?
//        Long postId = chatEntryDto.getPostId(); // 참여요청한 멤버가 들어가려는 방 고유번호
//
//        OpenViduRole role = setOvRole(chatEntryDto);
//
//        String serverData = chatEntryDto.getNickName();
//
//        // role 과 optional 한 serverData 와 함께 WEBRTC 타입으로 connectionProperties 를 생성
//        ConnectionProperties connectionProperties = new ConnectionProperties.Builder().type(ConnectionType.WEBRTC)
//                .role(role).data(serverData).build();
//
//        // 이미 생성된 음성채팅방에 대한 참여 요청일 경우
//        if (this.mapSessions.get(postId) != null) {
//            log.info("이미 존재하는 room 에 대한 참여요청입니다. postId = {}", postId);
//
//            try {
//                return joinExistingRoom(chatEntryDto, postId, role, connectionProperties, userDetails);
//
//            } catch (Exception e) {
//                log.error("기존 방에 참여를 요청했으나 exception 발생. errorMessage = {}", e.getMessage());
//                return ResponseEntity.badRequest().body(e.getMessage());
//            }
//        } else {
//            // 새로 음성채팅방을 개설하는 경우
//            try {
//                return createNewSession(chatEntryDto, postId, role, connectionProperties, userDetails);
//            } catch (Exception e) {
//                log.error("새로운 방 개설을 요청했으나 exception 발생. errorMessage = {}", e.getMessage());
//                log.error("새로운 방 개설을 요청했으나 exception 발생. e = {}", e);
//                return ResponseEntity.badRequest().body(e.getMessage());
//            }
//        }
//    }
//
//    private ResponseEntity<Object> joinExistingRoom(AudioChatEntryDto chatEntryDto, Long postId, OpenViduRole role, ConnectionProperties connectionProperties,
//                                                    UserDetailsImpl userDetails) throws OpenViduJavaClientException, OpenViduHttpException {
//        int nowParticipants = this.mapSessionNamesTokens.get(postId).size();
//        log.info("{}번 room에 대해 발급된 유효한 token 개수는 {}", postId, this.mapSessionNamesTokens.get(postId).size());
//
//        Long maxParticipants = chatEntryDto.getParticipantCount();
//
//        if (maxParticipants <= nowParticipants) {
//            log.info("{}번 room에 대해 수용가능 인원이 이미 찼어요. 현재 인원:{}, 최대 인원:{}", postId, nowParticipants, maxParticipants);
//            return ResponseEntity.badRequest().body("수용가능 인원이 이미 찼어요.");
//        }
//
//        // 방금 막 생성한 connectionProperties 를 기반으로 token 만들기
//        String token = this.mapSessions.get(postId).createConnection(connectionProperties).getToken();
//        log.info("token {}", token);
//
//        // token 을 키로 하고 role 을 값으로 갖는 map 객체를 현재 postId(=sessionName)를 키로 갖는 더 상위 map 객체의 값으로 넣음.
//        this.mapSessionNamesTokens.get(postId).put(token, role);
//        //SUBSCRIBER -> 디폴트 이미지
//        String profileImgUrl="https://seunghodev-bucket.s3.ap-northeast-2.amazonaws.com/default/Group+1.png";
//        //getStringStringMap- 받아온 인자값를 map에 담아 프론트에게 보내주는 메소드
//        Map<String, String> map = getStringStringMap(chatEntryDto, postId, role, token, "참여요청 성공", profileImgUrl);
//        return ResponseEntity.ok().body(map);
//    }
//
//    private ResponseEntity<Object> createNewSession(AudioChatEntryDto chatEntryDto, Long postId, OpenViduRole role, ConnectionProperties connectionProperties,
//                                                    UserDetailsImpl userDetails) throws OpenViduJavaClientException, OpenViduHttpException {
//        log.info("새로운 room 개설 요청입니다. postId = {}", postId);
//
//        if (chatEntryDto.getRole() != AudioChatRole.MODERATOR) {
//            log.error("방장만이 방 개설 요청을 할 수 있습니다. 현재 role: {}", chatEntryDto.getRole());
//            String message = "방장만이 방 개설 요청을 할 수 있습니다. 현재 role: " + chatEntryDto.getRole();
//            throw new IllegalArgumentException(message);
//        }
//
//        // 새 openVidu session 을 만들기
//        Session session = this.openVidu.createSession();
//
//        // 방금 막 생성한 connectionProperties 를 기반으로 token 만들기
//        String token = session.createConnection(connectionProperties).getToken();
//
//        // 새로 개설된 방을 등록
//        this.mapSessions.put(postId, session);
//        // 이 방에 대해 지금 참여자의 정보(token(key) 과 role(val))를 담기
//        this.mapSessionNamesTokens.put(postId, new ConcurrentHashMap<>());
//        this.mapSessionNamesTokens.get(postId).put(token, role);
//
//        String profileImgUrl=userDetails.getProfileImgUrl();
//        // 개설요청 성공한 postId, 요청한 memberName, 그리고 프론트에서 openvidu session connect 에 사용할 token 을 response 로 보내기
//        Map<String, String> map = getStringStringMap(chatEntryDto, postId, role, token, "개설요청 성공", profileImgUrl);
//        return ResponseEntity.ok().body(map);
//    }
//
//    private OpenViduRole setOvRole(AudioChatEntryDto chatEntryDto) {
//        OpenViduRole role = OpenViduRole.SUBSCRIBER;
//        AudioChatRole reqRole = chatEntryDto.getRole();
//        if (reqRole == AudioChatRole.MODERATOR) {
//            role = OpenViduRole.MODERATOR;
//        } else if (reqRole == AudioChatRole.PUBLISHER) {
//            role = OpenViduRole.PUBLISHER;
//        }
//        return role;
//    }
//
//    //받아온 인자값를 map에 담아 프론트에게 보내주는 메소드
//    private Map<String, String> getStringStringMap(AudioChatEntryDto chatMember, Long postId, OpenViduRole role, String token,
//                                                   String message, String profileImgUrl) {
//        Map<String, String> map = new HashMap<>();
//        map.put("postId", postId.toString());
//        map.put("memberName", chatMember.getNickName());
//        map.put("profileImgUrl", profileImgUrl);
//        map.put("token", token);
//        map.put("role", role.toString());
//        map.put("etc", message);
//        return map;
//    }
//
//    @PostMapping(value = "/deleteToken")
//    public ResponseEntity<Object> leaveRoom(@RequestBody AudioChatLeaveDto chatLeaveDto) {
//
//        log.info("채팅방 퇴장 요청입니다.", chatLeaveDto);
//
//        Long postId = chatLeaveDto.getPostId();
//        String token = chatLeaveDto.getToken();
//        String memberName = chatLeaveDto.getMemberName();
//
//        // 존재하는 방에 대한 퇴장 요청인 경우
//        if (this.mapSessions.get(postId) != null && this.mapSessionNamesTokens.get(postId) != null) {
//
//            // 유효한 토큰이었고, 삭제작업이 진행됨.
//            if (this.mapSessionNamesTokens.get(postId).remove(token) != null) {
//                // 해당 멤버 퇴장 성공
//
//                log.info("{}님이 room {}에서 퇴장 성공!", memberName, postId);
//                // 그런데 이 postId에 대한 토큰이 전혀 없다면 ( 해당 음성채팅방 참여자가 남지 않았다면 )
//                if (this.mapSessionNamesTokens.get(postId).isEmpty()) {
//                    // postId 로 열린 session 삭제
//                    this.mapSessions.remove(postId);
//                    log.info("더 이상 남아있는 사람이 없어요. 채팅방 {}도 삭제됩니다.", postId);
//                }
//                // 또는 나가려는 사람이 방장이라면 ?
//                if (chatLeaveDto.getRole().equals(OpenViduRole.MODERATOR.toString())) {
//                    // postId 로 열린 session 삭제
//                    this.mapSessions.remove(postId);
//                    log.info("방장이 퇴장했으므로 채팅방 {}도 삭제됩니다.", postId);
//                }
//                String message = postId + "에 대한 퇴장 요청 성공, 퇴장한 memberName: " + memberName;
//                return ResponseEntity.ok().body(message);
//            } else {
//                // 유효한 토큰이 아닌 경우
//                log.info("유효하지 않은 토큰입니다! 제출한 토큰은 {}", token);
//                String message = "유효하지 않은 토큰입니다! 제출한 토큰: " + token;
//                throw new IllegalArgumentException(message);
//            }
//        } else {
//            log.info("존재하지 않는 방에 대한 퇴장 요청입니다.", postId);
//            String message = "존재하지 않는 방에 대한 퇴장 요청입니다.";
//            throw new IllegalArgumentException(message);
//        }
//    }
//
//}
