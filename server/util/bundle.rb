#!/usr/bin/env ruby

require 'uglifier'
js_dir = File.join(File.dirname(__FILE__), "..", "src", "main", "webapp", "js", "")
bundle = File.join(js_dir, "bundle.js")
bundle_min = File.join(js_dir, "bundle.min.js")

File.unlink bundle if File.exists? bundle
File.unlink bundle_min if File.exists? bundle_min

script = ""

Dir["#{js_dir}*.js"].sort.each do |f|
  script += "/* File #{File.basename(f)} */\n"
  script += File.read(f)
  script += "\n\n"
end

File.open(bundle, "w") do |f|
  f.write(script)
  puts "Created #{File.path f}"
end

File.open(bundle_min, "w") do |f|
  f.write(Uglifier.compile script)
  puts "Created #{File.path f}"
end

