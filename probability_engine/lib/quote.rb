require 'probability_engine/version'

class Quote

  def initialize(symbol: , timestamp: , trading_day: , open: , high: , low: , close: , volume: , open_interest: )

    @symbol = symbol
    @timestamp = timestamp
    @trading_day = trading_day
    @open = open
    @high = high
    @low = low
    @close = close
    @volume = volume
    @open_interest = open_interest
  end

  attr_reader :symbol, :timestamp, :trading_day, :open, :high, :low, :close, :volume, :open_interest

end
