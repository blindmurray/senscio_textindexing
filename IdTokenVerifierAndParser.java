import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;

public class IdTokenVerifierAndParser {

	private static final String GOOGLE_CLIENT_ID = "953997974940-6oclg8f4hefo52qlufnb7bj1j0jscuoc.apps.googleusercontent.com";

	public static String getVerifiedEmail(String id) throws Exception {
		System.out.println("Connecting database...");
		System.out.println(id.length());
		GoogleIdToken.Payload payLoad = getPayload(id);
		String email = payLoad.getEmail();
		return email;
	}

	public static String getVerifiedName(String id) throws Exception {
		System.out.println("Connecting database...");
		System.out.println(id.length());
		GoogleIdToken.Payload payLoad = getPayload(id);

		String userName = (String) payLoad.get("name");
		return userName;
	}

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