# Introduction
Quest_on is a web application allowing tailored to collecting advanced scientific online surveys.
It provides features like time oriented surveying which are not frequently found in commercial
competitior's products.

# Running Locally
## Hosted at Heroku
The free tier of Heroku allows you to run your own instance of quest_on for your purposes.
You might want to fork this repository to have direct control over the hosted code, please
note however that receiving updates will require further intervention of you (ie. pushing changes to Heroku via git).

All you need to do is press the following button and proceed with the procedure.

[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy)

## Locally (for development or your own server)
You need to have a recent Java SDK (ie. version 8) and Scala SBT installed.
From within a terminal navigate to the directory the application is located and start the server with:

```sh
$ cd quest_on
$ ./sbt
> jetty:start
> browse
```

If `browse` doesn't launch your browser, manually open [http://localhost:8080/](http://localhost:8080/) in your browser.

# Administration
## Admin Area
You can perform all relevant operations from the web-based admin area.
Note that by default registration for new admins is enabled, however you might want to disable this for 
security reasons. To do this edit the file `/src/main/resources/config.yaml`.

Reports can be downloaded in the XLSX format, allowing for easy integration with many office softwares.
At this time it is recommended to perform periodic downloads of your reports data as a safety premeasure against data loss.

## Survey Editing
Currently a visual editor for surveys is not implemented yet.
However it's possible to directly edit a survey's [JSON](https://en.wikipedia.org/wiki/JSON) representation.

As it's possible to make mistakes easily there is survey validation built into the admin dashboard.
In case you are stuck feel free to open an issue with your survey JSON and validation output.

Check [this reference](https://github.com/evotopid/quest_on/blob/master/docs/survey_json.md) for further information on how to write a survey.

## Images
In the survey it's possible to reference images. You only have to provide the filename in the survey file.
However you have to either upload the images or register their location on the internet in the images section in the
admin area.

# License
Check out the LICENSE file.
