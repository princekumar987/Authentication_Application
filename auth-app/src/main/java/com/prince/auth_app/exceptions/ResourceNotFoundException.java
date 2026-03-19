package com.prince.auth_app.exceptions;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message){
          super(message);
    }

    public  ResourceNotFoundException(){
          super("user not found");
    }
}
