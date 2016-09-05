window.loadApplication = (surveyId, callback) ->
    loadImages = (cb) ->
        $.ajax(url: "/all_images.json").done((images) ->
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
 
