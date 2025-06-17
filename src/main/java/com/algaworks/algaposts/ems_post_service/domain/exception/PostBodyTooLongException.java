package com.algaworks.algaposts.ems_post_service.domain.exception;

public class PostBodyTooLongException extends RuntimeException{

    public PostBodyTooLongException(String message) {
        super(message);
    }
}
