require 'probability_engine/version'
require_relative 'trade_result_processor'

class TradeResultsProcessor

  def initialize(execution_parameters)
    @execution_parameters = execution_parameters
    @winners = []
    @losers = []

    @mt4_file_repo = MT4FileRepo.new(FadeMapper.new)
    @trade_result_processor = TradeResultProcessor.new(execution_parameters.window_size, execution_parameters.required_score)
  end

  public def process_trade_results(trade_results)
    trade_on = false

    trade_results.each { |trade_result|

      if trade_result.timestamp.utc < @execution_parameters.start_date ||
          trade_result.timestamp.utc > @execution_parameters.end_date #||
         # trade_result.profit.abs < @execution_parameters.minimum_profit
        next
      end

      trade_on = @trade_result_processor.process_trade_result(trade_result, trade_on, @winners, @losers, @execution_parameters.minimum_profit)
    }

    if !@losers.empty? || !@winners.empty?

      winning_percentage = ((@winners.size.to_f / (@losers.size + @winners.size)) * 100).round(2)
      cut_off_percentage = ((@execution_parameters.required_score.to_f / @execution_parameters.window_size) * 100).round(2)

      puts @trade_result_processor.stored_trades.join('')

      Results.new(winning_percentage, cut_off_percentage, @winners, @losers)
    end
  end
end