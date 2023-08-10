package com.leka.teashop.controller;


import com.leka.teashop.event.RegistrationEmailEvent;
import com.leka.teashop.model.User;
import com.leka.teashop.model.dto.UserDto;
import com.leka.teashop.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Builds presentation of registration form
     * @param model model to insert in the form
     * @return the file name of *.html
     */
    @GetMapping("/register")
    public String getRegistration(Model model) {
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);
        return "registration";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") UserDto userDto,
                           BindingResult result, Model model,
                           HttpServletRequest httpServletRequest) throws Exception {
        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "registration";
        }
        String passwordConfirm = httpServletRequest.getParameter("retypePass");
        if (!userDto.getPassword().equals(passwordConfirm)) {
            model.addAttribute("passwordError", "Passwords don't coincide");
            return "registration";
        }
        userService.register(userDto, httpServletRequest);
        return "redirect:/register?success";
    }

    @GetMapping("/verifyEmail")
    public String verifyEmail(@RequestParam("token") String verificationToken){
        return userService.verifyEmailByToken(verificationToken);
    }

    @PostMapping("/renew")
    public String renewVerificationLink(@Valid @ModelAttribute("user") UserDto userDto,
                                        HttpServletRequest httpServletRequest){
        return userService.renewVerificationLink(userDto.getEmail(), httpServletRequest);
    }

    @GetMapping("/renew")
    public String renewFormPage(Model model){
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);
        return "renew";
    }

    @GetMapping("/login")
    public String getLogin() {
        return "login";
    }
}
