
class RScriptService

  def execute(options)
    summary_new_by_year = "Rscript ../RScripts/summary_new_by_year.r #{options[:output_directory]}/summary_high_scores-2-100-bands.csv #{options[:ruby_graph_output]}"

    execute_command(summary_new_by_year)

  end

  def execute_command(command)
    stdout, stderr, status = Open3.capture3(command)

    puts status
    puts stdout
    puts stderr
  end

end
