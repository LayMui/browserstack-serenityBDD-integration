package demo.stepdefinitions;

//import com.applitools.eyes.BatchInfo;
//import com.applitools.eyes.FileLogger;
//import com.applitools.eyes.TestResultsSummary;
//import com.applitools.eyes.selenium.BrowserType;
//import com.applitools.eyes.selenium.Configuration;
//import com.applitools.eyes.selenium.Eyes;
//import com.applitools.eyes.selenium.fluent.Target;
//import com.applitools.eyes.visualgrid.model.DeviceName;
//import com.applitools.eyes.visualgrid.model.ScreenOrientation;
//import com.applitools.eyes.visualgrid.services.VisualGridRunner;
//import net.thucydides.core.webdriver.WebDriverFacade;
//import net.serenitybdd.core.Serenity;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import demo.pageobjects.LoginPage;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import net.thucydides.core.annotations.Steps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;

public class LoginStepDefinitions {
  @Steps
  LoginPage loginPage;
  @Steps
  CheckUtility checkUtility;
  @Steps
  RoutingUtility routingUtility;

//  VisualGridRunner runner;
//  Eyes eyes;

  private final Logger log = LoggerFactory.getLogger(LoginStepDefinitions.class);


  /*
  private static void setUp(Eyes eyes) {

    // Initialize the eyes configuration.
    Configuration config = new Configuration();

    // Add this configuration if your tested page includes fixed elements.
    //config.setStitchMode(StitchMode.CSS);

    // You can get your api key from the Applitools dashboard
    // https://applitools.com/docs/api/eyes-sdk/classes-gen/class_eyes/method-eyes-setapikey-selenium-java.html

    // set new batch
    config.setBatch(new BatchInfo("Demo batch"));
   // Add browsers with different viewports
    config.addBrowser(800, 600, BrowserType.CHROME);
    config.addBrowser(700, 500, BrowserType.FIREFOX);
    config.addBrowser(1600, 1200, BrowserType.IE_11);
    config.addBrowser(1024, 768, BrowserType.EDGE_CHROMIUM);
    config.addBrowser(800, 600, BrowserType.SAFARI);
    // Add mobile emulation devices in Portrait mode
    config.addDeviceEmulation(DeviceName.iPhone_X, ScreenOrientation.PORTRAIT);
    config.addDeviceEmulation(DeviceName.Pixel_2, ScreenOrientation.PORTRAIT);
    // set the configuration to eyes
    eyes.setConfiguration(config);
  }
*/

  @Before(value = "@login or @noLogin")
  public void setTheStage() {

//    if (System.getenv("APPLITOOLS_API_KEY") != null) {
//      runner = new VisualGridRunner(1);
//      // Initialize the eyes SDK
//      eyes = new Eyes(runner);
//      setUp(eyes);
//      //eyes.setLogHandler(new StdoutLogHandler(true));
//      eyes.setLogHandler(new FileLogger("file.log", true, true));
//    }
    OnStage.setTheStage(new OnlineCast()); }

  @After(value = "@login or @noLogin")
  public void drawTheCurtain() {
//    if (System.getenv("APPLITOOLS_API_KEY") != null) {
//      eyes.closeAsync();
//      // Get test results from Eyes
//      TestResultsSummary myTestResults = runner.getAllTestResults(false);
//      // Push test results into Serenity report
//      Serenity.recordReportData().withTitle("Applitools Report").andContents(myTestResults.toString());
//    }

    OnStage.drawTheCurtain(); }

  @Given("^(.*) is at the url page$")
  public void jamesIsAtTheUrlPage(String actor) {
    theActorCalled(actor).attemptsTo(Open.browserOn().the(loginPage));

//    if (System.getenv("APPLITOOLS_API_KEY") != null) {
//      eyes.open(((WebDriverFacade) loginPage.getDriver()).getProxiedDriver(),
//              "DEMO", "DEMO Ultra Grid");
//      eyes.check(Target.window().fully().withName("App Page"));
//    }
  }


  @When("^(.*) login with username \"([^\"]*)\" and password \"([^\"]*)\"")
  public void jamesLoginInWithUsernameAndPassword(String actor, String username, String password) {
    routingUtility.actorLogin(username, password);
//    if (System.getenv("APPLITOOLS_API_KEY") != null)
//      eyes.check(Target.window().fully().withName("Logging page"));
  }

  @Then("^(?:.*) is able to login$")
  public void jamesIsAbleToLogin() {
    checkUtility.loginToDashboard();
  }

  @Then("^(?:.*) is unable to login$")
  public void jamesIsUnableToLogin() {
    checkUtility.unableToLogin();
  }
}