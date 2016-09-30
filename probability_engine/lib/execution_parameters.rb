require 'probability_engine/version'

class Execution_parameters

  def initialize(start_date, end_date, data_set, minimum_profit, required_score, window_size, trade_results)
    @start_date = start_date
    @end_date = end_date
    @data_set = data_set
    @minimum_profit = minimum_profit
    @required_score = required_score
    @window_size = window_size
    @trade_results = trade_results
  end

  attr_reader :start_date, :end_date, :data_set, :minimum_profit, :required_score, :window_size, :trade_results

end