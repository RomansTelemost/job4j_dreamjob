package ru.job4j.dreamjob.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "users/login";
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute User user, Model model) {
        var userOptional = userService.findByEmailAndPassword(user.getEmail(), user.getPassword());
        if (userOptional.isEmpty()) {
            model.addAttribute("error", "Почта или пароль введены неверно");
            return "users/login";
        }
        return "redirect:/vacancies";
    }

    @GetMapping("/register")
    public String getRegistrationPage() {
        return "users/register";
    }

    @PostMapping("/create")
    @ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE, reason = "Моя ошибка!")
    public String register(@ModelAttribute User user, Model model) {
        var savedUser = userService.save(user);
        if (savedUser.isEmpty()) {
            model.addAttribute("message", "Пользователь с такой почтой уже существует");
            return "errors/404";
        }
        return "redirect:/users";
    }

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("users", userService.findAll());
        return "users/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(Model model, @PathVariable int id) {
        var isDeleted = userService.deleteById(id);
        if (!isDeleted) {
            model.addAttribute("message", "Ошибка удаления вакансии");
            return "errors/404";
        }
        model.addAttribute("users", userService.findAll());
        return "redirect:/users";
    }

}
