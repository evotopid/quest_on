class PageEditor
    constructor: (@editor, @page_number)


class Editor
    constructor: (@selector, @id) ->
        @root = $(@selector)
    
    launch: ->
        # Load survey data from the server.
        $.get "/survey/#{@id}.json", (data) =>
            @data = data

            # Build the form.

    delete_page: (position) ->
        # Remove data for page.
        @data.pages.splice(position, 1)

        # Remove editor component: TODO

    # position: insert a new editor page at this location
    add_page: (posititon) ->
        # WARNING (TODO) Because pages store and use their page_number it is essential that it gets manually updated if the page is inserted in the middle of the survey.
        # Otherwise we might consider adding new pages only at the very end and then have a manual swap function for the user although this would result in a pretty bad user experience.



# Export launch_editor.
window.launch_editor = (selector, id) ->
    window.editor = new Editor(selector, id)
    window.editor.launch

