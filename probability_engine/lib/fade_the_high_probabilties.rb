require 'probability_engine/version'
require 'json'
require_relative 'bar_chart_file_repo'
require_relative 'mt4_file_repo'
require_relative 'stat_executor'
require_relative 'candle_operations'
require_relative 'processor'
require_relative 'processors'
require_relative 'news_reader'
require_relative 'fade_mapper'
require_relative 'date_range_generator'
require_relative 'trade_results_processor'
require_relative 'results'
require_relative 'results_writer'
require_relative 'execution_parameters'
require_relative 'results_with_name'
require_relative 'option_parser'
require_relative 'profile'
require_relative 'profiles'
require_relative 'r_script_service'
require 'active_support/all'
require 'optparse'
require 'open3'

options = Options.new.parse

@bar_chart_file_repo = BarChartFileRepo.new
@mt4_file_repo = MT4FileRepo.new(FadeMapper.new)
@candle_ops = CandleOperations.new
@processors = Processors.new
@date_range_generator = DateRangeGenerator.new(DateTime.new(2007, 12, 5), DateTime.new(2016, 8, 2))

profile = Profiles.new.data[options[:profile].to_sym]

raise "Profile not found '#{options[:profile]}'" if profile.nil?

symbols = %w(AUDUSD EURCHF EURGBP EURUSD GBPUSD USDCAD USDCHF NZDUSD USDJPY EURJPY)

all_results_with_names = []
symbols.each { |data_set|

  trade_results = @mt4_file_repo.read_quotes("#{options[:input_directory]}/#{data_set}.csv")
  all_results_with_names.push(ResultsWithName.new(data_set, trade_results))
}

date_ranges = @date_range_generator.get_ranges

@results_writer = ResultsWriter.new(options[:output_directory])

@results_writer.write_summary_header

def process_data_set(execution_parameters)

  data_set_processor = TradeResultsProcessor.new(execution_parameters)
  results = data_set_processor.process_trade_results(execution_parameters.trade_results)

  unless results.nil?
    puts "#{execution_parameters.start_date}-#{execution_parameters.end_date} #{execution_parameters.data_set} minimum_profit: #{execution_parameters.minimum_profit} cut off: #{execution_parameters.required_score} "\
               "moving average count: #{execution_parameters.window_size} winners: #{results.winners.size} losers: #{results.losers.size} "\
               "#{results.winning_percentage}% cut off percentage: #{results.cut_off_percentage}"

    @results_writer.write_summary_line(execution_parameters, results)
  end

end

profile.minimum_profits.each { |minimum_profit|
  profile.cut_offs.each { |required_score|
    profile.moving_average_counts.each { |window_size|
      date_ranges.each { |date_range|
        start_date = date_range.start_date
        end_date = date_range.end_date

        if required_score.abs >= window_size
          next
        end

        all_results_with_names.each { |results_with_name|
          execution_parameters = Execution_parameters.new(start_date, end_date, results_with_name.name, minimum_profit,
                                                          required_score, window_size, results_with_name.trade_results)
          process_data_set(execution_parameters)
        }
      }
    }
  }
}

RScriptService.new.execute(options)

