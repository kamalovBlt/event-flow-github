    package com.technokratos.service;

    import com.technokratos.exception.UserNotFoundException;
    import com.technokratos.model.AuthProvider;
    import com.technokratos.model.Role;
    import com.technokratos.model.User;
    import com.technokratos.service.interfaces.UserService;
    import org.junit.jupiter.api.Test;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.context.SpringBootTest;
    import org.springframework.test.context.ActiveProfiles;
    import org.springframework.transaction.annotation.Transactional;

    import java.util.ArrayList;
    import java.util.List;

    import static org.assertj.core.api.Assertions.*;

    @SpringBootTest
    @ActiveProfiles(profiles = "test")
    @Transactional
    public class UserServiceTest{

        @Autowired
        private UserService userService;

        @Test
        void shouldSaveAndFindById() {
            List<Role> roles = new ArrayList<>();
            roles.add(Role.USER);
            User user = User.builder()
                    .password("password")
                    .email("test@example.com")
                    .firstName("Save")
                    .roles(roles)
                    .authProvider(AuthProvider.GOOGLE)
                    .deleted(false)
                    .build();

            Long id = userService.save(user);
            User result = userService.findById(id);

            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo("test@example.com");
        }

        @Test
        void shouldFindAllNonDeletedUsers() {
            List<Role> roles = new ArrayList<>();
            roles.add(Role.USER);
            userService.save(User.builder()
                    .password("password1")
                    .email("test1@example.com")
                    .firstName("u1")
                    .roles(roles)
                    .authProvider(AuthProvider.GOOGLE)
                    .deleted(false)
                    .build());

            userService.save(User.builder()
                    .password("password2")
                    .email("test2@example.com")
                    .firstName("u2")
                    .roles(roles)
                    .authProvider(AuthProvider.GOOGLE)
                    .deleted(true)
                    .build());

            List<User> users = userService.findAll();

            assertThat(users).extracting(User::getEmail).contains("test1@example.com");
            assertThat(users).extracting(User::getEmail).doesNotContain("test2@example.com");
        }

        @Test
        void shouldUpdateExistingUser() {
            List<Role> roles = new ArrayList<>();
            roles.add(Role.USER);
            User user = User.builder()
                    .password("password")
                    .email("before@example.com")
                    .firstName("u")
                    .roles(roles)
                    .authProvider(AuthProvider.GOOGLE)
                    .deleted(false)
                    .build();

            userService.save(user);

            user.setEmail("after@example.com");

            userService.update(user);

            User updated = userService.findById(user.getId());
            assertThat(updated.getEmail()).isEqualTo("after@example.com");
        }

        @Test
        void shouldThrowWhenUserDoesNotExist() {
            List<Role> roles = new ArrayList<>();
            roles.add(Role.USER);
            User fake = User.builder()
                    .id(999L)
                    .password("password1")
                    .firstName("u")
                    .roles(roles)
                    .authProvider(AuthProvider.GOOGLE)
                    .email("test1@example.com")
                    .build();

            assertThatThrownBy(() -> userService.update(fake))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        void shouldSoftDeleteUserAndThrowUserNotFoundException() {
            List<Role> roles = new ArrayList<>();
            roles.add(Role.USER);
            User user = User.builder()
                    .password("password1")
                    .email("test1@example.com")
                    .firstName("u")
                    .roles(roles)
                    .authProvider(AuthProvider.GOOGLE)
                    .deleted(false)
                    .build();

            Long id = userService.save(user);

            userService.delete(id);

            assertThatThrownBy(() -> userService.findById(user.getId()))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        void shouldThrowWhenUserNotExists() {
            assertThatThrownBy(() -> userService.delete(404L))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }
