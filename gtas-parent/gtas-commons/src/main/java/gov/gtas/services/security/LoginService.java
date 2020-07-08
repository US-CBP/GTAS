package gov.gtas.services.security;

public interface LoginService {

    void resetFailedLoginAttemptCount(String userId);

    void addToFailAttempts(String userId);

    Integer getUserLoginAttempts(String userId);

}
