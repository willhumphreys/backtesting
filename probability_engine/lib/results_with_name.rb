require 'probability_engine/version'

class ResultsWithName

  def initialize(name, trade_results)
    @name = name
    @trade_results = trade_results
  end

  attr_reader :name, :trade_results

end