package com.gestion_candidaturas.gestion_candidaturas.error;


public class ApiError {
    private String status;
    private ErrorDetails error;

    public static class ErrorDetails{
        private String code;
        private String message;

        public ErrorDetails() {
        }

        public ErrorDetails(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static ApiError of(String code, String message){
        return new ApiError("error", new ErrorDetails(code, message));
    }

    public ApiError() {
    }

    public ApiError(String status, ErrorDetails error) {
        this.status = status;
        this.error = error;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ErrorDetails getError() {
        return error;
    }

    public void setError(ErrorDetails error) {
        this.error = error;
    }
}
