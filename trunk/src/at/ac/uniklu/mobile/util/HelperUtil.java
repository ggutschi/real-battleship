/**
 * 
 */
package at.ac.uniklu.mobile.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

/**
 * this class provides some helpful methods (e.g. get device information like ip address or unique android id)
 *
 */
public class HelperUtil {
	
	
	/**
	 * returns the unique android id of the smartphone
	 * @param context
	 * @return a unique 64-bit hex string representing the unique android id
	 */
	public static String getAndroidId(Context context) {
		String id = Settings.Secure.getString(context.getContentResolver(),
		         Settings.Secure.ANDROID_ID);
		
		// TODO: remove next two lines when deploying app (only needed on emulator, because emulator doesnt have a device id)
		if (id == null)
			id = Constants.DEFAULT_EMULATOR_ANDROID_ID;
		
		return id;
	}
	
	/**
	 * returns the ip address of the smartphone
	 * @return current ip address of the smartphone
	 */
	public static String getIpAddress()  {
		try {
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
	            NetworkInterface intf = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
	                InetAddress inetAddress = enumIpAddr.nextElement();
	                if (!inetAddress.isLoopbackAddress()) {
	                    return inetAddress.getHostAddress().toString();
	                }
	            }
	        }
	    } catch (SocketException ex) {
	        Log.e(Constants.LOG_TAG, "could not determine device´ ip address", ex);
	    }
	    return null;
	}

}
