require 'probability_engine/version'
require_relative 'candle_operations'
require_relative 'processor'
class Processors
  attr_reader :processors

  def initialize
    @candle_ops = CandleOperations.new

    higher_high_close_above_l = lambda do |first, second, contract|
      if @candle_ops.is_a_higher_high_in(first, second) && @candle_ops.closes_above_range(first, second)
        return @candle_ops.get_pip_difference(first.high, second.high, contract)
      end
      return nil
    end

    higher_high_close_inside_l = lambda do |first, second, contract|
      if @candle_ops.is_a_higher_high_in(first, second) && @candle_ops.closes_inside_range(first, second)
        return @candle_ops.get_pip_difference(first.high, second.high, contract)
      end
      nil
    end

    lower_low_close_below_l = lambda do |first, second, contract|
      if @candle_ops.is_a_lower_low_in(first, second) && @candle_ops.closes_below_range(first, second)
        return @candle_ops.get_pip_difference(first.low, second.low, contract)
      end
      return nil
    end

    lower_low_close_inside_l = lambda do |first, second, contract|
      if @candle_ops.is_a_lower_low_in(first, second) && @candle_ops.closes_inside_range(first, second)
        return @candle_ops.get_pip_difference(first.low, second.low, contract)
      end
      nil
    end

    @processors = {
        :higher_high_close_above => Processor.new('higher_high_close_above', higher_high_close_above_l),
        :higher_high_close_inside => Processor.new('higher_high_close_inside', higher_high_close_inside_l),
        :lower_low_close_below => Processor.new('lower_low_close_below', lower_low_close_below_l),
        :lower_low_close_inside => Processor.new('lower_low_close_inside', lower_low_close_inside_l)
    }
  end

end