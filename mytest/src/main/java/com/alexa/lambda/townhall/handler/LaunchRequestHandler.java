package com.alexa.lambda.townhall.handler;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Context;
import com.amazon.ask.model.LaunchRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.request.Predicates;

import java.util.Optional;

public class LaunchRequestHandler implements RequestHandler {
    @Override
    public boolean canHandle(HandlerInput input) {
        System.out.println(">> in canHandle()");
        return input.matches(Predicates.requestType(LaunchRequest.class));
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {
        System.out.println(">> in LaunchRequestHandler.handle()");
        String speechText = "Hi, Welcome to the SCB account assistant, Is there anything I can help you out!";

        return input.getResponseBuilder()
                .withSpeech(speechText)
                .withSimpleCard("Welcome to the SCB account assistant, what can I help you today?", speechText)
                .withReprompt(speechText)
                .build();
    }

    public String handleRequest(String input, Context context) {
        System.out.println("Welcome to SCB account assistant! executed with input: " + input);
        return input;
    }

}
