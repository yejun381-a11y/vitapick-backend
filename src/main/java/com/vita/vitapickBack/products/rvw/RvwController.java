package com.vita.vitapickBack.products.rvw;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vita.vitapickBack.users.UsersRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rvw")
public class RvwController {

    private final RvwService rvwService;

    private final UsersRepository usersRepository;

    // 관리자 권한 확인
    // JwtAuthenticationFilter에서 authorities 가 비어 있으므로
    // authentication principal 의 userNum 으로 users 테이블을 조회해서 roleCd 확인
    private boolean isAdmin(Authentication authentication) {

        if (authentication == null) {
            return false;
        }

        Long userNum = Long.valueOf(authentication.getPrincipal().toString());

        return usersRepository.findById(userNum)
                .map(user -> "ROLE_ADMIN".equals(user.getRoleCd()) || "ADMIN".equals(user.getRoleCd()))
                .orElse(false);
    }

    // 리뷰 작성
    @PostMapping
    public ResponseEntity<Rvw> createRvw(
            @AuthenticationPrincipal Long userNum,
            @RequestBody RvwDTO dto) {

        return ResponseEntity.ok(rvwService.createRvw(userNum, dto));
    }
    
    // 리뷰 작성 가능 여부 확인
    @GetMapping("/can-write/{prdId}")
    public ResponseEntity<RvwCanWriteDTO> canWriteReview(
            @AuthenticationPrincipal Long userNum,
            @PathVariable("prdId") Long prdId
    ) {
        return ResponseEntity.ok(rvwService.canWriteReview(userNum, prdId));
    }

    // 상품 ID로 리뷰 목록 조회
    @GetMapping("/prd/{prdId}")
    public ResponseEntity<List<RvwDTO>> findByPrdId(
            @PathVariable("prdId") Long prdId) {

        return ResponseEntity.ok(rvwService.findByPrdId(prdId));
    }

    // 로그인한 사용자의 리뷰 목록 조회
    @GetMapping("/user")
    public ResponseEntity<List<Rvw>> findByUserNum(
            @AuthenticationPrincipal Long userNum) {

        return ResponseEntity.ok(rvwService.findByUserNum(userNum));
    }

    // 리뷰 단건 조회
    @GetMapping("/{rvwId}")
    public ResponseEntity<Rvw> findByRvwId(
            @PathVariable("rvwId") Long rvwId) {

        return ResponseEntity.ok(rvwService.findByRvwId(rvwId));
    }

    // 리뷰 삭제
    // 실제 DB에서 리뷰 row를 삭제함
    // 관리자 답글은 rvw 테이블 안에 같이 있으므로 리뷰 삭제 시 같이 삭제됨
    @DeleteMapping("/{rvwId}")
    public ResponseEntity<Void> deleteRvw(
            @AuthenticationPrincipal Long userNum,
            @PathVariable("rvwId") Long rvwId) {

        rvwService.deleteRvw(userNum, rvwId);
        return ResponseEntity.ok().build();
    }

    // 리뷰 수정
    @PatchMapping("/{rvwId}")
    public ResponseEntity<Rvw> updateRvw(
            @AuthenticationPrincipal Long userNum,
            @PathVariable("rvwId") Long rvwId,
            @RequestBody RvwDTO dto) {

        return ResponseEntity.ok(rvwService.updateRvw(userNum, rvwId, dto));
    }

    // 관리자 리뷰 답글 등록
    @PostMapping("/{rvwId}/reply")
    public ResponseEntity<?> createRvwReply(
            @PathVariable("rvwId") Long rvwId,
            @RequestBody RvwDTO dto,
            Authentication authentication) {

        try {
            if (authentication == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 후 이용해주세요.");
            }

            if (!isAdmin(authentication)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("관리자만 이용할 수 있습니다.");
            }

            rvwService.createRvwReply(rvwId, dto.getReplyTxt());

            return ResponseEntity.status(HttpStatus.CREATED).build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("리뷰 답글 등록에 실패했습니다.");
        }
    }

    // 관리자 리뷰 답글 수정
    @PatchMapping("/{rvwId}/reply")
    public ResponseEntity<?> updateRvwReply(
            @PathVariable("rvwId") Long rvwId,
            @RequestBody RvwDTO dto,
            Authentication authentication) {

        try {
            if (authentication == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 후 이용해주세요.");
            }

            if (!isAdmin(authentication)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("관리자만 이용할 수 있습니다.");
            }

            rvwService.updateRvwReply(rvwId, dto.getReplyTxt());

            return ResponseEntity.status(HttpStatus.OK).build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("리뷰 답글 수정에 실패했습니다.");
        }
    }

    // 관리자 리뷰 답글 삭제
    // 리뷰 자체는 삭제하지 않고 replyTxt, replyAt만 비움
    @DeleteMapping("/{rvwId}/reply")
    public ResponseEntity<?> deleteRvwReply(
            @PathVariable("rvwId") Long rvwId,
            Authentication authentication) {

        try {
            if (authentication == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 후 이용해주세요.");
            }

            if (!isAdmin(authentication)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("관리자만 이용할 수 있습니다.");
            }

            rvwService.deleteRvwReply(rvwId);

            return ResponseEntity.status(HttpStatus.OK).build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("리뷰 답글 삭제에 실패했습니다.");
        }
    }
}