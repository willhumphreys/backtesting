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
require_relative 'bollinger_mapper'
require 'active_support/all'
require 'optparse'
require 'open3'

@mt4_file_repo = MT4FileRepo.new(BollingerMapper.new)
@candle_ops = CandleOperations.new


results = @mt4_file_repo.read_quotes('/home/whumphreys/code/backtesting/EURUSD.csv-bollingers.csv')

results.each { |result|
  puts result.sma
}



