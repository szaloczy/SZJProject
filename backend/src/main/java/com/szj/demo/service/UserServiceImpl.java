package com.szj.demo.service;

import com.szj.demo.model.AuthenticationResponse;
import com.szj.demo.model.Token;
import com.szj.demo.model.User;
import com.szj.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<User> findAllUser() {
        return userRepository.findAll();
    }

    @Override
    public User createUser(User user) {
        if (user.getUsername().length() < 3
                || user.getPassword().length() < 3) {
            throw new IllegalStateException("Must be at least 3 character");
        }
        if (!Pattern.matches("^[a-zA-Z0-9]+$", user.getUsername())) {
            throw new IllegalStateException("Data holds illegal character");
        }

        userRepository.findUserByUsername(user.getUsername())
                .ifPresentOrElse(u -> {
                            throw new IllegalStateException(u.getUsername() + " already exists");
                        }, () -> {
                            user.setPassword(passwordEncoder.encode(user.getPassword()));
                            userRepository.save(user);
                        }
                );
        return user;
    }

    @Override
    public void deleteUser(Long id) throws Exception {
        User user = userRepository.findById(id).orElseThrow(() -> new Exception("User not exists"));

        userRepository.delete(user);
    }

    @Override
    public AuthenticationResponse login(User user) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                user.getPassword()
        ));

        Optional<User> searchUser = Optional.ofNullable(userRepository.findUserByUsername(user.getUsername())
                .orElseThrow(IllegalStateException::new));
        String jwtToken = jwtService.generateToken(user);
        if (searchUser.isPresent()) {
            tokenService.revokeUserTokens(searchUser.get().getUsername());
            tokenService.storeToken(new Token(searchUser.get().getUsername(), jwtToken, false));
        }
        return new AuthenticationResponse(jwtToken);
    }
}
