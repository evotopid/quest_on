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

class window.Timelimit
    constructor: (data, @page) ->
        @group = data.group
        
        if data.seconds?
            @seconds = data.seconds
        else
            console.error 'Timelimit with undefined seconds attribute encountered in your survey.json file.'
            alert 'Timelimit with undefined seconds attribute encountered in your survey.json file.'
        
        if data.timeoutnotice?
            @timeoutnotice = data.timeoutnotice
        else
            @timeoutnotice = false

        @active = false

    start: ->
        @active = true
        @startedAt = (new Date).getTime()
        @update()

    stop: ->
        @seconds = @millisLeft() * 1000
        @active = false

    update: ->
        window.setTimeout (=>
            if @active
                if @millisLeft() <= 0
                    @active = false
                    @page.timelimitReached()
                else
                    @update()
        ), 300

    secondsLeft: -> Math.ceil(@millisLeft() * 0.001)
    millisLeft: -> @seconds * 1000 - ((new Date).getTime() - @startedAt)

