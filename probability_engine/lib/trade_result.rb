require 'probability_engine/version'

class TradeResult

  def initialize(timestamp: , direction: , profit:, above_top_band:, below_bottom_band:)

    @timestamp = timestamp
    @direction = direction
    @profit = profit
    @above_top_band = above_top_band
    @below_bottom_band = below_bottom_band
  end

  def is_not_a_loser
    profit >= 0
  end

  def is_outside_right_band
    @direction == 'long' && @below_bottom_band || @direction == 'short' && @above_top_band
  end

  attr_reader :timestamp, :direction, :profit, :above_top_band, :below_bottom_band
end