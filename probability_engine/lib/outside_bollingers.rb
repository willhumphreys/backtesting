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
require_relative 'bollinger_out_result'
require_relative 'outside_bollingers_writer'
require 'active_support/all'
require 'optparse'
require 'open3'

@mt4_file_repo = MT4FileRepo.new(BollingerMapper.new)

results_writer = OutsideBollingersWriter.new('../results/normal', 'outside_bollinger.csv')
results_writer.write_summary_header


bollinger_out_results = []
smas = 10.step(100, 10).to_a

smas.each { |sma|
  parent_directory = "../results/normal/data_bollingers/#{sma}"
  files = Dir.entries(parent_directory).select { |f| !File.directory? f }

  files.each { |file|

    puts "Processing #{file} SMA #{sma}"

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

      # if result.sma_bb != 0 && result.sma > result.up_bb
      if result.sma_bb != 0 && result.sma > result.sma_bb
        buy_on = false
      end

    }

    winning_percentage = ((win_count.to_f / (win_count + lose_count)) * 100).round(2)
    result = BollingerOutResult.new(sma: sma,
                                    symbol: file.split('-').first,
                                    win_count: win_count,
                                    lose_count: lose_count,
                                    winning_percentage: winning_percentage)
    bollinger_out_results.push(result)

    results_writer.write_line(result)

    puts "win count: #{win_count} lose count #{lose_count} total trades: #{win_count + lose_count} "\
"winning percentage: #{winning_percentage}%"
  }
}

