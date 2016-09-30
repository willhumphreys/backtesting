require 'probability_engine/version'
require_relative 'trade_result'
require 'date'

class FadeMapper

  def initialize

  end

  def map(row)

    #2007-12-27T02:00,short,0.87368,stopped,2007-12-27T03:00,0.87472,-21

    date = DateTime.strptime("#{row[0]}", '%Y-%m-%dT%H:%M')

    above_top_band_value = row[9] == 'true'
    below_bottom_band_value = row[10] == 'true'

    TradeResult.new(
        timestamp: date,
        direction: row[1],
        profit: row[6].to_i,
        above_top_band: above_top_band_value,
        below_bottom_band: below_bottom_band_value
    )

  end
end
