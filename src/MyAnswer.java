/*
This is my answer to the A5-WebService-assigmenet
Andreas Øie
 */

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.lang.Math.sqrt;

public class MyAnswer {

    private String email = "andreoi@stud.ntnu.no";
    private String phone = "46988288";
    private String host = "52.164.220.230";
    private int port = 80;
    private String BASE_URL = "http://" + host + ":" + port + "/";

    public static void main(String[] args) {

        // Create answer
        MyAnswer assignment = new MyAnswer();

        // Get ID from LOGIN
        int tempSessionID = assignment.authorizeLogin();

        String task1 = assignment.requestTask(tempSessionID, "1");
        assignment.sendAnswer(tempSessionID, task1);

        String task2 = assignment.requestTask(tempSessionID, "2");
        assignment.sendAnswer(tempSessionID, task2);

        String task3 = assignment.requestTask(tempSessionID, "3");
        assignment.sendAnswer(tempSessionID, task3);

        String task4 = assignment.requestTask(tempSessionID, "4");
        assignment.sendAnswer(tempSessionID, task4);

        String secretTask = assignment.requestTask(tempSessionID, assignment.getSecretTask());
        assignment.sendAnswer(tempSessionID, secretTask);

        //assignment.requestSecretTask(tempSessionID);
        // GET RESULTS
        //assignment.sendGET("dkrest/results/" + tempSessionID);

    }

    /**
     * Login and retrieve session-ID
     */
    private int authorizeLogin() {
        int sessionID = 0;
        String loginPath = "dkrest/auth";
        JSONObject loginObj = new JSONObject();
        loginObj.put("email", email);
        loginObj.put("phone", phone);

        // send & receive
        String jsonINFO = sendPOST(loginPath, loginObj);
        JSONObject obj = new JSONObject(jsonINFO);

        if (obj.has("sessionId")) {
            sessionID = obj.getInt("sessionId");
        }
        return sessionID;
    }

    private String requestTask(int sessID, String taskNumber) {
        String requestObj = null;
        String taskPath = "dkrest/gettask/" + taskNumber + "?sessionId=" + sessID;
        requestObj = sendGET(taskPath);
        return requestObj;
    }

    private String getSecretTask() {
        Double d = sqrt(4064256);
        int numbr = d.intValue();
        String secretNumber = Integer.toString(numbr);
        return secretNumber;
    }

    private void requestSecretTask(int sessID) {
        String requestObj = null;
        String path = "dkrest/gettask/secrettask?sessionId=" + sessID;
        requestObj = sendGET(path);
    }

    private void sendAnswer(int sessID, String taskObj) {

        JSONObject answerOBJ = new JSONObject();
        answerOBJ.put("sessionId", sessID);

        String solvingPath = "dkrest/solve";
        JSONObject task = new JSONObject(taskObj);

        int taskNumber = 0;
        if (task.has("taskNr")) {
            taskNumber = task.getInt("taskNr");
        }
        switch (taskNumber) {

            case 1:
                answerOBJ.put("msg", "Hello");
                break;

            case 2:
                if (task.has("arguments")) {
                    String answer = task.getJSONArray("arguments").getString(0);
                    answerOBJ.put("msg", answer);
                }
                break;

            case 3:
                if (task.has("arguments")) {
                    JSONArray intArray = task.getJSONArray("arguments");
                    String answer = multiplyNumbers(intArray);
                    answerOBJ.put("result", answer);
                }
                break;

            case 4:
                if (task.has("arguments")) {
                    String pwHash = task.getJSONArray("arguments").getString(0);
                    String answer = iterateHashSolution(pwHash);
                    answerOBJ.put("pin", answer);
                }
                break;
            case 2016:
                if (task.has("arguments")) {
                    String correctIP = null; // ip=yyy.yyy.yyy.yyy. (IPv4)
                    String IPAddress = task.getJSONArray("arguments").getString(0);
                    String subNetMask = task.getJSONArray("arguments").getString(1);
                    correctIP = findSubnetNumber(IPAddress, subNetMask);
                    System.out.println(correctIP);
                    answerOBJ.put("ip", correctIP);
                }
                break;

            default:
                // pass
                break;
        }
        sendPOST(solvingPath, answerOBJ);
    }

