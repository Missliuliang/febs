package cc.mrbird.common.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddressUtils {
    public static void main(String[] args) throws UnsupportedEncodingException {
        System.out.println(getRealAddressByIP("192.168.1.132", null));
    }

    public static String getAddress(String content ,String encodingString ) throws UnsupportedEncodingException {
        String urlStr = "http://ip.taobao.com/service/getIpInfo.php";
        String returnStr = getResult(urlStr, content, encodingString);
        if (returnStr != null) {
            returnStr = encodeUnicode(returnStr);
            String[] temp = returnStr.split(",");
            if (temp.length < 3) {
                return "0";
            }
            return returnStr;
        }
        return null;
    }


    private static String getResult(String urlstr ,String content ,String encoding){
        URL url=null;
        HttpURLConnection connection=null;

        try {
            url=new URL(urlstr);
            connection=(HttpURLConnection) url.openConnection();
            connection.setReadTimeout(2000);
            connection.setConnectTimeout(2000);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.connect();
            DataOutputStream dataOutputStream=new DataOutputStream(connection.getOutputStream());
            dataOutputStream.writeBytes(content);
            dataOutputStream.flush();
            dataOutputStream.close();
            BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream(),encoding));
            StringBuffer sb=new StringBuffer();
            String line="";
            while ((line=reader.readLine())!=null ){
                sb.append(line);
            }
            reader.close();
            return sb.toString();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (connection !=null){
                connection.disconnect();
            }
        }
        return null;
    }

    public static  String encodeUnicode(String theString){
        char aChar;
        int len =theString.length();
        StringBuffer stringBuffer=new StringBuffer(len);
        for (int i = 0; i<len;) {
            aChar=theString.charAt(i++);
            if (aChar=='\\'){
                aChar=theString.charAt(i++);
                if (aChar=='u'){
                    int value=0;
                    for (int j=0 ; j<4 ; j++){
                        aChar=theString.charAt(i++);
                        switch (aChar){
                                case '0':
                                case '1':
                                case '2':
                                case '3':
                                case '4':
                                case '5':
                                case '6':
                                case '7':
                                case '8':
                                case '9':
                                    value = (value << 4) + aChar - '0';
                                    break;
                                case 'a':
                                case 'b':
                                case 'c':
                                case 'd':
                                case 'e':
                                case 'f':
                                    value = (value << 4) + 10 + aChar - 'a';
                                    break;
                                case 'A':
                                case 'B':
                                case 'C':
                                case 'D':
                                case 'E':
                                case 'F':
                                    value = (value << 4) + 10 + aChar - 'A';
                                    break;
                                default:
                                    throw new IllegalArgumentException("Malformed      encoding.");
                            }
                        }
                        stringBuffer.append((char) value);
                    } else {
                        if (aChar == 't') {
                            aChar = '\t';
                        } else if (aChar == 'r') {
                            aChar = '\r';
                        } else if (aChar == 'n') {
                            aChar = '\n';
                        } else if (aChar == 'f') {
                            aChar = '\f';
                        }
                    stringBuffer.append(aChar);
                    }
                } else {
                stringBuffer.append(aChar);
                }
            }
            return stringBuffer.toString();
        }



    public static String getRealAddressByIP(String ip, ObjectMapper mapper) {
        String address = "";
        try {
            address = getAddress("ip=" + ip, "utf-8");
            JsonNode node = mapper.readTree(address);
            JsonNode data = node.get("data");
            String region = data.get("region").asText();
            String city = data.get("city").asText();
            address = region + "" + city;
        } catch (Exception e) {

        }
        return address;
    }
}

