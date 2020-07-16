Feature: login
    In order to perform authentication to access the system
    As a user based on the role given James
    James want to login to the system

  Background:
    Given James is at the url page

  @noLogin @rest @web
  Scenario Outline: login with correct credentials (<hiptest-uid>)
    In order to perform authentication to access the system
    As a user based on the role given James
    James wants to login with correct credentials
    When James login with username "<username>" and password "<password>"
    Then James is able to login

    Examples:
      | username | password | hiptest-uid |
      | xxxx | xxxx | uid d0bbc47d-1636-46fe-a261-c5ba5c72cf0f |
     