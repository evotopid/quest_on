require 'rubygems'
require 'bundler/setup'
require 'rubyXL'
require 'mail'
require 'json'
require 'sinatra/base'


module QuestOn
  class Application < Sinatra::Base
    # Startpage
    get '/' do
      content_type "text/html"
      File.read("web/index.html")
    end

    get '/index.html' do
      content_type "text/html"
      File.read("web/index.html")
    end
    
    get '/*.js' do |name|
      content_type "text/javascript"
      File.read("web/js/#{name}.js")
    end

    # CSS
    get '/*.css' do |name|
      content_type "text/css"
      File.read("web/css/#{name}.css")
    end

    # Bilder
    get '/*.jpg' do |name|
      sleep 0.3
      content_type "image/jpeg"
      File.read("web/img/#{name}.jpg")
    end
    get '/*.png' do |name|
      content_type "image/png"
      File.read("web/img/#{name}.png")
    end

    # mail configuration
    options = { :address              => ENV["SMPT_SERVER"],
                :port                 => 587,
                :domain               => ENV["SMTP_DOMAIN"],
                :user_name            => ENV["SMPT_USERNAME"],
                :password             => ENV["SMPT_PASSWORD"],
                :authentication       => 'plain',
                :enable_starttls_auto => true  }

    Mail.defaults do
      delivery_method :smtp, options
    end


    # store!
    post '/store' do
      results = JSON.parse(params["results"])
      
      # excel
      workbook  = RubyXL::Workbook.new
      worksheet = workbook[0]
      row = 0
      results.each do |result|
        worksheet.add_cell(row, 0, result["id"])
        worksheet.add_cell(row, 1, result["value"])
        row += 1
      end
      worksheet.change_column_bold(0, true)
      
      # email
      message = Mail.new do 
        from     ENV["SOURCE_EMAIL"]
        to       ENV["DESTINATION_EMAIL"]
        subject  'Neues Umfrageresultat'
        body     'Weitere Informationen in den angehängten Dateien.'
      end
      message.attachments["werte.xlsx"] = workbook.stream.read
      message.deliver
    
      "ok"  
    end

    # Survey
    get '/survey.json' do
      content_type "application/json"
      File.read("survey.json")
    end
    
    # Localization
    get '/strings.json' do
      content_type 'application/json'
      
      default_language = "en" # TODO externalize
      requested_language = request.env["HTTP_ACCEPT_LANGUAGE"][0..1].gsub(/^a-zA-Z/, "")
      
      if File.exists? locale_path(requested_language)
        File.read(locale_path(requested_language))
      else
        File.read(locale_path(default_language))
      end
    end

    # Filenames of all images in folder img to preload the images
    get '/all_images.json' do
      content_type "application/json"
      Dir["web/img/*"].map{|path| File.basename(path)}.to_json
    end
    
    private
    def locale_path(language)
      File.join(File.dirname(__FILE__), "locales", "#{language}.json")
    end
  end
end
