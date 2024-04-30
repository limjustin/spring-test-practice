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
        // given
        String userName = "Jaeyoung";
        String userNickname = "Jayce";
        User user = createUser(userName, userNickname);
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);

        // when
        User joinedUser = userService.join(user);

        // then
        assertEquals(joinedUser.getName(), userName);
    }

    @Test
    @DisplayName("사용자 등록 : 예외 - 닉네임 길이 제한 초과")
    void givenUserWithLongNickname_whenJoinUser_thenThrowException() {
        // given
        String userName = "Jaeyoung";
        String userNickname = "JayceJayceJayceJayceJayceJayceJayce";
        User user = createUser(userName, userNickname);

        // then
        assertThrows(IllegalArgumentException.class, () -> userService.join(user));
    }

    private User createUser(String name, String nickname) {
        return User.builder()
                .name(name)
                .nickname(nickname)
                .build();
    }
}