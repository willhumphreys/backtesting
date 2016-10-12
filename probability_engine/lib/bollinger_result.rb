require 'probability_engine/version'

class BollingerResult

  def initialize(timestamp: , direction: , profit:, above_top_band:, below_bottom_band:, sma:, down_bb:, up_bb:, sma_bb:)

    @timestamp = timestamp
    @direction = direction
    @profit = profit
    @above_top_band = above_top_band
    @below_bottom_band = below_bottom_band
    @sma = sma
    @down_bb = down_bb
    @up_bb = up_bb
    @sma_bb = sma_bb
  end

  def is_not_a_loser
    profit >= 0
  end

  def is_outside_right_band
    @direction == 'long' && @below_bottom_band || @direction == 'short' && @above_top_band
  end

  attr_reader :timestamp, :direction, :profit, :above_top_band, :below_bottom_band, :sma, :down_bb, :up_bb, :sma_bb
end
