package unq.api.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unq.api.model.Director;
import unq.api.model.Student;
import unq.api.security.SecurityFilter;
import unq.api.service.RepositoryService;
import unq.api.service.SecurityService;

/**
 * Created by mrivero on 25/3/17.
 */
public class SecurityServiceImpl implements SecurityService {

    private static Logger LOGGER = LoggerFactory.getLogger(SecurityServiceImpl.class);
    private static RepositoryService repositoryService = RepositoryServiceImpl.getInstance();

    private static SecurityService INSTANCE = null;

    public static SecurityService getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new SecurityServiceImpl();
        }
        return INSTANCE;
    }

    @Override
    public String getDirectorToken(String token) {
        LOGGER.info("Getting director token");
        return repositoryService.getDirectorByToken(token).map(Director::getToken).orElse("");
    }

    @Override
    public String getStudentToken(String token) {
        LOGGER.info("Getting student token");
        return repositoryService.getStudentByToken(token).map(Student::getAuthToken).orElse("");
    }
}
