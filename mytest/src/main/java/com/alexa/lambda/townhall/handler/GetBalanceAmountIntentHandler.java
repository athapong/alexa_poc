package com.alexa.lambda.townhall.handler;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.amazon.ask.request.Predicates;

import java.util.Optional;

public class GetBalanceAmountIntentHandler implements RequestHandler {

    @Override
    public boolean canHandle(HandlerInput input) {
        System.out.println(">> in GetBalanceAmountIntentHandler.handle()");
        return input.matches(Predicates.intentName("get_balance_amount"));
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {
        System.out.println("get balance input: " + input.getRequest().toString());
        String speechText = "Yes, please confirm your account number";
        return input.getResponseBuilder()
                .withSpeech(speechText)
                .withSimpleCard("Next step - confirm account number", speechText)
                .withReprompt("Could you please confirm your account number?")
                .build();
    }

}
