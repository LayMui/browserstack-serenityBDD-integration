package demo.tasks;

import demo.pageobjects.LoginPage;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;


import net.serenitybdd.screenplay.actions.Clear;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Enter;


public class EnterLoginDetails {

  public static Performable forAdminUser(String username, String password) {
    return Task.where(
            "{0} attempts to enter #username and #password",
            Clear.field(LoginPage.USERNAME_FIELD),
            Enter.theValue(username).into(LoginPage.USERNAME_FIELD),
            Clear.field(LoginPage.PASSWORD_FIELD),
            Enter.theValue(password).into(LoginPage.PASSWORD_FIELD),
            Click.on(LoginPage.LOGIN_BUTTON))
        .with("username")
        .of(username)
        .with("password")
        .of(password);
  }
}
