package org.avengers.codefest18;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

import java.util.HashSet;
import java.util.Set;

public class ContributionAnalyzerSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {
    private static final Set<String> supportedApplicationIds = new HashSet<>();
    static {
        /*
         * This Id can be found on https://developer.amazon.com/edw/home.html#/ "Edit" the relevant
         * Alexa Skill and put the relevant Application Ids in this Set.
         */
        supportedApplicationIds.add("amzn1.ask.skill.a47e4b77-90dd-4201-a3ec-dfe4c4864f64");
    }

    public ContributionAnalyzerSpeechletRequestStreamHandler() {
        super(new ContributionAnalyzerSpeechlet(), supportedApplicationIds);
    }
}
