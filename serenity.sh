#!/usr/local/bin/bash

display_usage() {
  echo "This script must be run with at least 2 arguments if task is run_tests, the 3rd arg is mandatory, if framework is web, the 4th arg is mandatory"
 	echo -e "Usage:" $0 "[rest|web] [sync|run_tests|update_features|push_results] [local|dev|qa|staging|prod] [local|remote|cloud] [chrome|firefox|ie|opera]"
	}

if [  $# -lt 2 ]
	then
		display_usage
		exit 1
	fi

FRAMEWORK=$1
TASK=$2
ENV=$3

if [ "$FRAMEWORK" != "rest" ] &&  [ "$FRAMEWORK" != "web" ]; then
  display_usage
  exit 1
fi

if [ "$TASK" != "sync" ] &&  [ "$TASK" != "update_features" ] && [ "$TASK" != "run_tests" ] && [ "$TASK" != "push_results" ]; then
  display_usage
  exit 1
fi

if [ "$TASK" = "run_tests" ]; then
  case $FRAMEWORK in
     web)
       NUM_ARGS=4
       ;;
     *)
       NUM_ARGS=3
       ;;
  esac

  if [  $# -lt $NUM_ARGS ]
	then
		  display_usage
		  exit 1
	fi

	if [ "$ENV" != "qa" ] && [ "$ENV" != "dev" ] && [ "$ENV" != "staging" ] && [ "$ENV" != "prod" ] && [ "$ENV" != "local" ] ; then
    display_usage
    exit 1
  fi
fi

MODE=$4
BROWSER=$5

SERENITY_FOLDER=serenity-"$FRAMEWORK"-java
if [ "$FRAMEWORK" = "rest" ]; then
  TEST_RUN_ID=$TEST_RUN_ID_REST
else
  TEST_RUN_ID=$TEST_RUN_ID_WEB
fi

ENV_PARAMETER_WEB="-Denvironment=$ENV"
case $ENV in
   local)
    ENV_PARAMETER_REST="-Drestapi.baseurl=https://demo.dev.io/api"
     ;;
    *)
    ENV_PARAMETER_REST="-Drestapi.baseurl=https://$ENV.demo.io/api"
    ;;
esac

WEB_TAG="@web"
REST_TAG="@rest"

if [ "$TEST_TAG" != "" ]; then
  WEB_TAG=$TEST_TAG
  REST_TAG=$TEST_TAG
fi

set_variables() {

  if [ "$API_UID" = "" ] ||
  [ "$API_ACCESS_TOKEN" = "" ] ||
  [ "$API_CLIENT" = "" ] ||
  [ "$PROJECT_ID" = "" ] ||
  [ "$TEST_RUN_ID_WEB" = "" ] ||
  [ "$TEST_RUN_ID_REST" = "" ]; then
    echo "Set the following Environment Variables"
    echo "API_UID, API_ACCESS_TOKEN, API_CLIENT"
    echo "PROJECT_ID, TEST_RUN_ID_WEB, TEST_RUN_ID_REST"
  fi
}

synchronise() {
  if [ -f project_scenarios.json ]; then
    rm -Rf project_scenarios.json
  fi

  if [ -f testrun_scenarios.json ]; then
    rm -Rf testrun_scenarios.json
  fi

 echo "Synchronize a test run"
 curl -sS -XPOST "https://studio.cucumber.io/api/projects/$PROJECT_ID/test_runs/$TEST_RUN_ID/synchronize" \
  -H "accept: application/vnd.api+json; version=1" \
  -H "access-token: $API_ACCESS_TOKEN" \
  -H "uid: $API_UID" \
  -H "client: $API_CLIENT"

 echo "Getting all scenarios from a project"
 response=$(curl -sS "https://studio.cucumber.io/api/projects/$PROJECT_ID/scenarios" \
  -H "accept: application/vnd.api+json; version=1" \
  -H "access-token: $API_ACCESS_TOKEN" \
  -H "uid: $API_UID" \
  -H "client: $API_CLIENT" \
  -o project_scenarios.json \
  -w %{http_code})

 if [[ $? -eq 0 && $response =~ ^2 ]]; then
  readarray -t project_arr < <(jq -r '.data[].id' project_scenarios.json)

  echo "Getting all scenarios from a test run"
  response=$(curl -sS "https://studio.cucumber.io/api/projects/$PROJECT_ID/test_runs/$TEST_RUN_ID/test_snapshots?include=scenario" \
    -H "accept: application/vnd.api+json; version=1" \
    -H "access-token: $API_ACCESS_TOKEN" \
    -H "uid: $API_UID" \
    -H "client: $API_CLIENT" \
    -o testrun_scenarios.json \
    -w %{http_code})

  if [[ $? -eq 0 && $response =~ ^2 ]]; then
    readarray -t testrun_arr < <(jq -r '.data[].relationships.scenario.data.id' testrun_scenarios.json)

    echo "Comparing collections..."
    for scenarioId in "${project_arr[@]}"
    do
      if [[ ! " ${testrun_arr[@]} " =~ " ${scenarioId} " ]]; then
        echo "Adding missing scenarios to existing test run"
        curl -sS -XPATCH https://studio.cucumber.io/api/projects/$PROJECT_ID/test_runs/$TEST_RUN_ID \
           -H "accept: application/vnd.api+json; version=1" \
           -H "access-token: $API_ACCESS_TOKEN" \
           -H "uid: $API_UID" \
           -H "client: $API_CLIENT" \
           --data '{"data": {"type": "test_runs", "id": '"$TEST_RUN_ID"', "attributes": {"scenario_id": '"$scenarioId"'}}}'
        echo ""
        sleep 5
      fi
    done
    echo "Comparison complete"
  else
    echo "ERROR: Failed to retrieve scenarios from testrun, response code received was $response"
  fi
else
  echo "ERROR: Failed to retrieve scenarios from project, response code received was $response"
fi

 rm -Rf project_scenarios.json
 rm -Rf testrun_scenarios.json
}

