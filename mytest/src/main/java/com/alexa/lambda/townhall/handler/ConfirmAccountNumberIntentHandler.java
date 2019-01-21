package com.alexa.lambda.townhall.handler;

import com.alexa.lambda.townhall.LambdaConstants;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.amazon.ask.request.Predicates;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

public class ConfirmAccountNumberIntentHandler implements RequestHandler {

    @Override
    public boolean canHandle(HandlerInput input) {
        System.out.println(">> in ConfirmAccountNumberIntentHandler.handle()");
        return input.matches(Predicates.intentName("confirm_account_number"));
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {
        final String ACCOUNT_NUMBER_KEY = "ACCOUNT_NUMBER";
        System.out.println("confirm account number input: " + input.getRequest().toString());

        String accessToken = input
                .getRequestEnvelope()
                .getContext()
                .getSystem()
                .getUser()
                .getAccessToken();
        String speechText = "Thank you, I'm working on it. please wait.";
        String invalidAccountLengthText = "The account number can only be 10 digits. please try again.";

        IntentRequest intentRequest = (IntentRequest) input.getRequestEnvelope().getRequest();
        System.out.println(">>> Intent Name: "+intentRequest.getIntent().getName());
        System.out.println(">>> Intent Value: "+ Arrays.toString(intentRequest.getIntent().getSlots().entrySet().toArray()));

        String accountNumberValue = "";
        if (! intentRequest.getIntent().getSlots().entrySet().isEmpty()
            && intentRequest.getIntent().getSlots().containsKey(ACCOUNT_NUMBER_KEY)) {

            Slot slot = intentRequest.getIntent().getSlots().get(ACCOUNT_NUMBER_KEY);
            accountNumberValue = slot.getValue();
            System.out.println(">>> Slot Account number: "+ accountNumberValue);
        }

        if (accountNumberValue.length() != 10) {
            return input.getResponseBuilder()
                    .withSpeech(invalidAccountLengthText)
                    .withSimpleCard("Warning - Invalid account number", invalidAccountLengthText)
                    .withReprompt("Could you please confirm your account number?")
                    .build();
        }


/*
        if (accessToken != null) {
            // call a method to validate the token, get the profile and balance
            return input.getResponseBuilder()
                    .withSpeech(speechText.concat(" you current balance is 23000 baht"))
                    .withSimpleCard("Next step - response with account balance", speechText)
                    .build();
        }
*/

        // Getting account balance by Building an Asynchronous Alexa Progressive Response
        String balance = getAccountBalance(accountNumberValue);
        balance = "2000";
        return input.getResponseBuilder()
                .withSpeech(speechText.concat(" you current balance is "+ balance + "baht"))
                .withSimpleCard("Next step - response with account balance", speechText)
                .build();
    }

    private String getAccountBalance(String accountNumber) {
        String balance = "0.00";
        try {
            URL url = new URL(LambdaConstants.url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(LambdaConstants.METHOD);
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("apikey",LambdaConstants.apiKey);
            conn.setRequestProperty("apisecret",LambdaConstants.apiSecret);
            conn.setRequestProperty("requestUID",generateRqUID());
            conn.setRequestProperty("requestUID","TEST");
            conn.setRequestProperty("resourceOwnerID","TEST");
            conn.setDoOutput(true);
            conn.setDoInput(true);


            OutputStream os = conn.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
            osw.write("{" +
                    "\"accountNumber\": \""+ accountNumber +"\"," +
                    "\"accountCurrency\": \"true\"," +
                    "\"includeBalance\": \"true\"," +
                    "\"includeExtBalance\": \"true\"," +
                    "\"includeInterest\": \"true\"" +
                    "}");
            osw.flush();
            osw.close();
            os.close();  //don't forget to close the OutputStream
            conn.connect();

            //read the inputstream and print it
            String result;
            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            int result2 = bis.read();
            while(result2 != -1) {
                buf.write((byte) result2);
                result2 = bis.read();
            }
            result = buf.toString();
            System.out.println(result);

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            } else {
                System.out.println("Response code = "+ conn.getResponseCode());
                System.out.println("Response code = "+ conn.getResponseMessage());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }
            conn.disconnect();
        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return balance;
    }

    public static String generateRqUID() {
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssSSS");
        return sdf.format(dt) + (int) (Math.random()*100000);
    }

}
