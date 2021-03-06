require 'probability_engine/version'

class CrossOverResult

  def initialize(timestamp:, direction:, profit:, above_top_band:, below_bottom_band:, long_cross_over:, short_cross_over:)

    @timestamp = timestamp
    @direction = direction
    @profit = profit
    @above_top_band = above_top_band
    @below_bottom_band = below_bottom_band
    @long_cross_over = long_cross_over
    @short_cross_over = short_cross_over
  end

  def is_not_a_loser
    profit >= 0
  end

  def is_outside_right_band
    @direction == 'long' && @below_bottom_band || @direction == 'short' && @above_top_band
  end

  attr_reader :timestamp, :direction, :profit, :above_top_band, :below_bottom_band, :long_cross_over, :short_cross_over
end
