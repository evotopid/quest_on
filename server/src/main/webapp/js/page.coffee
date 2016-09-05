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

