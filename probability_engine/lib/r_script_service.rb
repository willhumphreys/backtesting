class RScriptService
  def execute(options)

    scripts = [
        "Rscript ../RScripts/summary_new_by_year.r #{options[:output_directory]}/odds_results.csv #{options[:ruby_graph_output]}",
        "Rscript ../RScripts/winners_by_year_frequency.r #{options[:output_directory]} #{options[:ruby_graph_output]}",
        "Rscript ../RScripts/summary_ruby.r #{options[:output_directory]} #{options[:output_directory]}",
        "Rscript ../RScripts/summary_ruby_graphs.r #{options[:output_directory]} #{options[:output_directory]}"
    ]

    scripts.each { |script|
      execute_command(script)
    }

  end

  def execute_command(command)
    stdout, stderr, status = Open3.capture3(command)

    puts status
    puts stdout
    puts stderr
  end
end