    private String multiplyNumbers(JSONArray numbers) {
        int total = 1;
        for (int i = 0; i < numbers.length(); i++) {
            String numb = numbers.getString(i);
            int numbr = Integer.parseInt(numb);
            total = total * numbr;
        }
        return String.valueOf(total);
    }

    private String getHash(String numbr) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] hashInBytes = md.digest(numbr.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private String iterateHashSolution(String hashGoal) {

        String currentPin = null;

        boolean hashFound = false;
        while (!hashFound) {
            for (int i = 0; i < 10000; i++) {
                String numb = String.format("%04d", i);
                String tempHash = getHash(numb);
                if (tempHash.equals(hashGoal)) {
                    hashFound = true;
                    currentPin = numb;
                }
            }
        }
        return currentPin;
    }


    // "198.92.236.0", "255.255.252.0"
    private String findSubnetNumber(String tempIP, String tempSUB) {

        String[] a = tempIP.split("\\.", 4);
        String[] b = tempSUB.split("\\.",4);
        String[] testIP = {"0", "0", "0", "0"};

        for (int i = 0; i < 4; i++) {

            if(b[i].equals("255")) {
                testIP[i] = a[i];
            }
            else if (b[i].equals("0")) {
                testIP[i] = b[i];
            }
            else {
                int aNumb = Integer.parseInt(a[i]);
                int bNumb = Integer.parseInt(b[i]);

                double difference = 256 - (double) bNumb;
                // C = 16
                double scalar = (double) aNumb / difference;
                // D = 73 / 16 = 4,56
                int scaleInt = (int) scalar;
                // E = 4
                int intC = (int) difference;
                // intC = 16
                int lastNum = scaleInt*intC;
                String tempNum = String.valueOf(lastNum);
                testIP[i] = tempNum;
            }
        }
        return String.join(".", testIP);
    }


    private String sendGET(String path) {
        String returnObj = null;
        try {
            String url = BASE_URL + path;
            URL urlObj = new URL(url);
            System.out.println("Sending HTTP GET to " + url);
            HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                System.out.println("Server reached");
                // Response was OK, read the body (data)
                InputStream stream = con.getInputStream();
                returnObj = convertStreamToString(stream);
                stream.close();
                System.out.println("Response from the server:");
                System.out.println(returnObj);
            } else {
                String responseDescription = con.getResponseMessage();
                System.out.println("Request failed, response code: " + responseCode + " (" + responseDescription + ")");
            }
        } catch (ProtocolException e) {
            System.out.println("Protocol not supported by the server");
        } catch (IOException e) {
            System.out.println("Something went wrong: " + e.getMessage());
            e.printStackTrace();
        }
        return returnObj;
    }


    private String sendPOST(String path, JSONObject jsonData) {
        String returnObj = null;
        try {
            String url = BASE_URL + path;
            URL urlObj = new URL(url);

            System.out.println("Sending HTTP POST to " + url);
            HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            OutputStream os = con.getOutputStream();
            os.write(jsonData.toString().getBytes());
            os.flush();
            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                System.out.println("Server reached");

                // Response was OK, read the body (data)
                InputStream stream = con.getInputStream();
                returnObj = convertStreamToString(stream);
                stream.close();
                System.out.println("Response from the server:");
                System.out.println(returnObj);
            } else {
                String responseDescription = con.getResponseMessage();
                System.out.println("Request failed, response code: " + responseCode + " (" + responseDescription + ")");
            }
        } catch (ProtocolException e) {
            System.out.println("Protocol nto supported by the server");
        } catch (IOException e) {
            System.out.println("Something went wrong: " + e.getMessage());
            e.printStackTrace();
        }
        return returnObj;
    }

    /**
     * Read the whole content from an InputStream, return it as a string
     *
     * @param is Inputstream to read the body from
     * @return The whole body as a string
     */
    private String convertStreamToString(InputStream is) {
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        StringBuilder response = new StringBuilder();
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                response.append('\n');
            }
        } catch (IOException ex) {
            System.out.println("Could not read the data from HTTP response: " + ex.getMessage());
        }
        return response.toString();
    }
}