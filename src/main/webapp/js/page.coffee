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

class window.Page
    constructor: (data, @survey) ->
        @actions = data.actions
        @values = []
        if not data.timelimit?
            @timelimit = null
        else
            @timelimit = new window.Timelimit(data.timelimit, this)

        @items = for item in data.items
            new window.PageItem(item)

    getHTML: ->
        html = ""
        for item in @items
            html += item.getHTML()
        for action in @actions
            if action == "continue"
                html += '<div class="nextbutton">'
                html += '<input type="button" onclick="survey.nextPage();" value="' + window._('Continue') + '">'
                html += '</div>'
        html

    saveValues: ->
        @values = []
        for item in @items
            pair = item.getKeyValuePair()
            if pair?
                @values.push pair
        null

    startTimer: ->
        # Only if the last timer was in a different group than this one we need to start the new one.
        if @survey.currentTimelimit == null
            if @timelimit?
                @survey.currentTimelimit = @timelimit
                @survey.currentTimelimit.start()
        else if @survey.currentTimelimit.group == null or @survey.currentTimelimit.group != @timelimit.group
            @survey.currentTimelimit.stop()
            @survey.currentTimelimit = @timelimit
            if @timelimit?
                @survey.currentTimelimit.start()
        else
            @survey.currentTimelimit.page = this
        null

    stopTimer: ->
        if @survey.currentTimelimit?
            @survey.currentTimelimit.stop()

    timelimitReached: ->
        if @survey.currentTimelimit.timeoutnotice
            console.log 'X'
            html = '<div class=\'textmessage\'>' + _('You ran out of time.') + '</div>'
            html += '<div class="nextbutton">'
            html += '<input type="button" onclick="survey.nextPage();" value="' + _('Continue') + '">'
            html += '</div>'
            $('#container').html html
        else
            @survey.nextPage()

