As currently the only option to edit surveys in quest_on is to directly edit the JSON data this document
aims to give as much information as possible. It's supposed to be clearly understandable - if it's not please open a bug report.

Readers are expected to be familiar with basic elements of JSON before reading further.

It's recommended using a good programming text editor of your choice.
If you are unsure what to use you might want to try [Kate](https://kate-editor.org/) (runs on macOs, Windows & GNU/Linux).

**Table of Contents**  (*generated with [DocToc](http://doctoc.herokuapp.com/)*)

- [Survey Structure](#)
- [Page object](#)
	- [Last page](#)
	- [Timelimit](#)
- [Page Item](#)
	- [textmessage](#)
	- [textinput](#)
	- [image](#)
	- [multiplechoice](#)
- [Example Survey](#)

# Survey Structure
The root element of a survey is a JSON object.
It has a `title` attribute which is a string, and an attribute `pages` which is an array of page objects (described below).

Basically a survey looks like this:

```
{
    "title": "My awesome survey",
    "pages": [
        page_object_1_here,
        page_object_2_here,
        ...,
        last_page_object_here
    ]
}
```

# Page object
Each survey is structured into multiple pages.
Each page corresponds to a page of information that will be displayed to the survey participant.
It holds a list of items which are the elements that will be actually displayed on the page together.

Attributes:

| Name      | JSON Type      | Description |
| --------- | -------------- | ----------- |
| items     | array          | Array consisting of page items. They will be rendered in this order. See section page items for more information on the structure and kinds of supported items. |
| timelimit | object or null | If present (ie. not null) it has to be a timelimit as described below. |
| actions   | array          | Currently must be `["continue"]` for there to be a button to continue the survey. On the last page of the survey there shouldn't be next button as on that page the survey submit message will be displayed. Therefore on the last page this should be just `[]`. |

Basically a page looks like this:

```
{
    "items": [page_item1, page_item2, ..., page_item100],
    "timelimit": { "group": null, "seconds": 5, "timeoutnotice": false },
    "actions": ["continue"]
}
```

## Last page
As stated in the description of the ictions attribute the last page of the survey is intended
to display a last message but no part of the real survey anymore. If the transmission of results
fails the participant will be given an option to retry there. Use the last page to thank a participant or
something like that.

## Timelimit
Timelimits allow you to specify a maximum amount of time a user may spend on a page or group of page.

Attributes:

| Name          | JSON Type      | Description |
| ------------- | -------------- | ----------- |
| group         | string or null | If null it's a page level timelimit. If it's a string the timelimit will be grouped with other timelimits of the same name meaning that it only is started the first time a timelimit with that name is encountered and if the time is over all subsequent pages of this group will be skipped. |
| seconds       | numeric        | Maximum number of seconds a user might spend on the page (or group of pages). |
| timeoutnotice | boolean        | If true the user will be displayed a message when the time runs out and asked to continue to the next page. Otherwise the next page will be displayed without any further comment. |

# Page Item
Each page item has an attribute `type` which indicates the type of the item.
As each type of item has its own structure they are described individually.

Inputs need to have unique ids assigned to them so they can be identified in the report.

## textmessage
Text that will be displayed on the page. You can use this to display instructions or labels for inputs.
It's possible to enter HTML but be aware that bad input might break the page layout or even stop the survey from working.

Example:
```json
{ "type": "textmessage", "content": "This text will be displayed in the survey."}
```

## textinput
A blank text field a user should fill in. Provide a unique id so it can be identified in the report.

Example:
```json
{ "type": "textinput", "id": "textfield1" }
```

## image
Displays an image on the page.

Images specified in an image item have to be uploaded or linked through the admin area.
Given the images are accessible, they will be preloaded in the browser at the beginning
of the survey so they are surely available when needed. Make sure to reduce the image sizes
as much as acceptable as this will allow.

Example:
```json
{ "type": "image", "path": "filename.jpg" }
```

## multiplechoice
Displays a multiple choice question. The user can select at most one option.
The id should be unique so that the result can be identified in the report.

Example:
```json
{
  "id": "question1",
  "type": "multiplechoice",
  "answers": [
    {"value": "1", "text": "Yes"},
    {"value": "2", "text": "No"},
    {"value": "3", "text": "Maybe"}
  ]
}
```

Each answer is represented by a value and text. The text will be displayed to the user in the survey, while
the key will be indicated in the report. Therefore it's essential to use **distinctive keys** for the individual
answers.
Here this means that "Yes", "No" and "Maybe" would be displayed to a survey participant, while "1", "2", "3" would end up in the report.

## pagestopwatch
Insert this item on a page to track the time a user spends on a given page. The unit of measurements is seconds.
Like with other items that end up on the report it's important to assign a unique ID to this item as well.

Example:
```json
{ "type": "pagestopwatch", "id": "page2_duration" }
```

# Example Survey
This is an example survey showcasing many of the previously explainded things in an example.
You can copy this code to try it out on your instance of quest_on.

```json
{
  "title": "Quest_on capability test",
  "pages": [
    {"items": [
      {"type": "textmessage", "content": "Hello dear participant, please enter your code:"},
      {"type": "textinput", "id": "field_code"}
    ], "timelimit": null, "actions": ["continue"]},
    
    {"items": [
      {"type": "textmessage", "content": "Showing a wonderful image for a couple seconds."},
      {"type": "image", "path": "example1.png"},
      {"type": "multiplechoice", "id": "rating1", "answers":
        [{"value":"good", "text":"I love it"}, {"value":"ok", "text":"It's ok"}, {"value":"bad", "text":"It's ugly"}]
      }
    ], "timelimit": {"group": null, "seconds": 3, "timeoutnotice": true}, "actions": ["continue"]},
    
    {"items": [{"type": "image", "path": "example1.png"}], "timelimit":{ "group":null, "seconds": 3, "timeoutnotice": false }, "actions": ["continue"]},
    {"items": [{"type": "image", "path": "example2.png"}], "timelimit":{ "group":null, "seconds": 3, "timeoutnotice": false }, "actions": ["continue"]},
    {"items": [{"type": "image", "path": "example3.png"}], "timelimit":{ "group":null, "seconds": 3, "timeoutnotice": false }, "actions": ["continue"]},
    
    {"items": [
      {"type": "textmessage", "content": "Now you can choose."},
      {"id": "rating2", "type": "multiplechoice", "answers": [{"value": "A", "text": "Apples"}, {"value": "B", "text": "Bananas"}]}
    ], "actions": ["continue"]},
    
    {"items": [{"type": "image", "path": "example1.png"}, {"type": "multiplechoice", "id": "rating3", "answers": [{"value":"good", "text":"I love it"}, {"value":"ok", "text":"It's ok"}, {"value":"bad", "text":"It's ugly"}]}], "timelimit":{ "group":null, "seconds": 3, "timeoutnotice": false }, "actions": ["continue"]},
    {"items": [{"type": "image", "path": "example2.png"}, {"type": "multiplechoice", "id": "rating4", "answers": [{"value":"good", "text":"I love it"}, {"value":"ok", "text":"It's ok"}, {"value":"bad", "text":"It's ugly"}]}], "timelimit":{ "group":null, "seconds": 3, "timeoutnotice": false }, "actions": ["continue"]},
    {"items": [{"type": "image", "path": "example3.png"}, {"type": "multiplechoice", "id": "rating5", "answers": [{"value":"good", "text":"I love it"}, {"value":"ok", "text":"It's ok"}, {"value":"bad", "text":"It's ugly"}]}], "timelimit":{ "group":null, "seconds": 3, "timeoutnotice": false }, "actions": ["continue"]},
    
    {"items": [{"type": "textmessage", "content": "Thank you very much for your participation."}], "actions": []}
  ]
}
```
