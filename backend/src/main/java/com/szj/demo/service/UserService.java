package com.szj.demo.service;

import com.szj.demo.enums.AuthenticationLevel;
import com.szj.demo.exception.InvalidTokenException;
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
    private String SECRET_KEY;
    private final UserRepository userRepository;
    private final HttpServletRequest request;
    private final Map<User, String> activeTokens = new HashMap<>();
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
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(username, encodedPassword);
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

   public void logout() throws InvalidTokenException {
       User user = currentUser();
       activeTokens.remove(user);
   }

    /**
     * Retrieves the currently logged-in user based on the token provided in the Authorization header of the request.
     *
     * @return The User object representing the currently logged-in user.
     * @throws InvalidTokenException If there is a problem with the token, such as it being invalid, expired, or associated with a user that does not exist or has no active token.
     */
    public User currentUser() throws InvalidTokenException {
        String token;
        if(request.getHeader("Authorization").startsWith("Bearer ")){
            token = request.getHeader("Authorization").substring("Bearer ".length());
        } else {
            throw new InvalidTokenException("Problem with token!");
        }

        String potentialUser = extractClaim(token, Claims::getSubject);
        Optional<User> user = userRepository.findUserByUsername(potentialUser);
        if(user.isEmpty()) throw new InvalidTokenException("Username does not exits!");
        if(!activeTokens.containsValue(user.get())) throw new InvalidTokenException("User has no token associated with it!");
        if(isTokenExpired(activeTokens.get(user.get()))) throw new InvalidTokenException("Token expired!");

        return user.get();
    }

    /**
     * Checks if the user with the current access level can access a certain level.
     *
     * @param level The level to check. Possible values are "PUBLIC", "PRIVATE", or "SUPER".
     * @return True if the user has access to the given level, false otherwise.
     * @throws InvalidTokenException If there is a problem with the token, such as it being invalid, expired, or associated with a user that does not exist or has no active token.
     */

    public boolean canAccess(AuthenticationLevel level) throws InvalidTokenException {
        if(level == AuthenticationLevel.PUBLIC) return true;
        User currentUser = currentUser();

        return switch (level) {
            case PRIVATE -> currentUser.getAccessLevel() == AuthenticationLevel.PRIVATE || currentUser.getAccessLevel() == AuthenticationLevel.SUPER;
            case SUPER -> currentUser.getAccessLevel() == AuthenticationLevel.SUPER;
            default -> false;
        };
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
