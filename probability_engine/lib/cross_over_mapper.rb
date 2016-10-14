require 'probability_engine/version'
require_relative 'cross_over_result'
require 'date'

class CrossOverMapper

  def initialize

  end

  def map(row)

    #2007-12-27T02:00,short,0.87368,stopped,2007-12-27T03:00,0.87472,-21

    date = DateTime.strptime("#{row[0]}", '%Y-%m-%d %H:%M:%S')

    above_top_band_value = row[9] == 'true'
    below_bottom_band_value = row[10] == 'true'

    CrossOverResult.new(
        timestamp: date,
        direction: row[1],
        profit: row[6].to_i,
        above_top_band: above_top_band_value,
        below_bottom_band: below_bottom_band_value,
        long_cross_over: row[12].to_f,
        short_cross_over: row[13].to_f
    )

  end
end
