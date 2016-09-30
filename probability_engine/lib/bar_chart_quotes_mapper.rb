require 'probability_engine/version'
require_relative 'bar_chart_quote_mapper'

class BarChartQuotesMapper

  def initialize
    @quote_mapper = BarChartQuoteMapper.new
  end

  def map(quotes)

    mapped_quotes = Array.new

    quotes.each do |quote|
      mapped_quotes.push(@quote_mapper.map(quote))
    end
    mapped_quotes

  end

end
