package ru.job4j.dreamjob.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void whenLoginUserThenSessionContainsSameUserAndRedirectToVacanciesPage() {
        User user = new User("Email1", "Name1", "Password1");
        ConcurrentModel model = new ConcurrentModel();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession httpSession = Mockito.mock(HttpSession.class);
        ArgumentCaptor<String> sessionParameter = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        Mockito.when(userService.findByEmailAndPassword(Mockito.anyString(), Mockito.anyString())).thenReturn(Optional.of(user));
        Mockito.when(request.getSession()).thenReturn(httpSession);
        Mockito.doNothing().when(httpSession).setAttribute(sessionParameter.capture(), userArgumentCaptor.capture());

        String view = userController.loginUser(user, request, model);

        Assertions.assertThat(view).isEqualTo("redirect:/vacancies");
        Assertions.assertThat(sessionParameter.getValue()).isEqualTo("user");
        Assertions.assertThat(userArgumentCaptor.getValue()).isEqualTo(user);
    }

    @Test
    public void whenRegisterNewUserThenSessionContainsSameUserAndRedirectToVacanciesPage() {
        User user = new User("Email1", "Name1", "Password1");
        ConcurrentModel model = new ConcurrentModel();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession httpSession = Mockito.mock(HttpSession.class);
        ArgumentCaptor<String> sessionParameter = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        Mockito.when(request.getSession()).thenReturn(httpSession);
        Mockito.when(userService.save(user)).thenReturn(Optional.of(user));
        Mockito.doNothing().when(httpSession).setAttribute(sessionParameter.capture(), userArgumentCaptor.capture());

        String view = userController.register(user, request, model);

        Assertions.assertThat(view).isEqualTo("redirect:/vacancies");
        Assertions.assertThat(sessionParameter.getValue()).isEqualTo("user");
        Assertions.assertThat(userArgumentCaptor.getValue()).isEqualTo(user);
    }

    @Test
    public void whenRequestUsersListPageThenGetPageWithUsers() {
        User user1 = new User("Email1", "Name1", "Password1");
        User user2 = new User("Email2", "Name2", "Password2");
        var expectedUsers = List.of(user1, user2);
        ConcurrentModel model = new ConcurrentModel();

        when(userService.findAll()).thenReturn(expectedUsers);

        String view = userController.getAll(model);
        assertThat(view).isEqualTo("users/list");
        var actualUsers = model.getAttribute("users");
        assertThat(actualUsers).isEqualTo(expectedUsers);
    }

    @Test
    public void whenRequestDeleteUserThenRedirectToPageWithUsers() {
        User user1 = new User("Email1", "Name1", "Password1");
        User user2 = new User("Email2", "Name2", "Password2");
        var expectedUsers = List.of(user1, user2);
        ConcurrentModel model = new ConcurrentModel();

        when(userService.deleteById(anyInt())).thenReturn(true);
        when(userService.findAll()).thenReturn(expectedUsers);

        String view = userController.delete(anyInt(), model);
        assertThat(view).isEqualTo("redirect:/users");
        var actualUsers = model.getAttribute("users");
        assertThat(actualUsers).isEqualTo(expectedUsers);
    }
}