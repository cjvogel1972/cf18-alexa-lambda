package org.avengers.codefest18;

import static com.amazon.speech.speechlet.SpeechletResponse.newAskResponse;
import static com.amazon.speech.speechlet.SpeechletResponse.newTellResponse;

import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.*;
import com.amazon.speech.ui.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ContributionAnalyzerSpeechlet implements Speechlet {

    private static Logger log = LoggerFactory.getLogger(ContributionAnalyzerSpeechlet.class);

    @Override
    public void onSessionStarted(SessionStartedRequest request, Session session) throws SpeechletException {
        log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(),
                 session.getSessionId());
    }

    @Override
    public SpeechletResponse onLaunch(LaunchRequest request, Session session) throws SpeechletException {
        log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(),
                 session.getSessionId());

        return getWelcomeResponse();
    }

    @Override
    public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
        log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
                 session.getSessionId());

        String intentName = request.getIntent()
                .getName();
        log.debug("invoking intent:{}", intentName);

        SpeechletResponse response;
        switch (intentName) {
            case "ComputeContributionsIntent":
                response = computeContributions(request);
                break;
            case "AMAZON.HelpIntent":
                response = getHelpResponse();
                break;
            case "AMAZON.StopIntent":
                response = getStopResponse();
                break;
            default:
                response = getUnknownResponse();
        }

        return response;
    }

    @Override
    public void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {
        log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(),
                 session.getSessionId());
    }

    private SpeechletResponse getWelcomeResponse() {
        String speechText = "Welcome to the Edward Jones contribution analyzer. How much is the contribution you would "
                + "like to analyze?";

        return buildPlainTellResponse(speechText, "Welcome to Edward Jones", speechText, false);
    }

    private SpeechletResponse computeContributions(final IntentRequest request) {
        Slot slot = request.getIntent()
                .getSlot("amount");

        String amountValue = slot.getValue();
        final Double amount;

        try {
            amount = Double.parseDouble(amountValue);
        } catch (NumberFormatException nfe) {
            String speechText = "<speak>I'm sorry but I could not understand " + amountValue + ". We could not process your request.  Please try again.</speak>";
            String cardText = "I'm sorry but I could not understand " + amountValue + ". We could not process your request.  Please try again.";
            return buildSsmlTellResponse(speechText, "Edward Jones Contribution Analyzer", cardText);
        }

        AnalyzerUtil util = new AnalyzerUtil();
        Optional<ContributionAnalysisSummary> contributionAnalysisSummary = util.callAnalysisService(amount);
        String speechText = contributionAnalysisSummary.map(summary -> buildAnalysisSpeech(amount, summary))
                .orElse("<speak>I'm sorry, but we could not analyze the contribution at this time.  Please try again later.</speak>");

        String cardText = "We recommend you distribute you funds to your different goals.";

        return buildSsmlTellResponse(speechText, "Edward Jones Contribution Analyzer", cardText);
    }

    private String buildAnalysisSpeech(Double amount, ContributionAnalysisSummary summary) {
        StringBuilder speechTextBuilder = new StringBuilder();
        speechTextBuilder.append("<speak>We recommend you distribute your <say-as interpret-as=\"unit\">$");
        speechTextBuilder.append(amount);
        speechTextBuilder.append("</say-as> as follows. ");
        for (GoalResult result : summary.getAfter()
                .getGoalResults()) {
            speechTextBuilder.append("Apply <say-as interpret-as=\"unit\">$");
            speechTextBuilder.append(result.getContributionAmount());
            speechTextBuilder.append("</say-as> to ");
            speechTextBuilder.append(result.getName());
            speechTextBuilder.append(". Your goal success rate will move from <say-as interpret-as=\"unit\">");
            float newRate = computePercentage(result.getSuccessRate());
            GoalResult beforeResult = null;
            for (GoalResult bResult : summary.getBefore()
                    .getGoalResults()) {
                if (bResult.getName()
                        .equals(result.getName())) {
                    beforeResult = bResult;
                }
            }
            float oldRate = computePercentage(beforeResult.getSuccessRate());
            speechTextBuilder.append(oldRate);
            speechTextBuilder.append("%</say-as> to <say-as interpret-as=\"unit\">");
            speechTextBuilder.append(newRate);
            speechTextBuilder.append("%</say-as>. ");
        }
        speechTextBuilder.append("You're overall success rate moves from <say-as interpret-as=\"unit\">");
        float newRate = computePercentage(summary.getAfter()
                                                  .getOverallSuccessRate());
        float oldRate = computePercentage(summary.getBefore()
                                                  .getOverallSuccessRate());
        speechTextBuilder.append(oldRate);
        speechTextBuilder.append("%</say-as> to <say-as interpret-as=\"unit\">");
        speechTextBuilder.append(newRate);
        speechTextBuilder.append("%</say-as>. ");
        speechTextBuilder.append("</speak>");

        return speechTextBuilder.toString();
    }

    private float computePercentage(float originalValue) {
        return ((float) Math.round(originalValue * 10000)) / 100;
    }

    private SpeechletResponse getUnknownResponse() {
        String speechText = "I didn't understand your request. You can tell me how much you would like to contribute.  Say I would like to contribute and then an amount.";

        return buildPlainTellResponse(speechText, "Edward Jones", speechText, false);
    }

    private SpeechletResponse getStopResponse() {
        String speechText = "This was dope.  Thank you. If you need other assistance please call your branch. Have a nice day.";

        return buildPlainTellResponse(speechText, "Edward Jones", speechText, true);
    }

    private SpeechletResponse getHelpResponse() {
        String speechText = "You can tell me how much you would like to contribute. Say I would like to contribute and then an amount.";

        return buildAskResponse(speechText, "Edward Jones Help", speechText);
    }

    private SpeechletResponse buildPlainTellResponse(String speechText, String cardTitle, String cardText,
                                                     boolean endSession) {
        return buildPlainTellResponse(speechText, cardTitle, cardText, speechText, endSession);
    }

    private SpeechletResponse buildPlainTellResponse(String speechText, String cardTitle, String cardText,
                                                     String repromptText, boolean endSession) {
        Card card = createSimpleCard(cardTitle, cardText);
        OutputSpeech speech = createPlainTextOutputSpeech(speechText);
        Reprompt reprompt = createReprompt(createPlainTextOutputSpeech(repromptText));

        SpeechletResponse response = newTellResponse(speech, card);
        response.setReprompt(reprompt);
        response.setNullableShouldEndSession(endSession);
        return response;
    }

    private SpeechletResponse buildSsmlTellResponse(String speechText, String cardTitle, String cardText) {
        Card card = createSimpleCard(cardTitle, cardText);
        OutputSpeech speech = createSsmlOutputSpeech(speechText);

        SpeechletResponse response = newTellResponse(speech, card);
        response.setNullableShouldEndSession(false);
        return response;
    }

    private SpeechletResponse buildAskResponse(String speechText, String cardTitle, String cardText) {
        Card card = createSimpleCard(cardTitle, cardText);
        OutputSpeech speech = createPlainTextOutputSpeech(speechText);
        Reprompt reprompt = createReprompt(speech);

        return newAskResponse(speech, reprompt, card);
    }

    private Card createSimpleCard(String title, String speechText) {
        SimpleCard card = new SimpleCard();
        card.setTitle(title);
        card.setContent(speechText);

        return card;
    }

    private OutputSpeech createPlainTextOutputSpeech(String speechText) {
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        return speech;
    }

    private OutputSpeech createSsmlOutputSpeech(String ssmlText) {
        SsmlOutputSpeech speech = new SsmlOutputSpeech();
        speech.setSsml(ssmlText);

        return speech;
    }

    private Reprompt createReprompt(OutputSpeech speech) {
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);

        return reprompt;
    }
}
