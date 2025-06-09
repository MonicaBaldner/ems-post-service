package com.algaworks.algaposts.ems_post_service.domain.exception;

public class PostNotFoundException extends RuntimeException{
    public PostNotFoundException(String message) {
        super(message);
    }
}
