package com.alexa.lambda.townhall;

import com.alexa.lambda.townhall.handler.*;
import com.amazon.ask.Skill;
import com.amazon.ask.Skills;
import com.amazon.ask.SkillStreamHandler;

import javax.net.ssl.*;
import java.io.InputStream;
import java.security.KeyStore;

public class HelloWorldStreamHandler extends SkillStreamHandler {

    private static Skill getSkill() {
        return Skills.standard()
                .addRequestHandlers(
                        new CancelandStopIntentHandler(),
                        new HelloWorldIntentHandler(),
                        new GetBalanceAmountIntentHandler(),
                        new ConfirmAccountNumberIntentHandler(),
                        new HelpIntentHandler(),
                        new LaunchRequestHandler(),
                        new SessionEndedRequestHandler())
                .build();
    }

    public HelloWorldStreamHandler() {
        super(getSkill());

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream keyStoreInput = classLoader.getResourceAsStream("keystore.jks");
        InputStream trustStoreInput = classLoader.getResourceAsStream("keystore.jks");

        if (keyStoreInput == null || trustStoreInput == null) {
            System.err.println("Fail to initialize key/trust store");
        }

        try {
            initSSLStore(keyStoreInput, LambdaConstants.storePass, trustStoreInput, LambdaConstants.storePass);
            keyStoreInput.close();
            trustStoreInput.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    private static void initSSLStore(InputStream keyStream, String keyStorePassword, InputStream trustStream, String trustStorePassword) throws Exception
    {
        System.out.println("Initializing Keystore");

        // Get keyStore
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

        // if your store is password protected then declare it (it can be null however)
        char[] keyPassword = keyStorePassword.toCharArray();

        // load the stream to your store
        keyStore.load(keyStream, keyPassword);

        // initialize a trust manager factory with the trusted store
        KeyManagerFactory keyFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyFactory.init(keyStore, keyPassword);
        // get the trust managers from the factory
        KeyManager[] keyManagers = keyFactory.getKeyManagers();

        // Now get trustStore
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());

        // if your store is password protected then declare it (it can be null however)
        char[] trustPassword = trustStorePassword.toCharArray();

        // load the stream to your store
        trustStore.load(trustStream, trustPassword);

        // initialize a trust manager factory with the trusted store
        TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustFactory.init(trustStore);

        // get the trust managers from the factory
        TrustManager[] trustManagers = trustFactory.getTrustManagers();

        // initialize an ssl context to use these managers and set as default
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustManagers, null);
        SSLContext.setDefault(sslContext);
        System.out.println("Keystore initialization success!");
    }

}