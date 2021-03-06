# Copyright 2014-2016 Leonardo Schwarz (leoschwarz.com)
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

class window.Survey
    constructor: (@id, data) ->
        @title = data.title
        @currentTimelimit = null
        @finished = false
        @pages = for page in data.pages
            new window.Page(page, this)
    
    run: ->
        @loadPage 0
        @updateLoop()
    
    getResults: ->
        results = []
        for page in @pages
            results = results.concat(page.values)
        results

    updateLoop: ->
        if @currentTimelimit? and @currentTimelimit.active
            $("#time_left").html _("Time left:") + " " + @currentTimelimit.secondsLeft() + "s"
        else
            $("#time_left").html ""
        window.setTimeout((=> @updateLoop()), 500)

    nextPage: ->
        @currentPage.stopTimer()
        @currentPage.saveValues()
        if @currentPageIndex + 1 < @pages.length
            @loadPage @currentPageIndex + 1
        else
            alert _("You reached the end of the survey.")

    loadPage: (index) ->
        @currentPageIndex = index
        @currentPage = @pages[index]

        # Display the page.
        $("#container").html @currentPage.getHTML()

        # Submit results on the last page.
        if @currentPageIndex == @pages.length - 1
            $("#container").append '<div id=\'transfer_progress\'>' + window._('Your answers are being transmitted to the server...') + '</div>'
            @submitResults()

        @currentPage.startTimer()

    submitResults: ->
        data =
            results: JSON.stringify @getResults()
            surveyId: @id

        $.post("/store", data).done(=>
            @finished = true
            $("#transfer_progress").html _("Your answers have been submitted successfully.")
        ).fail(->
            $("#transfer_progress").html _("An error occured while submitting your answers.") + '<br><input type=\'button\' onclick=\'survey.submitResults();\' value=\'' + _('Try again') + '\'>'
        )


# Prevent users from accidentally closing their browser's window.
window.onbeforeunload = ->
    if window.survey?
        if window.survey.finished
            null
        else
            _ 'If you proceed closing this page, your results won\'t be stored.'
    else
        null
