package com.szj.demo.service;

import com.szj.demo.model.AuthenticationResponse;
import com.szj.demo.model.Token;
import com.szj.demo.model.User;
import com.szj.demo.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * The UserService class is responsible for managing user-related operations such as registration, login, and access control.
 */

@Service
@RequiredArgsConstructor
public class UserService {
    @Value("${SECRET_KEY}")
    private String  SECRET_KEY;
    private final UserRepository userRepository;
    private final HttpServletRequest request;
    private final Map<User, String> activeTokens = new HashMap<>();

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registers a new user with the given username and password.
     *
     * @param username The username of the user to be registered.
     * @param password The password of the user to be registered.
     * @return The registered User object.
     * @throws IllegalStateException If the username already exists.
     * @throws NoSuchElementException If an error occurs while saving to the user repository.
     */

    public User register(String username, String password) throws IllegalStateException, NoSuchElementException {
        if(userRepository.findUserByUsername(username).isPresent()) {
            throw new IllegalStateException("The following username already exits: " + username);
        }

        /*Encode password...*/

        User user = new User(username, password);
        userRepository.save(user);

        if(userRepository.findUserByUsername(username).isEmpty()){
            throw new NoSuchElementException("Exception while saving to user repositroy!");
        }
        return user;
    }

    /**
     * Authenticates the user by checking their username and password. If successful, generates a token for the user
     * and adds it to the active tokens map.
     *
     * @param user The User object containing the username and password for authentication.
     * @return The generated token for the authenticated user.
     * @throws IllegalAccessException If the username does not exist or the password is incorrect.
     */

   public String login(User user) throws NoSuchElementException, IllegalAccessException {
       Optional<User> foundUser = userRepository.findUserByUsername(user.getUsername()) ;
       if (foundUser.isEmpty()){
           throw new NoSuchElementException("Username does not exits!");
       }

       if(!foundUser.get().getPassword().equals(user.getPassword())) {
           throw new IllegalAccessException("Incorrect username or password!");
       }

       activeTokens.remove(foundUser.get());

       String token = generateToken(foundUser.get());
       activeTokens.put(foundUser.get(), token);

       return token;
   }

    public List<User> findAllUser() {
        return userRepository.findAll();
    }

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

    public void deleteUser(Long id) throws Exception {
        User user = userRepository.findById(id).orElseThrow(() -> new Exception("User not exists"));

        userRepository.delete(user);
    }

    public org.springframework.security.core.userdetails.User currentUser() {
        return null;
    }

    private String generateToken(User user) {
       Map<String, Object> userClaims = new HashMap<>();
       Map<String, Object> userDetails = new HashMap<>();
       userDetails.put("Password",user.getPassword());
       userDetails.put("AccessLevel",user.getAccessLevel());
       userClaims.put(user.getUsername(), userDetails);

       return Jwts
               .builder()
               .setClaims(userClaims)
               .setSubject(user.getUsername())
               .setIssuedAt(new Date(System.currentTimeMillis()))
               .setExpiration(new Date(System.currentTimeMillis() + 3500000))// 1 hour
               .signWith(getSigningKey(), SignatureAlgorithm.HS256)
               .compact();
   }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
