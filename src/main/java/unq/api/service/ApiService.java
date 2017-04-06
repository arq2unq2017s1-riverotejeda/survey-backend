package unq.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import unq.api.controller.SurveyController;
import unq.api.security.SecurityFilter;
import unq.utils.WebServiceConfiguration;

/**
 * Created by mrivero on 17/9/16.
 */
public class ApiService {

	public static Logger LOGGER = LoggerFactory.getLogger(ApiService.class);

	public static void main(String[] args) {
		LOGGER.info("Starting services configuration");
		WebServiceConfiguration.getInstance().initConfiguration();
		SecurityFilter.initSecurityFilter();
		SurveyController.initSurveyEndpoints();
		LOGGER.info("Finish services configuration");
	}

}