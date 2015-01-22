package livefyre.streamhub;

public class Config {
	// comments
	public static String scheme = "http";
	public static String environment = "livefyre.com";
	public static String bootstrapDomain = "bootstrap";
	public static String quillDomain = "quill";
	public static String adminDomain = "admin";
	public static String streamDomain = "stream1";
	public static String networkId ="labs.fyre.co";

	public static String getHostname(String networkId)
	{
	if (networkId.equals("livefyre.com")) {
	return Config.environment;
	}
	return networkId;
	}

	// reviews
//	public static String scheme = "http";
//	public static String environment = "livefyre.com";
//	public static String bootstrapDomain = "bootstrap";
//	public static String quillDomain = "quill";
//	public static String adminDomain = "admin";
//	public static String streamDomain = "stream1";
//	public static String networkId = "client-solutions.fyre.co";
//
//	public static String getHostname(String networkId) {
//		if (networkId.equals("livefyre.com")) {
//			return Config.environment;
//		}
//		return networkId;
//	}

}
