package com.sparta.finalprojectback.member;

import com.sparta.finalprojectback.jwt.JwtTokenProvider;
import com.sparta.finalprojectback.statuscode.ResponseMessage;
import com.sparta.finalprojectback.statuscode.StatusCode;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @Override
    public Long join(MemberJoinRequestDto requestDto) {
        return memberRepository.save(Member.builder()
                .username(requestDto.getUsername())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .nickname(requestDto.getNickname())
                .email(requestDto.getEmail())
                .roles(Collections.singletonList("ROLE_USER"))
                .build()).getId();
    }

    @Override
    public String login(MemberLoginRequestDto requestDto) {
        System.out.println(requestDto.getPassword());
        System.out.println(requestDto.getUsername());
        Member member = memberRepository.findByUsername(requestDto.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("가입되지 않은 아이디 입니다.")
        );
        if (!passwordEncoder.matches(requestDto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }
        return jwtTokenProvider.createToken(member.getUsername(), member.getRoles());
    }

    @Override
    public MemberResponseDto myInfo(Member member) {
        return MemberResponseDto.builder()
                .id(member.getId())
                .username(member.getUsername())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .createdAt(member.getCreatedAt())
                .image(member.getImage())
                .build();
    }

    @Override
    public ResponseEntity<List<MemberResponseDto>> findUser() {
        List<Member> members = memberRepository.findAll();
        List<MemberResponseDto> memberResponseDtoList = new ArrayList<>();
        for (Member member : members){
            memberResponseDtoList.add(MemberResponseDto.builder()
                    .id(member.getId())
                    .username(member.getUsername())
                    .email(member.getEmail())
                    .nickname(member.getNickname())
                    .createdAt(member.getCreatedAt())
                    .build()
            );
        }
        return new ResponseEntity<>(memberResponseDtoList, HttpStatus.valueOf(StatusCode.OK));
    }
    @Override
    public ResponseEntity<String> deleteUser(Member member, Long memberId) {
        memberRepository.deleteById(memberId);
        return new ResponseEntity<>(ResponseMessage.DELETE_USER, HttpStatus.valueOf(StatusCode.OK));
    }
    @SneakyThrows
    @Override
    public ResponseEntity<String> findOverlapUsername(String username) {
        Member member = memberRepository.findByUsername(username).orElse(new Member());
        if (username.equals(member.getUsername())) {
            return new ResponseEntity<>(ResponseMessage.READ_FIND_USERNAME, HttpStatus.valueOf(StatusCode.OK));
        }
        return new ResponseEntity<>(HttpStatus.valueOf(StatusCode.OK));
    }
    @SneakyThrows
    @Override
    public ResponseEntity<String> findOverlapNickname(String nickname) {
        Member member = memberRepository.findByNickname(nickname).orElse(new Member());
        if (nickname.equals(member.getNickname())) {
            return new ResponseEntity<>(ResponseMessage.READ_FIND_NICKNAME, HttpStatus.valueOf(StatusCode.OK));
        }
        return new ResponseEntity<>(HttpStatus.valueOf(StatusCode.OK));
    }
    @SneakyThrows
    @Override
    public ResponseEntity<String> findOverlapEmail(String email) {
        Member member = memberRepository.findByEmail(email).orElse(new Member());
        if (email.equals(member.getEmail())) {
            return new ResponseEntity<>(ResponseMessage.READ_FIND_EMAIL, HttpStatus.valueOf(StatusCode.OK));
        }
        return new ResponseEntity<>(HttpStatus.valueOf(StatusCode.OK));
    }
    @SneakyThrows
    @Override
    @Transactional
    public ResponseEntity<String> modifyUser(Member member, MemberUpdateRequestDto requestDto){
        Member memberModify = memberRepository.findById(member.getId()).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 유저입니다.")
        );
        memberModify.updateUser(requestDto);
        return new ResponseEntity<>(ResponseMessage.UPDATE_USER, HttpStatus.valueOf(StatusCode.OK));
    }
}