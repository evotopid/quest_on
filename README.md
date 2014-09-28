# Introduction
Quest_on is a web application which allows you to collect advanced surveys online.
Unlike many of its commercial competitors which are especially made for business use cases,
it tries to provide an interface to collect data for scientific research.

There aren't too many features yet and all the specifications of the project are subject to changes.
Please note that while this documentation is written in English the main text output of the webpage
is German. Localization for strings displayed to users will be added in a future release.


## TODO
- Organize the left Javascript in index.html
- Minimize all Javascript files into one for performance
- Preload images on all platforms (currently only works in Webkit)
- Add multiple export methods
- Improve Docs
- Interactive editor for survey.json files
- Service to host survey.json files online
- Make a real relase

# Server Configuration
Quest_on is a standalone ruby application which bundles it's own rack interface so it can be easily
ran on different hosting options (or self hosted).
To allow quest_on to function there are several environemnt variables which have to be set.
Currently the only export method for quest_on is to have an email sent with an "xlsx" file attached
as soon as there is a new survey report collected.
For this to work you have to set these environment variables:

- DESTINATION_EMAIL: The email address the results will be sent to.
- SOURCE_EMAIL: The email address the results will be sent from.
- SOURCE_DOMAIN: The domain you want the email to appear to be sent from.
- SMTP_ADDRESS: The address of the SMTP server. 
- SMTP_USERNAME: The username for the SMTP server.
- SMTP_PASSWORD: The password for the SMTP server.



# Survey Configuration
The survey is described in the file "pages.json". (Currently there is no visual editor available...)
The file follows the JSON standard which you should be somewhat familiar with.

If you want to see an actual example see the default page.json file, otherwise the documentation
of the page.json format follows:

The root element in the file is a list which lists all the different entries for the survey.
The first entry is going to be rendered first, and the last entry is going to be rendered last.
Each entry is an object and must have a field "type" which specifies the type of the entry.

Currently these types are available for top level entries: "page", "timelimit_start", "timelimit_end"


## "type":"page"
An entry with the type "page" represents a page in the survey.
The attribute "action" is a string value which represents which action will lead switching to the next page.
It can either be "user" which will basically display a "next" button and wait until the user decides to
click on that button, or it can be a time. The time has to be noted in the format: "Ns", e.g. "20s"

The value of "items" is an array of objects of which each has a value for the key "type".
All the items will be rendered in order when the page is displayed.
Following is an explanation for the different items which exist:

### textmessage
This is one of the most basic items. It just displays a text on the page. You can use this to display
informative messages, or to label input fields which don't have a label themselves.
You can use HTML for the content, just make sure you don't break your layout with it.
Example: {"type": "textmesage", "content": "Hello world!"}

### image
This item displays an image on the page.
All images in the folder web/img will be preloaded once the survey is opened in the browser.
To load an image you have to first place it into web/img and then you can just reference it by it's name.
Don't include the web/img in the path to the image, but if you place it into a subdirectory you need to have
that subdirectory in your path.
Full image path: web/img/an_image.jpg
Example: {"type": "image", "path": "an_image.jpg"}
Full image path: web/img/part1/another_image.png
Example: {"type": "image", "path": "part1/another_image.png"}

### multiplechoice
This is an item that expects user input. As such you need to assign it an unique id to label the value in the report.

This item displays multiple options of which the user can select only one.
Each of the options has a key which is the value of the answer that will be placed in the report,
and also has a value which is the description of the answer which will be only displayed but not saved.
Example: {"id": "question1", "type": "multiplechoice", "answers": {"1": "Yes", "2": "No", "3": "Maybe"}}

### textinput
This is an item that expects user input. As such you need to assign it an unique id to label the value in the report.

This item displays a text input field which can be filled with any text/string.
Example: {"type": "textinput", "id": "textfeld1"}


## "type":"timelimit_start"
This one starts a timelimit. A timelimit can wrap around multiple questions, please don't forget to close this one!
Otherwise there are going to be problems with your survey.
The only parameter this one needs is the "seconds" one which should be an integer.
The remaining time will be displayed and when the timelimit is reached a message will be displayed, after
which the user will jump to the next timelimit_end.


## "type":"timelimit_end"
End of a timelimit. See documentation for timelimit_start for more information.


# The end of the survey
At the end of the survey the last page (it's recommended to have a page display a message but not ask anything anymore) will stay and not be able to be clicked away anymore. (The action will be ignored if there is any)
All the data won't be sent to the server until the very end. If there was a failure sending the data the user will be shown a notice and be allowed to retry sending it.


