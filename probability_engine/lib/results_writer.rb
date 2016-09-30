require 'probability_engine/version'

class ResultsWriter

  def initialize
    @output_directory = 'results'
    @summary_file = "#{@output_directory}/summary_high_scores-2-100-bands.csv"

    File.delete(@summary_file) if File.exist?(@summary_file)

    #FileUtils.rm_rf Dir.glob("#{@output_directory}/*")

  end

  def write_summary_header
    open(@summary_file, 'a') { |f|
      f << 'start_date,end_date,data_set,minimum_profit,cut_off,moving_average_count,winners.size,losers.size,'\
       "winning_percentage,cut_off_percentage\n"
    }
  end

  def write_summary_line(execution_parameters, results)

    open(@summary_file, 'a') { |f|
      f << "#{execution_parameters.start_date},#{execution_parameters.end_date},#{execution_parameters.data_set},"\
            "#{execution_parameters.minimum_profit},#{execution_parameters.required_score},#{execution_parameters.window_size},"\
                 "#{results.winners.size},#{results.losers.size},#{results.winning_percentage},#{results.cut_off_percentage}\n"
    }

  end
end