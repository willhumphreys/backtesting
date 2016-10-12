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
require_relative 'bollinger_result'
require 'active_support/all'
require 'optparse'
require 'open3'

@mt4_file_repo = MT4FileRepo.new(BollingerMapper.new)
@candle_ops = CandleOperations.new

parent_directory = '../results/normal/data_bollingers'

files = Dir.entries(parent_directory).select {|f| !File.directory? f}

files.each { |file|

  puts "Processing #{file}"

  results = @mt4_file_repo.read_quotes(File.join(parent_directory, file))

  buy_on = false

  win_count = 0
  lose_count = 0

  results.each { |result|

    if buy_on
      if result.profit > 0
        win_count +=1
      else
        lose_count += 1
      end
    end

    if result.down_bb != 0 && result.sma < result.down_bb
      buy_on = true
    end

    if result.sma_bb != 0 && result.sma > result.up_bb
      buy_on = false
    end
  }

  puts "win count: #{win_count} lose count #{lose_count} total trades: #{win_count + lose_count} "\
"winning percentage: #{((win_count.to_f / (win_count + lose_count)) * 100).round(2)}%"
}



