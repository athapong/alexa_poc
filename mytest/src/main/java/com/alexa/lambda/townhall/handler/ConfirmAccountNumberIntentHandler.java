package com.alexa.lambda.townhall.handler;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.amazon.ask.request.Predicates;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
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
            URL url = new URL("http://localhost:8080/mindtuit/execute?command="+accountNumber);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return balance;
    }

}
