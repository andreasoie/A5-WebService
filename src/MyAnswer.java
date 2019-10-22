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

public class MyAnswer {

    private String email = "andreoi@stud.ntnu.no";
    private String phone = "46988288";
    String host = "52.164.220.230";
    int port = 80;
    String BASE_URL = "http://" + host + ":" + port + "/";

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


        // assignment.requestTask(sessID, "1");


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
        String taskPath = "dkrest/gettask/"+ taskNumber + "?sessionId=" + sessID;
        requestObj = sendGET(taskPath);
        return requestObj;
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
                    String hash = task.getString("arguments");
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
            total = total*numbr;
        }
        return String.valueOf(total);
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

