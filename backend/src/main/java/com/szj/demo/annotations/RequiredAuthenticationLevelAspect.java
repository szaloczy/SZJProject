package com.szj.demo.annotations;

import com.szj.demo.enums.AuthenticationLevel;
import com.szj.demo.exception.InsufficientPrivilageException;
import com.szj.demo.exception.InvalidTokenException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.lang.reflect.Method;

@Aspect
public class RequiredAuthenticationLevelAspect {

    private final UserService userService;

    @Autowired
    public RequiredAuthenticationLevelAspect(UserService userService){
        this.userService = userService;
    }

    @Pointcut("@annotation(com.evoBid.evoBid.annotations.RequiredAuthenticationLevel)")
    public void requiredAuthenticationLevelPointcut(){}


    /**
     * This method is an aspect that is executed before methods annotated with @RequiredAuthenticationLevel.
     * It checks if the current user has the required authentication level to access the method.
     * If the user does not have the required level, it throws exceptions:
     * - If the user has an invalid token, it throws InvalidTokenException.
     * - If the user has an insufficient privilege, it throws InsufficientPrivilegeException.
     * - If the user is unauthorized, it throws UnauthorizedException.
     *
     * @param joinPoint The join point representing the method execution.
     * @throws UnauthorizedException      If the user is unauthorized to access the method.
     * @throws ForbiddenException         If the user has insufficient privileges to access the method.
     */
    @Before(value = "requiredAuthenticationLevelPointcut()")
    public void requiredAuthenticationLevel(JoinPoint joinPoint){
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequiredAuthenticationLevel annotation = method.getAnnotation(RequiredAuthenticationLevel.class);

        if(annotation != null){
            AuthenticationLevel level = annotation.level();
            try {
                if(!userService.canAcces(level)){
                    throw new InsufficientPrivilageException();
                }
            } catch (InvalidTokenException e){
                throw new UnauthorizedException();
            } catch (InsufficientPrivilageException e){
                throw new ForbiddenException();
            }
        }
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class UnauthorizedException extends RuntimeException{
        public UnauthorizedException() {
            super();
        }
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    public static class ForbiddenException extends RuntimeException {
        public ForbiddenException() {
            super();
        }
    }
}
