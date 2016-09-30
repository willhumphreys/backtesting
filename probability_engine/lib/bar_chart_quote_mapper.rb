require 'probability_engine/version'
require_relative 'quote'

class BarChartQuoteMapper

  def map(quote)
    Quote.new(quote['symbol'], quote['timestamp'], quote['tradingDay'], quote['open'], quote['high'],
                      quote['low'], quote['close'], quote['volume'], quote['openInterest'])
  end

end
