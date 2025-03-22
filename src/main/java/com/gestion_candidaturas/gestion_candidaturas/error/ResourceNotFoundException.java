package com.gestion_candidaturas.gestion_candidaturas.error;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message){
        super(message);
    }

    public static ResourceNotFoundException of (String resourceName, String fieldName, Object fieldValue){
        return new ResourceNotFoundException(
                String.format("%s no encontrado con % : '%s'", resourceName, fieldName, fieldValue)
        );
    }
}
