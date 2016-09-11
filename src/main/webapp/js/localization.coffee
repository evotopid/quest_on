window._ = (str) ->
    if window.$strings[str]?
        window.$strings[str]
    else
        str
