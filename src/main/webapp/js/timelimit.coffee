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

