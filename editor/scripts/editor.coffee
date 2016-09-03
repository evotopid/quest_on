# Unique IDs
# ----------
# To avoid large re-enumeration efforts when elements get added or deleted its better
# to give every PAGE and PAGE_ITEM its own unique ids. The DOM will represent these
# values and also the data of each element to allo for easy exporting of results.
# in HTML: `data-element-id`: element id stored as HTML data attribute
# in JSON: `element-id`:      element id

class PageEditor
    constructor: (@editor, @page_number) ->
        @sel = "#pageeditor-#{@page_number}"

    render: ->
        # Locate or create div for page edit.
        if $(@sel).length == 0
            @editor.root.append("<div class='pageeditor' id='pageeditor-#{@page_number}'></div>")
        div = $(@sel)

        # Generate controls for existing page items.
        page = @editor.data.pages[@page_number]
        @controls = for item in page.items
            div.append """
            <div class="pageeditor-item">
                <label>
                    Type
                    <select>
                        <option value="textdisplay">Text (display)</option>
                        <option value="textinput">Text (input)</option>
                    </select>
                </label>
            </div>
            """

class SurveyData
    # raw: raw data as stored for future display
    constructor: (@raw) ->

    get_pages: -> @raw.pages
    get_title: -> @row.title
    set_title: (title) -> @row.title = title

    next_element_id: ->
        if @raw.max_element_id?
            ++@raw.max_element_id
        else
            @raw.max_element_id = 1

class Editor
    constructor: (@selector, @id) ->
        @root = $(@selector)

    launch: ->
        # Load survey data from the server.
        $.get "/survey/#{@id}.json", (data) =>
            @data = new SurveyData(data)

            # Build the form.
            @page_editors = for page_number in [0..data.pages.length]
                page_editor = new PageEditor(this, page_number)
                page_editor.render()
                page_editor

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
    window.editor.launch()

