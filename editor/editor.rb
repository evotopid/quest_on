#!/usr/bin/env ruby
require 'sinatra/base'
require 'json'
require 'coffee-script'

module QuestOnEditor
    SURVEY_DIR = File.join(File.dirname(__FILE__), "surveys")
    SCRIPTS_DIR = File.join(File.dirname(__FILE__), "scripts")

    class Application < Sinatra::Base
        get "/" do
            # List existing surveys.
            surveys = Dir["#{SURVEY_DIR}/*.json"].map{|path| get_survey_metadata(path: path)}
            puts surveys.inspect
            erb :index, locals: { surveys: surveys }
        end

        get "/edit/:id" do |id|
            survey_metadata = get_survey_metadata(id)
            if survey_metadata.empty?
                [404, "Unknown survey"]
            else
                erb :edit_survey, locals: { survey_title: survey_metadata["title"],
                                            survey_id: survey_metadata["id"] }
            end
        end

        # Fetch survey content.
        get "/survey/*.json" do |id|
            content_type "application/javascript"
            file = get_survey_path(id)
            if File.exist? file
                File.read file
            else
                [404, "not found"]
            end
        end

        # Store survey content.
        post "/survey/*.json" do |id|
            File.open(get_survey_path(id), "w") do |file|
                file.write(params[:content])
            end
        end

        get "/create" do
            erb :create_survey
        end

        post "/create" do
            # Extract values from form.
            title, identifier = params[:title], params[:identifier].gsub(/[^a-zA-Z0-9_-]/, "")
            filename = get_survey_path(identifier)
           
            # Make sure fields were provided.
            if title.empty? or identifier.empty?
                "Please go back and provide both title and identifier."
            # Make sure we are not accidentally overwriting an existing survey.
            elsif File.exist? filename
                "Identifier invalid. Survey #{identifier}.json already exists."
            else
                # Write basic data to file.
                File.open(filename, "w") do |file|
                    data = { "metadata" => { "title" => title }, "pages" => [] }
                    file.write(JSON.dump(data))
                end

                # Redirect user to editing page.
                redirect to("/edit/#{identifier}")
            end
        end

        get "/js/*.js" do |filename|
            content_type "application/javascript"
            filename.gsub!(/[^a-zA-Z0-9_-]/, "")
            jsfile = File.join(SCRIPTS_DIR, filename + ".js")
            csfile = File.join(SCRIPTS_DIR, filename + ".coffee")
            if File.exist? jsfile
                File.read jsfile
            elsif File.exist? csfile
                CoffeeScript.compile File.read(csfile)
            else
                [404, 'alert("script not found")']
            end
        end

        def get_survey_path(survey_id)
            File.join(SURVEY_DIR, survey_id.gsub(/[^a-zA-Z0-9_-]/, "") + ".json")
        end

        def get_survey_metadata(survey_id=nil, path: nil)
            if path.nil?
                path = get_survey_path(survey_id)
            end

            if File.exist? path
                if survey_id.nil?
                    id = File.basename(path, ".json")
                else
                    id = survey_id
                end
                {"id"=>id}.merge JSON.load(File.read path)["metadata"]
            else
                {}
            end
        end

        # start the server if ruby file executed directly
        run! if app_file == $0
    end
end

