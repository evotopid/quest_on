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

window.loadApplication = (surveyId, callback) ->
    loadImages = (cb) ->
        $.ajax(url: "/survey/#{surveyId}/images.json").done((images) ->
            remainingImages = images.length
            window.loadingObjects = for src in images
                image = new Image
                image.onload = ->
                    remainingImages--
                    if remainingImages <= 0
                        cb()
                    null
                image.src = src
                image
        ).fail((error) ->
            console.error "Preloading images failed:"
            console.error error
            cb()
        )
    
    loadLocalization = (cb) ->
        $.ajax(url: "/strings.json")
            .done((strings) -> window.$strings = strings; cb())
            .fail((error) ->
                console.error("Failed to load strings.json from server.")
                window.$strings = {}
                cb()
            )

    loadSurvey = (cb) ->
        $.ajax(url: "/survey/#{surveyId}.json")
            .done((data) -> cb(new Survey(surveyId, data)))
            .fail((error) ->
                console.error "Loading survey failed:"
                console.error error
                alert "Loading survey failed."
                cb(undefined)
            )

    loadLocalization ->
        loadImages ->
            loadSurvey (s) ->
                window.survey = s
                window.document.title = s.title
                callback()
 
