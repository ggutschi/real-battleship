/**
 * 
 */
package at.ac.uniklu.mobile.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.content.Context;
import android.provider.Settings.Secure;
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
		return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);		
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
