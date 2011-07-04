package at.ac.uniklu.mobile.util;

/**
 * Class containing all application specific constants
 */
public class Constants {
	/** logging tag for debugging **/
	public final static String LOG_TAG = "Real Battleship";
	/** default latitude for google map view **/
	public final static double DEFAULT_LATITUDE = 46.61615;
	/** default longitude for google map view **/
	public final static double DEFAULT_LONGITUDE = 14.2651;
	/** default android identifier for the emulator (because the emulator doesnt have a unique device id) **/
	public final static String DEFAULT_EMULATOR_ANDROID_ID = "e3f17bc89afe54fe";
	
	/** URL of webservice to retrieve all challenges from server **/
	//public final static String URL_WEBSERVICE_GETCHALLENGES = "http://10.0.2.2/rbs/getChallenges.php";	
	public final static String URL_WEBSERVICE_GETCHALLENGES = "http://cms03-typo3-dev.xgx.at/7050227546/rbs/getChallenges.php";
	//public final static String URL_WEBSERVICE_GETCHALLENGES = "http://192.168.1.101/rbs/getChallenges.php";
	/** URL of webservice to retrieve a challenge from server **/
	//public final static String URL_WEBSERVICE_GETCHALLENGES = "http://10.0.2.2/rbs/getChallenges.php";	
	public final static String URL_WEBSERVICE_GETCHALLENGE = "http://cms03-typo3-dev.xgx.at/7050227546/rbs/getChallenge.php";
	/** URL of webservice to add a new particpiant to a challenge **/
	public final static String URL_WEBSERVICE_ADDPARTICIPANTS = "http://cms03-typo3-dev.xgx.at/7050227546/rbs/addParticipant.php";
	//public final static String URL_WEBSERVICE_ADDPARTICIPANTS = "http://192.168.1.101/rbs/addParticipant.php";
	/** URL of webservice to uncover a shipposition **/
	public final static String URL_WEBSERVICE_UNCOVERSHIPPOSITION = "http://cms03-typo3-dev.xgx.at/7050227546/rbs/updateChallengeState.php";
	//public final static String URL_WEBSERVICE_UNCOVERSHIPPOSITION = "http://192.168.1.101/rbs/updateChallengeState.php";
	/** URL of webservice to clear all challenges (reset uncovered & destroyed flags of all ships) **/
	public final static String URL_WEBSERVICE_CLEARCHALLENGE = "http://cms03-typo3-dev.xgx.at/7050227546/rbs/resetChallenges.php";
	//public final static String URL_WEBSERVICE_CLEARCHALLENGE = "http://192.168.1.101/rbs/resetChallenges.php";
	/** response code value if webservice call was successful **/
	public final static int WEBSERVICE_STATUSCODE_OK = 200;
	/** response code from webservice if the transaction was sucessfully performed (e.g. adding a new participant to the challenge) **/
	public final static String WEBSERVICE_TRANSACTION_OK = "OK";	
	/** the request code when starting an activity for result **/
	public final static int CMD_CODE_CHANGE_CHALLENGE = 1000;
	/** the return code from challenge list activity if challenge registration was not successful **/
	public final static int RETURN_CODE_CHANGE_CHALLENGE_ERROR = 400;
	/** response code value if webservice call was successful **/
	public final static String WEBSERVICE_STATUSCODE_UNCOVERED = "OK";
	/** response code from webservice if the transaction was sucessfully performed (e.g. adding a new participant to the challenge) **/
	public final static String WEBSERVICE_STATUSCODE_NOT_UNCOVERED = "NOK";	
	/** port for peer to peer messaging and rendezvous server **/
	public final static int PEER_TO_PEER_PORT = 19423;
	/** separator for peer messages **/
	public final static char MESSAGE_SEP_CHAR = ';';
	/** message prefix for joining **/
	public final static String JOINED_MSG = "joined";
	/** message prefix for uncovering fields **/
	public final static String UNCOVERED_MSG = "uncovered";
	/** message prefix for uncovering fields **/
	public final static String RELEASED_MSG = "released";
}
