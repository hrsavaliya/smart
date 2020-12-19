package com.hk.smart.smartads.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.ads.AdRequest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public final class Util {

    private static String TAG = Util.class.getSimpleName();

    public static boolean isOnline(Context ctx) {
        ConnectivityManager connMgr = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it returns as
    // a string.
    public static String downloadDataFromUrl(String myurl) throws IOException {
        InputStream is = null;
        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000); // time in milliseconds
            conn.setConnectTimeout(15000); // time in milliseconds
            conn.setRequestMethod("GET"); // request method GET OR POST
            conn.setDoInput(true);
            // Starts the query
            conn.connect(); // calling the web address
            int response = conn.getResponseCode();
            Log.d(TAG, "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readInputStream(is);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    // Reads an InputStream and converts it to a String.
    public static String readInputStream(InputStream stream) throws IOException {
        int n = 0;
        char[] buffer = new char[1024 * 4];
        InputStreamReader reader = new InputStreamReader(stream, "UTF8");
        StringWriter writer = new StringWriter();
        while (-1 != (n = reader.read(buffer)))
            writer.write(buffer, 0, n);
        return writer.toString();
    }

    // if the same key occurs in h1 and h2, use the value from h1
    public static final Hashtable hashtableMerge(Hashtable h1, Hashtable h2) {
        // System.out.println("in hastableMerge");
        Hashtable h = new Hashtable();
        Enumeration keys = h1.keys();
        while (keys.hasMoreElements()) {
            Object k = keys.nextElement();
            if (h1.get(k) != null) {
                h.put(k, h1.get(k));
            }
        }
        keys = h2.keys();
        while (keys.hasMoreElements()) {
            Object k = keys.nextElement();
            if (!h.containsKey(k) && h2.get(k) != null) {
                h.put(k, h2.get(k));
            }
        }
        return h;
    }

    // sorts String values
    public static final Enumeration sort(Enumeration e) {
        Vector v = new Vector();
        while (e.hasMoreElements()) {
            String s = (String) e.nextElement();
            int i = 0;
            for (i = 0; i < v.size(); i++) {
                int c = s.compareTo((String) v.elementAt(i));
                if (c < 0) { // s should go before i
                    v.insertElementAt(s, i);
                    break;
                } else if (c == 0) { // s already there
                    break;
                }
            }
            if (i >= v.size()) { // add s at end
                v.addElement(s);
            }
        }
        return v.elements();
    }

    /**
     * Split string into multiple strings
     *
     * @param original  Original string
     * @param separator Separator string in original string
     * @return Splitted string array
     */
    public static final String[] split(String original, String separator) {
        Vector nodes = new Vector();

        // Parse nodes into vector
        int index = original.indexOf(separator);
        while (index >= 0) {
            nodes.addElement(original.substring(0, index));
            original = original.substring(index + separator.length());
            index = original.indexOf(separator);
        }
        // Get the last node
        nodes.addElement(original);

        // Create splitted string array
        String[] result = new String[nodes.size()];
        if (nodes.size() > 0) {
            for (int loop = 0; loop < nodes.size(); loop++)
                result[loop] = (String) nodes.elementAt(loop);
        }
        return result;
    }

    // this is an OAuth-friendly url encode -- should work fine for ordniary
    // encoding also
    public static final String urlEncode(String s) {
        if (s == null)
            return s;
        StringBuffer sb = new StringBuffer(s.length() * 3);
        try {
            char c;
            for (int i = 0; i < s.length(); i++) {
                c = s.charAt(i);
                if (c == '&') {
                    // sb.append("&amp;"); // don't do this
                    sb.append("%26");
                    // } else if (c == ' ') {
                    // sb.append('+'); // maybe don't do this either
                } else if (
                    // safe characters
                        c == '-' || c == '_' || c == '.' || c == '!' || c == '~'
                                || c == '*' || c == '\'' || c == '(' || c == ')'
                                || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
                    sb.append(c);
                } else {
                    sb.append('%');
                    if (c > 15) { // is it a non-control char, ie. >x0F so 2
                        // chars
                        sb.append(Integer.toHexString((int) c).toUpperCase()); // just
                        // add
                        // %
                        // and
                        // the
                        // string
                    } else {
                        sb.append("0"
                                + Integer.toHexString((int) c).toUpperCase());
                        // otherwise need to add a leading 0
                    }
                }
            }

        } catch (Exception ex) {
            return (null);
        }
        return (sb.toString());
    }

    // Reads an InputStream and converts it to a String.
    public static String readIt(InputStream stream) throws IOException,
            UnsupportedEncodingException {

        int ch;
        StringBuffer sb = new StringBuffer();
        while ((ch = stream.read()) != -1) {
            sb.append((char) ch);
        }
        return sb.toString().trim();
    }

    public static final String getUrlResponse(String url) {
        String str = null;
        URL urlObj = null;
        HttpURLConnection connection = null;
        InputStream inputstream = null;

        try {
            urlObj = new URL(url);
            connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                int len = 8192;
                inputstream = new BufferedInputStream(
                        connection.getInputStream(), len);
                str = readIt(inputstream);
            }
        } catch (Exception error) {
            Log.e("GET URL ERROR", error.getMessage());
        } finally {
            if (inputstream != null) {
                try {
                    inputstream.close();
                } catch (Exception error) {
                    /* log error */
                }
            }
        }
        return str;
    }

    public static String postUrlResponse(String url, String params)
            throws MalformedURLException {

        URL urlObj = new URL(url);
        HttpURLConnection c = null;
        InputStream is = null;
        OutputStream os = null;
        StringBuffer b = new StringBuffer();
        String response = "";

        try {
            c = (HttpURLConnection) urlObj.openConnection();

            // HTTP Request
            c.setRequestMethod("POST");
            c.setDoOutput(true);
            // c.setRequestProperty("Content-Length",
            // String.valueOf(params.length()));

            os = new BufferedOutputStream(c.getOutputStream());

            if (params != null)
                os.write(params.getBytes());

            os.flush();

            if (c.getResponseCode() == HttpURLConnection.HTTP_OK) {
                int len = 8192;
                is = new BufferedInputStream(c.getInputStream());
                response = readIt(is);
            }
        } catch (Exception ex) {
            Log.e("postUrlResponse Error", ex.getMessage());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception ex) {
                    Log.e("postUrlResponse Error", ex.getMessage());
                }
            }
        }

        return response;
    }

    private static char[] map1 = new char[64];

    static {
        int i = 0;
        for (char c = 'A'; c <= 'Z'; c++) {
            map1[i++] = c;
        }
        for (char c = 'a'; c <= 'z'; c++) {
            map1[i++] = c;
        }
        for (char c = '0'; c <= '9'; c++) {
            map1[i++] = c;
        }
        map1[i++] = '+';
        map1[i++] = '/';
    }

    public static final String base64Encode(byte[] in) {
        int iLen = in.length;
        int oDataLen = (iLen * 4 + 2) / 3;// output length without padding
        int oLen = ((iLen + 2) / 3) * 4;// output length including padding
        char[] out = new char[oLen];
        int ip = 0;
        int op = 0;
        int i0;
        int i1;
        int i2;
        int o0;
        int o1;
        int o2;
        int o3;
        while (ip < iLen) {
            i0 = in[ip++] & 0xff;
            i1 = ip < iLen ? in[ip++] & 0xff : 0;
            i2 = ip < iLen ? in[ip++] & 0xff : 0;
            o0 = i0 >>> 2;
            o1 = ((i0 & 3) << 4) | (i1 >>> 4);
            o2 = ((i1 & 0xf) << 2) | (i2 >>> 6);
            o3 = i2 & 0x3F;
            out[op++] = map1[o0];
            out[op++] = map1[o1];
            out[op] = op < oDataLen ? map1[o2] : '=';
            op++;
            out[op] = op < oDataLen ? map1[o3] : '=';
            op++;
        }
        return new String(out);
    }

    public static boolean validateEmailID(String email) {

        if (email == null || email.length() == 0 || email.indexOf("@") == -1
                || email.indexOf(" ") != -1) {
            return false;
        }
        int emailLenght = email.length();
        int atPosition = email.indexOf("@");

        String beforeAt = email.substring(0, atPosition);
        String afterAt = email.substring(atPosition + 1, emailLenght);

        if (beforeAt.length() == 0 || afterAt.length() == 0) {
            return false;
        }
        if (email.charAt(atPosition - 1) == '.') {
            return false;
        }
        if (email.charAt(atPosition + 1) == '.') {
            return false;
        }
        if (afterAt.indexOf(".") == -1) {
            return false;
        }
        char dotCh = 0;
        for (int i = 0; i < afterAt.length(); i++) {
            char ch = afterAt.charAt(i);
            if ((ch == 0x2e) && (ch == dotCh)) {
                return false;
            }
            dotCh = ch;
        }
        if (afterAt.indexOf("@") != -1) {
            return false;
        }
        int ind = 0;
        do {
            int newInd = afterAt.indexOf(".", ind + 1);

            if (newInd == ind || newInd == -1) {
                String prefix = afterAt.substring(ind + 1);
                if (prefix.length() > 1 && prefix.length() < 6) {
                    break;
                } else {
                    return false;
                }
            } else {
                ind = newInd;
            }
        } while (true);
        dotCh = 0;
        for (int i = 0; i < beforeAt.length(); i++) {
            char ch = beforeAt.charAt(i);
            if (!((ch >= 0x30 && ch <= 0x39) || (ch >= 0x41 && ch <= 0x5a)
                    || (ch >= 0x61 && ch <= 0x7a) || (ch == 0x2e)
                    || (ch == 0x2d) || (ch == 0x5f))) {
                return false;
            }
            if ((ch == 0x2e) && (ch == dotCh)) {
                return false;
            }
            dotCh = ch;
        }
        return true;
    }

    public static final int getMonthNumber(String month) {
        if (month.equalsIgnoreCase("Jan"))
            return 0;
        if (month.equalsIgnoreCase("Feb"))
            return 1;
        if (month.equalsIgnoreCase("Mar"))
            return 2;
        if (month.equalsIgnoreCase("Apr"))
            return 3;
        if (month.equalsIgnoreCase("May"))
            return 4;
        if (month.equalsIgnoreCase("Jun"))
            return 5;
        if (month.equalsIgnoreCase("Jul"))
            return 6;
        if (month.equalsIgnoreCase("Aug"))
            return 7;
        if (month.equalsIgnoreCase("Sep"))
            return 8;
        if (month.equalsIgnoreCase("Oct"))
            return 9;
        if (month.equalsIgnoreCase("Nov"))
            return 10;
        if (month.equalsIgnoreCase("Dec"))
            return 11;
        else
            return 0;
    }

    public static final String getMonthName(int month) {
        switch (month) {
            case 0:
                return "Jan";
            case 1:
                return "Feb";
            case 2:
                return "Mar";
            case 3:
                return "Apr";
            case 4:
                return "May";
            case 5:
                return "Jun";
            case 6:
                return "Jul";
            case 7:
                return "Aug";
            case 8:
                return "Sep";
            case 9:
                return "Oct";
            case 10:
                return "Nov";
            case 11:
                return "Dec";
            default:
                return "";
        }
    }

    public static Object findString(Vector tempVector, String searchString) {
        Object result = null;

        for (int i = 0; i < tempVector.size(); i++) {
            Object tempObject = tempVector.elementAt(i);

            if (tempObject.toString().equalsIgnoreCase(searchString)) {
                result = tempObject;
                return result;
            }
        }

        return result;
    }

    public static Vector reverseVector(Vector tempVector) {
        Vector result = new Vector();

        for (int i = tempVector.size() - 1; i >= 0; i--) {
            result.addElement(tempVector.elementAt(i));
        }

        return result;
    }


    /*
     * @param dp A value in dp (density independent pixels) unit. Which we need
     * to convert into pixels
     *
     * @param context Context to get resources and device specific display
     * metrics
     *
     * @return A float value to represent px equivalent to dp depending on
     * device density
     */
    public static float convertDpToPixel(float dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources()
                .getDisplayMetrics();
        int px = Math.round(dp
                * (displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    /**
     * This method converts device specific pixels to density independent
     * pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context) {
        DisplayMetrics displayMetrics = context.getResources()
                .getDisplayMetrics();
        int dp = Math.round(px
                / (displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public static int getPixels(int unit, float size) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return (int) TypedValue.applyDimension(unit, size, metrics);
    }

    public static String readResourceFile(Context context, String fileName) {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(fileName)));
            String line;
            StringBuilder text = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                text.append(line);
//                text.append('\n');
            }

            return text.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Location geFastLocation(Context context) {
        String locCtx = Context.LOCATION_SERVICE;

        LocationManager locationManager = (LocationManager) context.getSystemService(locCtx);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        String locationProvider = null;
        if (locationManager != null) {
            locationProvider = locationManager.getBestProvider(criteria, true);
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Location targetLocation = new Location("");//provider name is unecessary
            targetLocation.setLatitude(6.465422d);//your coords of course
            targetLocation.setLongitude(3.406448d);
            return null;
        }

        if (locationManager != null) {
            if (locationProvider != null) {
                return locationManager.getLastKnownLocation(locationProvider);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * This checks if the class name is present in the current library
     *
     * @param className
     * @return
     */
    public static boolean isClassAvailable(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (final ClassNotFoundException e) {
            return false;
        }
    }

    public static String getMessageFromAdMobErrorCode(int errorCode) {

        switch (errorCode) {
            case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                return "an invalid response was received from the ad server";

            case AdRequest.ERROR_CODE_INVALID_REQUEST:
                return "ad unit ID was incorrect";

            case AdRequest.ERROR_CODE_NETWORK_ERROR:
                return "The ad request was unsuccessful due to network connectivity";

            case AdRequest.ERROR_CODE_NO_FILL:
                return "The ad request was successful, but no ad was returned due to lack of ad inventory";

            default:
                return "";
        }

    }
}

