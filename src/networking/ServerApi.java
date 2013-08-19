/**
 * Author: Adam James Dickin
 * Team: Ballmer's Peakin
 */
package networking;

/**
 * This class wraps the Battleship 3D server API into static strings. 
 * The server communicates with its clients though plain ASCII text.
 */
public class ServerApi
{
	//Required to Terminate each Request/Response.
	public static final String CARRIAGE_FEED_AND_LINE_RETURN = "\r\n";
	public static final String STRING_SEPARATOR = "|"; 
	
	//API variables specific for server messages.
	public static final String BATTLESHIP_SERVER_NAME = "Pason Undergraduate Battleship Server";
	public static final String BATTLESHIP_SERVER_OK_RESPONSE = "OK";
	public static final String BATTLESHIP_SERVER_FAIL_RESPONSE = "FAIL";
	public static final String BATTLESHIP_SERVER_LOSE_RESPONSE = "LOSE";
	public static final String BATTLESHIP_SERVER_WIN_RESPONSE = "WIN";
	public static final String BATTLESHIP_SERVER_GAME_RESPONSE = "GAME";
	public static final String BATTLESHIP_SERVER_MATCH_RESPONSE = "MATCH";
	public static final String BATTLESHIP_SERVER_MISS_RESPONSE = "MISS";
	public static final String BATTLESHIP_SERVER_HIT_RESPONSE = "HIT";
	
	
	//Ship Layout Names
	public static final String FRIGATE = "FF";
	public static final String SUBMARINE = "SSK";
	public static final String DESTROYER = "DDH";
	public static final String BATTLESHIP = "BB";
	public static final String CARRIER = "CVL";
	
	
	/*
	 * function that formats two strings into the required format for sending to the
	 * BattleShip server.
	 */
	public static String createTwoStringRequest(String firstField, String secondField)
	{
		String messageFormattedToSend = firstField + STRING_SEPARATOR 
			+ secondField + CARRIAGE_FEED_AND_LINE_RETURN;
		
		return messageFormattedToSend;
	}
}

