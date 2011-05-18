package at.ac.uniklu.mobile.util;

/**
 * Class containing all application specific constants
 */
public class Constants {
	/** logging tag for debugging **/
	public final static String LOG_TAG = "Real Battleship";
	/** default latitude for google map view **/
	public final static double DEFAULT_LATITUDE = 46.62794;
	/** default longitude for google map view **/
	public final static double DEFAULT_LONGITUDE = 14.30899;
	
	/** URL of webservice to retrieve all challenges from server **/
	//public final static String URL_WEBSERVICE_GETCHALLENGES = "http://10.0.2.2/rbs/getChallenges.php";	
	public final static String URL_WEBSERVICE_GETCHALLENGES = "http://cms03-typo3-dev.xgx.at/7050227546/rbs/getChallenges.php";
	/** URL of webservice to add a new particpiant to a challenge **/
	public final static String URL_WEBSERVICE_ADDPARTICIPANTS = "http://cms03-typo3-dev.xgx.at/7050227546/rbs/addParticipant.php";
	/** response code value if webservice call was successful **/
	public final static int WEBSERVICE_STATUSCODE_OK = 200;
	/** response code from webservice if the transaction was sucessfully performed (e.g. adding a new participant to the challenge) **/
	public final static String WEBSERVICE_TRANSACTION_OK = "OK";	
	/** the request code when starting an activity for result **/
	public final static int CMD_CODE_CHANGE_CHALLENGE = 1000;
	
}
