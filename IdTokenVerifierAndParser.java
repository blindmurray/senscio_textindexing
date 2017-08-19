import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;

/**
 * This class will take the id token of the user and verify that they are a registered
 * google email. Part of this class requires a Google client id that can be retrieved from
 * Google APIs.
 * @author Gina
 *
 */
public class IdTokenVerifierAndParser {

	/*The google_client_id is a unique identification for google log in and is used to 
	 * certify whether or not the email is a legitimate google email. Please ensure that this
	 * one is updated to the current Google login.
	 */
	private static final String GOOGLE_CLIENT_ID = "953997974940-6oclg8f4hefo52qlufnb7bj1j0jscuoc.apps.googleusercontent.com";

	/**
	 * Retrieves the email of the user logged-in
	 * @param id The id token to be verified
	 * @return Email of the user
	 * @throws Exception
	 */
	public static String getVerifiedEmail(String id) throws Exception {
		GoogleIdToken.Payload payLoad = getPayload(id);
		String email = payLoad.getEmail();
		return email;
	}

	/**
	 * Retrieves the name of the user logged-in
	 * @param id The id token to be verified
	 * @return Name of the user
	 * @throws Exception
	 */
	public static String getVerifiedName(String id) throws Exception {
		GoogleIdToken.Payload payLoad = getPayload(id);
		String userName = (String) payLoad.get("name");
		return userName;
	}
	/**
	 * Verifies the Google ID token using Payload.
	 * @param tokenString The id token being verified
	 * @return Returns a Payload to be used to retrieve other objects.
	 * @throws Exception
	 */
	public static GoogleIdToken.Payload getPayload(String tokenString) throws Exception {
		JacksonFactory jacksonFactory = new JacksonFactory();
		GoogleIdTokenVerifier googleIdTokenVerifier = new GoogleIdTokenVerifier(new NetHttpTransport(), jacksonFactory);
		GoogleIdToken token = GoogleIdToken.parse(jacksonFactory, tokenString);

		if (googleIdTokenVerifier.verify(token)) {
			GoogleIdToken.Payload payload = token.getPayload();
			if (!GOOGLE_CLIENT_ID.equals(payload.getAudience())) {
				throw new IllegalArgumentException("Audience mismatch");
			} else if (!GOOGLE_CLIENT_ID.equals(payload.getAuthorizedParty())) {
				throw new IllegalArgumentException("Client ID mismatch");
			}
			return payload;
		} else {
			throw new IllegalArgumentException("id token cannot be verified");
		}
	}
}