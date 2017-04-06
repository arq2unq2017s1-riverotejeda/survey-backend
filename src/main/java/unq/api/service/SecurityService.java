package unq.api.service;

/**
 * Created by mrivero on 25/3/17.
 */
public interface SecurityService {

    String getDirectorToken(String token);
    String getStudentToken(String token);
}
