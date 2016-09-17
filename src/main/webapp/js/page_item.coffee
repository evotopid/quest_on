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

class window.PageItem
    constructor: (data) ->
        @type = data.type
        @content = data.content
        @path = data.path
        @id = data.id
        @answers = data.answers

    getHTML: ->
        switch @type
            when 'textmessage'
                "<div class='textmessage'>#{@content}</div>"
            when 'image'
                "<div class='image'><img src='/survey/#{window.survey.id}/img/#{@path}'></div>"
            when 'multiplechoice'
                html = '<div class="multiplechoice">'
                for answer in @answers
                    html += "<label><input type='radio' name='#{@id}' value='#{answer.value}'>#{answer.text}</label><br>"
                    html += '</div>'
                html
            when 'textinput'
                "<div class='textinput'><input type='text' name='#{@id}'></div>"
            else
                console.error 'Trying to get HTML for unknown PageItem type: ' + @type
                null
    
    getKeyValuePair: ->
        if @type == 'textinput'
            { id: @id, value: $("input[name=#{@id}]", '#container').val() }
        else if @type == "multiplechoice"
            { id: @id, value: $("input[name=#{@id}]:checked", '#container').val() }
        else
            null

