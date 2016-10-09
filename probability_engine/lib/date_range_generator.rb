require 'probability_engine/version'
require 'active_support/all'

class DateRange

  def initialize(start_date, end_date)
    @start_date = start_date
    @end_date = end_date
  end

  def to_s
    "#{@start_date} - #{@end_date}"
  end

  attr_reader :start_date, :end_date
end

class DateRangeGenerator

  def initialize(data_start_date, data_end_date)
    @data_start_date = data_start_date
    @data_end_date = data_end_date
  end

  def get_ranges
    date_ranges = []
    date_periods = [1]

    date_periods.each { |date_period|

      run_end_date = @data_end_date

      while run_end_date > @data_start_date do

        run_start_date = run_end_date - (date_period * 12).months

        #puts "start_date #{run_start_date} end date #{run_end_date} date_period #{date_period}"

        # date_ranges.push(DateRange.new(run_start_date , run_end_date + 12.months))
        date_ranges.push(DateRange.new(run_start_date , run_end_date))

        run_end_date = run_end_date - (date_period * 12).months
      end
    }
    date_ranges
  end
end
#
# ranges = DateRangeGenerator.new(DateTime.new(2007, 12, 5), DateTime.new(2016, 8, 2)).get_ranges
# puts(ranges)


