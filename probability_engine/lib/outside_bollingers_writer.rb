require 'probability_engine/version'
require 'fileutils'

class OutsideBollingersWriter

  def initialize(output_directory, output_file)
    @summary_file = "#{output_directory}/#{output_file}"

    FileUtils.mkpath output_directory unless File.exists?(output_directory)
    File.delete(@summary_file) if File.exist?(@summary_file)

  end

  def write_summary_header
    open(@summary_file, 'a') { |f|
      f << "sma,symbol,win_count,lose_count,winning_percentage\n"
    }
  end

  def write_line(result)

    open(@summary_file, 'a') { |f|
      f << "#{result.sma},#{result.symbol},#{result.win_count},#{result.lose_count},#{result.winning_percentage}\n"
    }

  end
end