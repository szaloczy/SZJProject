package com.szj.demo.service;

import com.szj.demo.model.Token;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@Scope("application")
public class TokenService {

    List<Token> tokens = new LinkedList<>();

    public void storeToken(Token token){
        tokens.add(token);
    }

    public void revokeUserTokens(String username){
        tokens.forEach(
                token -> {
                    if (token.getUsername().equals(username)){
                        token.setRevoked(true);
                    }
                }
        );
    }

    public Token getToken(String jwt){
        for(Token token : tokens){
            if(token.getToken().equals(jwt)){
                return token;
            }
        }
        return null;
    }

    public void setTokenRevoked(Token token){
        tokens.forEach( t -> {
            if (t.equals(token)) {
                t.setRevoked(true);
            }
        });
    }

    public boolean isNotRevoked(String jwt){
        for(Token token : tokens){
            if(token.getToken().equals(jwt) && !token.isRevoked()){
                return true;
            }
        }
        return false;
    }
}
