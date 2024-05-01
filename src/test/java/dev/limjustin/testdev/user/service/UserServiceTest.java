package dev.limjustin.testdev.user.service;

import dev.limjustin.testdev.user.domain.User;
import dev.limjustin.testdev.user.domain.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    /**
     * 테스트 케이스 정리
     * [v] 사용자 등록 : 정상
     * [v] 사용자 등록 : 예외 - 닉네임 길이 제한 초과
     */

    @Test
    @DisplayName("사용자 등록 : 정상")
    void givenUser_whenJoinUser_thenReturnUser() {
        // given (사용자 정의 후, 서비스 코드 메서드 내에 필요한 stub 정의)
        String userName = "Jaeyoung";
        String userNickname = "Jayce";
        User user = createUser(userName, userNickname);  // user 객체를 생성하는 private 메서드 정의

        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);  // userService 클래스의 join 메서드 안에 있는 동작 정의 (= stub)

        // when (새로운 사용자 가입하였을 때, 이를 반환 값으로 받음)
        User joinedUser = userService.join(user);  // joinedUser 객체는 위에서 정의한 user 객체와 똑같을 것이다!

        // then (반환 값으로 받은 결과물과, 테스트 코드에서 정의한 객체와 동일한지 확인)
        assertEquals(joinedUser, user);            // 궁금하면 직접 해보면 되지!
        assertEquals(joinedUser.getName(), userName);
    }

    @Test
    @DisplayName("사용자 등록 : 예외 - 닉네임 길이 제한 초과")
    void givenUserWithLongNickname_whenJoinUser_thenThrowException() {
        // given (닉네임 길이 다르게 사용자 정의)
        String userName = "Jaeyoung";
        String userNickname = "JayceJayceJayceJayceJayceJayceJayce";  // 닉네임 길이 다르게 정의
        User user = createUser(userName, userNickname);

        // 여기서는 Mockito 정의 해줄 필요가 없다! 정의 해주면 Exception 발생함!
        // join 메서드 가보면, 닉네임 길이 제한을 초과할 경우 userRepository 접근하기 전에 이미 예외를 던져버림
        // Mockito 철학 중, 테스트 코드에서 사용되지 않는 stub 없어야 함! 그렇지 않다면 아래와 같은 예외 발생
        // org.mockito.exceptions.misusing.UnnecessaryStubbingException: Unnecessary stubbings detected.

        // then (닉네임 최대 길이 초과하였으므로 예외 발생)
        assertThrows(IllegalArgumentException.class, () -> userService.join(user));  // 예외 처리 테스트는 assertThrows 활용
    }

    private User createUser(String name, String nickname) {
        return User.builder()
                .name(name)
                .nickname(nickname)
                .build();
    }
}