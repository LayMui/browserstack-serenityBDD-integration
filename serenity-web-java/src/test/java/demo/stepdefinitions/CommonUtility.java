 package demo.stepdefinitions;

import com.google.gson.Gson;
import demo.dto.*;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import net.serenitybdd.screenplay.rest.interactions.Delete;
import net.serenitybdd.screenplay.rest.interactions.Get;
import net.serenitybdd.screenplay.rest.interactions.Post;
import net.serenitybdd.screenplay.rest.interactions.Put;
import net.thucydides.core.util.EnvironmentVariables;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;

 public class CommonUtility {
    
     EnvironmentVariables environmentVariables;
     private String theRestApiBaseUrl;
     private String bearerToken;
     Actor james;

     private final Logger log = LoggerFactory.getLogger(CommonUtility.class);

     public String getEncodedPassword(String username, String password) {
         String auth = username + ":" + password;
         byte[] encodedAuth = Base64.getEncoder().encode(
                 auth.getBytes(Charset.forName("US-ASCII")));
         return new String(encodedAuth);
     }

     public List<String> getValuesForGivenKey(String jsonArrayStr, String key) {
         JSONArray jsonArray = new JSONArray(jsonArrayStr);
         return IntStream.range(0, jsonArray.length())
                 .mapToObj(index -> ((JSONObject) jsonArray.get(index)).optString(key))
                 .collect(Collectors.toList());
     }

     public String setUpRestBaseUrl() {
         theRestApiBaseUrl =
                 environmentVariables
                         .optionalProperty("restapi.baseurl")
                         .orElse("https://xxxx.io");

         log.info("theRestApiBaseUrl: " + theRestApiBaseUrl);

         globalAdminUsername = "dummy";
         globalAdminPassword = "dummy";
         james = Actor.named("James the global admin").whoCan(CallAnApi.at(theRestApiBaseUrl));
         bearerToken = globalAdminAuthentication(james, globalAdminUsername, globalAdminPassword);

         log.info("BEARER TOKEN: " + bearerToken);
         return theRestApiBaseUrl;
     }

     
 }

