package com.szj.demo.service;

import com.szj.demo.enums.AuthenticationLevel;
import com.szj.demo.exception.InvalidTokenException;
import com.szj.demo.model.Address;
import com.szj.demo.model.ApiResponse;
import com.szj.demo.model.UpdateBalanceRequest;
import com.szj.demo.model.User;
import com.szj.demo.repository.AddressRepository;
import com.szj.demo.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Key;
import java.util.*;
import java.util.function.Function;

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
    private final AddressRepository addressRepository;

    @Transactional
    public void updateUserBalance(User user, UpdateBalanceRequest updateBalanceRequest) {
        Optional<User> optUser = userRepository.findUserByUsername(user.getUsername());
        if(optUser.isEmpty()){
            throw new IllegalArgumentException("User does not exists in repository");
        }
        validateCardDetails(updateBalanceRequest);
        User updatedUser = optUser.get();
        updatedUser.setBalance(user.getBalance() + updateBalanceRequest.getNewBalance());
        userRepository.save(updatedUser);
    }

    public Double getBalance(Long userId) {
        Optional<User> optUser = userRepository.findUserById(userId);
        if(optUser.isEmpty()){
            throw new IllegalArgumentException("User does not exists in repository");
        }
        return optUser.get().getBalance();
    }

    public void createAddress(User user, Address newAddress){
        Optional<User> optUser = userRepository.findUserByUsername(user.getUsername());
        if (optUser.isEmpty()) {
            throw new IllegalArgumentException("User does not exist in repository");
        }

        User updatedUser = optUser.get();

        // Check if the new address already exists
        Optional<Address> existingAddress = addressRepository.findByDetails(
                newAddress.getCountry(),
                newAddress.getCity(),
                newAddress.getStreet(),
                newAddress.getZipCode()
        );

        Address addressToUse;
        if (existingAddress.isPresent()) {
            // If address exists, use it
            addressToUse = existingAddress.get();
        } else {
            // If address doesn't exist, save the new address
            addressToUse = addressRepository.save(newAddress);
        }

        // Add or update the address in the user's address list
        // Check if the address already exists in the user's list
        if (!updatedUser.getAddresses().contains(addressToUse)) {
            updatedUser.getAddresses().add(addressToUse);
        } else {
            // Optionally update existing address details if needed
            // You can modify this section to update specific fields if necessary
        }

        // Save the updated user
        userRepository.save(updatedUser);

        // Optionally, ensure the address's user reference is set correctly
        // This is typically handled automatically by JPA/Hibernate
        addressToUse.setUser(updatedUser);
        addressRepository.save(addressToUse);
    }

    private void validateCardDetails(UpdateBalanceRequest updateBalanceRequest) {

        String cardNumberSize = updateBalanceRequest.getCardNumber().replace("-","");

        if(cardNumberSize.length() < 13 || cardNumberSize.length() > 16){
            throw new IllegalArgumentException("Card number length should be between 13 and 16");
        }

        if(!updateBalanceRequest.getExpirationDate().matches("(?:0[1-9]|1[0-2])/[0-9]{2}")){
            throw new IllegalArgumentException("Expiration date should be in the format MM/yy");
        }

        if (!updateBalanceRequest.getCvv().matches("\\d{3,4}")) {
            throw new IllegalArgumentException("Invalid CVV");
        }
    }

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

        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(username, encodedPassword);
        userRepository.save(user);

        if(userRepository.findUserByUsername(username).isEmpty()){
            throw new NoSuchElementException("Exception while saving to user repository!");
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
        Optional<User> foundUser = userRepository.findUserByUsername(user.getUsername());
        if(foundUser.isEmpty()) throw new NoSuchElementException("Username does not exist!");

        //Proper password decoding and matching...
        if(!passwordEncoder.matches(user.getPassword(), foundUser.get().getPassword())) throw new IllegalAccessException("Incorrect username or password!");

        // Remove old token if by some miracle it wasn't cleaned up.
        activeTokens.remove(foundUser.get());

        String token = generateToken(foundUser.get());
        activeTokens.put(foundUser.get(), token);

        return token;
    }

    public List<Address> getAddress(Long userId) {
        List<Address> addresses = addressRepository.findAddressesByUserId(userId);
        if (addresses.isEmpty()) {
            throw new NoSuchElementException("No addresses found for user ID: " + userId);
        }

        return addresses;
    }
    /**
     * Logs out the current user by removing their active token.
     *
     * @throws InvalidTokenException if the user has no active token or the token has expired
     */

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
        String authorizationHeader = request.getHeader("Authorization");
        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")){
           throw new InvalidTokenException("Authorization header is missing or invalid!");
        }

        String token = authorizationHeader.substring("Bearer ".length());

        String potentialUser = extractClaim(token, Claims::getSubject);
        Optional<User> optionalUser = userRepository.findUserByUsername(potentialUser);

        if(optionalUser.isEmpty()) throw new InvalidTokenException("User not found for token!");

        User user = optionalUser.get();

        if(!activeTokens.containsKey(user)){
            throw new InvalidTokenException("User has no active token associated!");
        }

        if(isTokenExpired(activeTokens.get(user))){
            throw new InvalidTokenException("Token expired");
        }

        return user;
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
       userDetails.put("UserId", user.getId());
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
