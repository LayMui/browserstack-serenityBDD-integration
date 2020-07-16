package demo.stepdefinitions;

import demo.pageobjects.*;
import net.serenitybdd.screenplay.actions.Scroll;
import net.serenitybdd.screenplay.matchers.WebElementStateMatchers;
import net.serenitybdd.screenplay.questions.WebElementQuestion;
import net.thucydides.core.annotations.Steps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static net.serenitybdd.screenplay.EventualConsequence.eventually;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.*;
import static net.serenitybdd.screenplay.questions.WebElementQuestion.the;

public class CheckUtility {
    @Steps
    CommonUtility commonUtility;
    private final Logger log = LoggerFactory.getLogger(CheckUtility.class);

    public void loginToDashboard() {
        theActorInTheSpotlight()
                .should(
                        eventually(
                                seeThat(
                                        WebElementQuestion.the(OrganizationManagementPage.HEADER_ORGANIZATION_ACTIVE),
                                        WebElementStateMatchers.isVisible())));
    }

    public void unableToLogin() {
        theActorInTheSpotlight().should(eventually(seeThat(
                WebElementQuestion.the(LoginPage.ERROR), WebElementStateMatchers.isVisible())));
    }

    