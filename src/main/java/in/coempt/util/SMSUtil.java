package in.coempt.util;

import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 *
 * @author milan.patnaik
 */
@Component
public class SMSUtil {

    public static String sendBulkSms(String[] mobileNumbers, String message,String templateId) {
        String iStatus = null;

        try {
            String data = "user=" + URLEncoder.encode("NITTTRR", "UTF-8");
            data += "&password=" + URLEncoder.encode("d0@*$me$$@ge", "UTF-8");
            data += "&message=" + URLEncoder.encode(message, "UTF-8");
            data += "&sender=" + URLEncoder.encode("COEMPT", "UTF-8");
            data += "&mobile=" + URLEncoder.encode(org.apache.commons.lang3.StringUtils.join(mobileNumbers, ","), "UTF-8");
            data += "&type=" + URLEncoder.encode("3", "UTF-8");
            data +="&template_id=" + URLEncoder.encode(templateId, "UTF-8");
            URL url = new URL("https://api.bulksmsgateway.in/sendmessage.php?" + data);
            // URL url = new URL("http://login.bulksmsgateway.in/sendmessage.php?"+data);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();
// Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            String sResult1 = "";
            while ((line = rd.readLine()) != null) {
// Process line...
                sResult1 = sResult1 + line + " ";
            }
            wr.close();
            rd.close();
            return sResult1;
        } catch (Exception e) {
            System.out.println("Error SMS " + e);
            return "Error " + e;
        }

    }

    //to bulk sms
    public static String sendSms1(String mobileNumber, String message,String templateId) {
        String sResult = null;
        try {
// Construct data

            String data = "user=" + URLEncoder.encode("NITTTR", "UTF-8");
            data += "&password=" + URLEncoder.encode("d0@*$me$$@ge", "UTF-8");
            data += "&message=" + URLEncoder.encode(message, "UTF-8");
            data += "&sender=" + URLEncoder.encode("EXAMOE", "UTF-8");
            data += "&mobile=" + URLEncoder.encode(mobileNumber, "UTF-8");
            data += "&type=" + URLEncoder.encode("3", "UTF-8");
            data +="&template_id=" + URLEncoder.encode(templateId, "UTF-8");
// Send data
            URL url = new URL("https://api.bulksmsgateway.in/sendmessage.php?" + data);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();
// Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            String sResult1 = "";
            while ((line = rd.readLine()) != null) {
// Process line...
                sResult1 = sResult1 + line + " ";
            }
            wr.close();
            rd.close();
            return sResult1;
        } catch (Exception e) {
            System.out.println("Error SMS " + e);
            return "Error " + e;
        }

    }
    public static String sendSCTEVTSms(String mobileNumber, String message,String templateId) {
        String sResult = null;
        try {
// Construct data

            String data = "user=" + URLEncoder.encode("NITTTR", "UTF-8");
            data += "&password=" + URLEncoder.encode("d0@*$me$$@ge", "UTF-8");
            data += "&message=" + URLEncoder.encode(message, "UTF-8");
            data += "&sender=" + URLEncoder.encode("BPUTOE", "UTF-8");
            data += "&mobile=" + URLEncoder.encode(mobileNumber, "UTF-8");
            data += "&type=" + URLEncoder.encode("3", "UTF-8");
            data +="&template_id=" + URLEncoder.encode(templateId, "UTF-8");
// Send data
            URL url = new URL("https://api.bulksmsgateway.in/sendmessage.php?" + data);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();
// Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            String sResult1 = "";
            while ((line = rd.readLine()) != null) {
// Process line...
                sResult1 = sResult1 + line + " ";
            }
            wr.close();
            rd.close();
            return sResult1;
        } catch (Exception e) {
            System.out.println("Error SMS " + e);
            return "Error " + e;
        }

    }

    public static void main(String[] args) {
        sendSms1("9912881065", "Dear srinu@coempt.in coempt","1507161526768082814");
    }

}
