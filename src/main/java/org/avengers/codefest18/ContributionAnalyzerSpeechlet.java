package org.avengers.codefest18;

import static com.amazon.speech.speechlet.SpeechletResponse.newAskResponse;
import static com.amazon.speech.speechlet.SpeechletResponse.newTellResponse;

import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.*;
import com.amazon.speech.ui.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

        String speechText = "<speak>I'm sorry, but we could not process your request right now.</speak>";
        String cardText = "I'm sorry, but we could not process your request right now.";

        String amountValue = slot.getValue();
        Double amount = null;

        try {
            amount = Double.parseDouble(amountValue);
        }
        catch (NumberFormatException nfe) {
            speechText = "<speak>I'm sorry but I could not understand " + amountValue + ". We could not process your request.  Please try again.</speak>";
            cardText = "I'm sorry but I could not understand " + amountValue + ". We could not process your request.  Please try again.";
            return buildSsmlTellResponse(speechText, "Edward Jones Contribution Analyzer", cardText);
        }

//        String urlStr = "http://gateway.ejcodefest2016.com:8080/gateway/balance/";
//        String response = callRestService(urlStr);
//        if (response != null && !response.isEmpty()) {
//            JSONObject obj = new JSONObject(response);
//            obj.getJSONObject("before");
//            int acctBalance = obj.getInt("acctBalance");
        float beforeOverallSuccessRate = .632562149f;
        List<GoalResult> before = new ArrayList<>();
        GoalResult beforeRetirement = new GoalResult("Retirement", .4087134f, 0);
        GoalResult beforeEducation = new GoalResult("Education", .843715f, 0);
        GoalResult beforePurchaseBoat = new GoalResult("Purchase Boat", .16754f, 0);
        before.add(beforeRetirement);
        before.add(beforeEducation);
        before.add(beforePurchaseBoat);

        float afterOverallSuccessRate = .6861341f;
        List<GoalResult> after = new ArrayList<>();
        GoalResult afterRetirement = new GoalResult("Retirement", .4387134f, 300);
        GoalResult afterEducation = new GoalResult("Education", .873715f, 100);
        GoalResult afterPurchaseBoat = new GoalResult("Purchase Boat", .20754f, 200);
        after.add(afterRetirement);
        after.add(afterEducation);
        after.add(afterPurchaseBoat);

        StringBuilder speechTextBuilder = new StringBuilder();
        speechTextBuilder.append("<speak>We recommend you distribute your <say-as interpret-as=\"unit\">$");
        speechTextBuilder.append(amount);
        speechTextBuilder.append("</say-as> as follows. ");
        for (GoalResult result : after) {
            speechTextBuilder.append("Apply <say-as interpret-as=\"unit\">$");
            speechTextBuilder.append(result.getContributionAmount());
            speechTextBuilder.append("</say-as> to ");
            speechTextBuilder.append(result.getName());
            speechTextBuilder.append(". Your goal success rate will move from <say-as interpret-as=\"unit\">");
            float newRate = ((float)Math.round(result.getSuccessRate() * 10000))/100;
            GoalResult beforeResult = null;
            for (GoalResult bResult : before) {
                if (bResult.getName().equals(result.getName())) {
                    beforeResult = bResult;
                }
            }
            float oldRate = ((float)Math.round(beforeResult.getSuccessRate() * 10000))/100;
            speechTextBuilder.append(oldRate);
            speechTextBuilder.append("%</say-as> to <say-as interpret-as=\"unit\">");
            speechTextBuilder.append(newRate);
            speechTextBuilder.append("%</say-as>. ");
        }
        speechTextBuilder.append("You're overall success rate moves from <say-as interpret-as=\"unit\">");
        float newRate = ((float)Math.round(afterOverallSuccessRate * 10000))/100;
        float oldRate = ((float)Math.round(beforeOverallSuccessRate * 10000))/100;
        speechTextBuilder.append(oldRate);
        speechTextBuilder.append("%</say-as> to <say-as interpret-as=\"unit\">");
        speechTextBuilder.append(newRate);
        speechTextBuilder.append("%</say-as>. ");
        speechTextBuilder.append("</speak>");
        speechText = speechTextBuilder.toString();
        cardText = "We recommend you distribute you funds to your different goals.";
//        speechText = "<speak>Your balance is <say-as interpret-as=\"unit\">" + acctBalance + "</say-as></speak>";
//        cardText = "Your balance is " + acctBalance;
//        }

        return buildSsmlTellResponse(speechText, "Edward Jones Contribution Analyzer", cardText);
    }

    private SpeechletResponse getUnknownResponse() {
        String speechText = "I didn't understand your request. You can tell me how much you would like to contribute.  Say I would like to contribute and then an amount.";

        return buildPlainTellResponse(speechText, "Edward Jones", speechText, false);
    }

    private SpeechletResponse getStopResponse() {
        String speechText = "Thank you. If you need other assistance please call your branch. Have a nice day.";

        return buildPlainTellResponse(speechText, "Edward Jones", speechText, true);
    }

    private SpeechletResponse getHelpResponse() {
        String speechText = "You can tell me how much you would like to contribute. Say I would like to contribute and then an amount.";

        return buildAskResponse(speechText, "Edward Jones Help", speechText);
    }

    private String callRestService(String urlStr) {
        String result = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                                                   + conn.getResponseCode());
            }

            StringWriter sw = new StringWriter();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
//            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                sw.write(output);
//                System.out.println(output);
            }
            result = sw.getBuffer()
                    .toString();

            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
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
