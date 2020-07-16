package demo.pageobjects;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.screenplay.targets.Target;
import net.thucydides.core.annotations.DefaultUrl;
import org.openqa.selenium.By;

@DefaultUrl("https://xxx.io/#/")
public class LoginPage extends PageObject {

  public static final Target USERNAME_FIELD = Target.the("username").located(By.id("username"));
  public static final Target PASSWORD_FIELD = Target.the("password").located(By.id("password"));
  public static final Target LOGIN_BUTTON =
      Target.the("login").locatedBy("css:button[data-qa='login-btn']");
  public static final Target ERROR =
      Target.the("error").locatedBy("//div[contains(text(), 'Incorrect username or password')]");
}
