package demo.stepdefinitions;

import demo.pageobjects.*;
import demo.tasks.*;
import net.serenitybdd.screenplay.actions.Scroll;
import net.serenitybdd.screenplay.matchers.WebElementStateMatchers;
import net.serenitybdd.screenplay.questions.WebElementQuestion;
import net.thucydides.core.annotations.Steps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;

import static net.serenitybdd.screenplay.EventualConsequence.eventually;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.*;
import static net.serenitybdd.screenplay.questions.WebElementQuestion.the;
import static org.hamcrest.Matchers.equalTo;

public class RoutingUtility {
   
    @Steps CheckUtility checkUtility;
    @Steps CommonUtility commonUtility;
    private final Logger log = LoggerFactory.getLogger(RoutingUtility.class);

    public void actorLogin(String username, String password)
    {
        theActorInTheSpotlight()
                .should(
                        eventually(
                                seeThat(
                                        WebElementQuestion.the(LoginPage.USERNAME_FIELD),
                                        WebElementStateMatchers.isVisible())));
        theActorInTheSpotlight()
                .should(
                        eventually(
                                seeThat(
                                        WebElementQuestion.the(LoginPage.PASSWORD_FIELD),
                                        WebElementStateMatchers.isVisible())));
        theActorInTheSpotlight().attemptsTo(EnterLoginDetails.forAdminUser(username, password));
    }
}