case $TASK in
   sync)
     set_variables
     synchronise
     ;;
   update_features)
        echo "Update Feature files from Cucumber Studio with test run id $TEST_RUN_ID"
        rm -fr  $SERENITY_FOLDER/src/test/resources/features/*
        docker run --rm \
        -v ${PWD}:/app hiptest/hiptest-publisher \
        --config-file $SERENITY_FOLDER/hiptest-publisher.conf --test-run-id $TEST_RUN_ID --only=features
      ;;
    run_tests)
        echo "Run Tests for $FRAMEWORK in $ENV"
        echo "WEB_TAG: $WEB_TAG"
        echo "REST_TAG: $REST_TAG"
        mvn -U -DskipTests=true clean install
        case $FRAMEWORK in
            rest)
              mvn -U -fn -pl serenity-rest-java clean verify -Dcucumber.filter.tags=$REST_TAG -Dserenity.project.name="Serenity-Rest" $ENV_PARAMETER_REST serenity:reports -Dserenity.reports=single-page-html
              ;;
            *)
              case "$MODE" in
                cloud)
                  echo "Running remotely on browserstack..."
                  mvn -U -fn -pl serenity-web-java clean verify $ENV_PARAMETER_WEB $ENV_PARAMETER_REST -Dcucumber.filter.tags=$WEB_TAG serenity:aggregate serenity:reports -Dserenity.reports=single-page-html -Dbrowserstack.buildName=3.0
                  ;;
                remote)
                  docker-compose up -d --remove-orphans
                  case $BROWSER in
                  firefox)
                    mvn -U -fn -pl serenity-web-java clean verify -Dheadless.mode=false -Dserenity.project.name="Serenity-Web" $ENV_PARAMETER_WEB -Dwebdriver.driver=remote -Dwebdriver.remote.url="http://localhost:4444/wd/hub" -Dwebdriver.remote.driver=firefox -Dwebdriver.driver.gecko=geckodriver -Dcucumber.filter.tags=$WEB_TAG serenity:aggregate serenity:reports -Dserenity.reports=single-page-html
                    ;;
                  opera)
                    mvn -U -fn -pl serenity-web-java clean verify -Dheadless.mode=false -Dserenity.project.name="Serenity-Web" $ENV_PARAMETER_WEB -Dwebdriver.driver=remote -Dwebdriver.remote.url="http://localhost:4444/wd/hub" -Dwebdriver.remote.driver=opera -Dwebdriver.driver.opera=operadriver -Dcucumber.filter.tags=$WEB_TAG serenity:aggregate serenity:reports -Dserenity.reports=single-page-html
                    ;;
                  *)
                    mvn -U -fn -pl serenity-web-java clean verify -Dheadless.mode=false -Dserenity.project.name="Serenity-Web" $ENV_PARAMETER_WEB  -Dwebdriver.driver=remote -Dwebdriver.remote.url="http://localhost:4444/wd/hub" -Dwebdriver.remote.driver=chrome -Dwebdriver.driver.chrome=chromedriver -Dchrome.switches=--incognito -Dcucumber.filter.tags=$WEB_TAG serenity:aggregate serenity:reports -Dserenity.reports=single-page-html
                    ;;
                  esac
                  ;;
              local)
                case $BROWSER in
                  firefox)
                    mvn -U -fn -pl serenity-web-java clean verify -Dheadless.mode=false -Dserenity.project.name="Serenity-Web" $ENV_PARAMETER_WEB $ENV_PARAMETER_REST -Dwebdriver.driver=firefox -Dwebdriver.driver.gecko=geckodriver -Dcucumber.filter.tags=$WEB_TAG serenity:aggregate serenity:reports -Dserenity.reports=single-page-html
                    ;;
                  ie)
                    mvn -U -fn -pl serenity-web-java clean verify -Dheadless.mode=false -Dserenity.project.name="Serenity-Web" $ENV_PARAMETER_WEB $ENV_PARAMETER_REST -Dwebdriver.driver=ie -Dwebdriver.ie.driver=IEDriverServer -Dcucumber.filter.tags=$WEB_TAG serenity:aggregate serenity:reports -Dserenity.reports=single-page-html
                    ;;
                  *)
                    mvn -U -fn -pl serenity-web-java clean verify -Dheadless.mode=false -Dserenity.project.name="Serenity-Web" $ENV_PARAMETER_WEB $ENV_PARAMETER_REST -Dwebdriver.driver=chrome -Dwebdriver.driver.chrome=chromedriver -Dchrome.switches=--incognito -Dcucumber.filter.tags=$WEB_TAG serenity:aggregate serenity:reports -Dserenity.reports=single-page-html
                    ;;
                esac
                ;;
              esac
        esac
        ;;
    push_results)
      echo "Push Test Result back to Cucumber Studio"
      docker run --rm \
        -v ${PWD}:/app hiptest/hiptest-publisher \
        hiptest-publisher --config-file $SERENITY_FOLDER/hiptest-publisher.conf --push="$SERENITY_FOLDER/target/site/serenity/SERENITY-JUNIT-*.xml"  --test-run-id $TEST_RUN_ID --push-format junit
      ;;
    *)
      display_usage
      exit 1
      ;;
esac