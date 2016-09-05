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
                "<div class='image'><img src='/img/#{@path}'></div>"
            when 'multiplechoice'
                html = '<div class="multiplechoice">'
                for answer_id of @answers
                    answer_text = @answers[answer_id]
                    html += "<label><input type='radio' name='#{@id}' value='#{answer_id}'>#{answer_text}</label><br>"
                    html += '</div>'
                html
            when 'textinput'
                '<div class="textinput"><input type="text" name="' + @id + '"></div>'
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

