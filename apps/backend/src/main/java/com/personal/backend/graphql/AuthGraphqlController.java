package com.personal.backend.graphql;

import com.personal.backend.domain.User;
import com.personal.backend.dto.UserDto;
import com.personal.backend.service.AuthService;
import com.personal.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class AuthGraphqlController {

    private final UserService userService;
    private final AuthService authService;

    @MutationMapping
    public boolean signup(@Argument("input") UserDto.SignupRequest input) {
        userService.signup(input);
        return true;
    }

    @MutationMapping
    public UserDto.TokenResponse login(@Argument("input") UserDto.LoginRequest input) {
        return authService.login(input);
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public User me(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.findByEmail(userDetails.getUsername()).orElseThrow(
            () -> new IllegalStateException("Authenticated user not found"));
    }
}
