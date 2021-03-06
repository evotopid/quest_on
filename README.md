# Introduction
Quest_on is a web application allowing tailored to collecting advanced scientific online surveys.
It provides features like time oriented surveying which are not frequently found in commercial
competitior's products.

# Running quest_on
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

If you want to have the server restart everytime there are modifications to any of the code, run `~jetty:start` from within SBT.

# Administration
## Admin Area
You can perform all relevant operations from the web-based admin area.
Note that by default registration for new admins is enabled, however you might want to disable this for
security reasons. To do this edit the file `/src/main/resources/config.yaml`.

Reports can be downloaded in the XLSX format, allowing for easy integration with many office softwares.
At this time it is recommended to perform periodic downloads of your reports data as a safety premeasure against data loss.

## Survey Editing
There is an experimental graphical editor for surveys available in the admin area. Please be sure to save regularily and perform offline backups of both surveys and reports to prevent data loss.

It's also possible to directly edit the [JSON](https://en.wikipedia.org/wiki/JSON) format. For further information on the format or the different options available from the graphical editor, please consult [this reference](https://github.com/evotopid/quest_on/blob/master/docs/survey_json.md).

Shall you end up with an invalid survey and unable to figure out your mistake, feel free to open an issue with both your survey's JSON and validator output.

## Images
In the survey it's possible to reference images. You only have to provide the filename in the survey file.
However you have to either upload the images or register their location on the internet in the images section in the
admin area.

# License
Check out the LICENSE file.
