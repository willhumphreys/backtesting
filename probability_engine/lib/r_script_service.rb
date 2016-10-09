class RScriptService
  def execute(options)
    summary_new_by_year = "Rscript ../RScripts/summary_new_by_year.r #{options[:output_directory]}/odds_results.csv #{options[:ruby_graph_output]}"
    execute_command(summary_new_by_year)
    winners_by_year_frequency = "Rscript ../RScripts/winners_by_year_frequency.r #{options[:output_directory]} #{options[:ruby_graph_output]}"
    execute_command(winners_by_year_frequency)

  end

  def execute_command(command)
    stdout, stderr, status = Open3.capture3(command)

    puts status
    puts stdout
    puts stderr
  end
end
