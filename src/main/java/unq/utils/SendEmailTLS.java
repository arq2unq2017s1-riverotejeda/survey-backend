package unq.utils;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * Created by mrivero on 26/11/16.
 */
public class SendEmailTLS {

	private static Logger LOGGER = LoggerFactory.getLogger(SendEmailTLS.class);

	public static void sendEmailSurveyNotification(String studentName, String studentEmail, String surveyUrl) {

		LOGGER.info(String.format("Starting sending email to %s", studentEmail));

		String ME = "Mailgun Sandbox <postmaster@sandboxb55f660394844ec3a37608408b60d2c4.mailgun.org>";
		String API_KEY = "key-9bd988f6d52e0f89f1348f03c684dfb6";
		String CLIENT_RESOURCE = "https://api.mailgun.net/v3/sandboxb55f660394844ec3a37608408b60d2c4.mailgun.org/messages";

		Client client = Client.create();
		client.addFilter(new HTTPBasicAuthFilter("api", API_KEY));
		WebResource webResource = client.resource(CLIENT_RESOURCE);

		MultivaluedMapImpl formData = new MultivaluedMapImpl();
		formData.add("from", ME);
		formData.add("to", studentEmail);
		formData.add("subject", "Encuesta UNQ 2016");
		formData.add("html", String.format("Hola %s," + "\n\n Completa la encuesta de pre inscripcion con esta url: %s",
				studentName, surveyUrl));

		ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_FORM_URLENCODED)
				.post(ClientResponse.class, formData);
		String output = clientResponse.getEntity(String.class);

		LOGGER.info("Email sent successfully : " + output);
	}
}
