require 'probability_engine/version'

class CandleOperations
  def is_a_low_nearer(first, second)
    (first.low - second.open).abs < (first.high - second.open).abs
  end

  def is_high_nearer(first, second)
    (first.low - second.open).abs > (first.high - second.open).abs
  end

  def is_day_opening_in_range(first, second)
    second.open >= first.low && second.open <= first.high
  end

  def is_a_lower_low_in(first, second)
    second.low < first.low
  end

  def is_a_higher_high_in(first, second)
    second.high > first.high
  end

  def is_a_down_day(quote)
    quote.close < quote.open
    #quote.close < quote.open
  end

  def is_a_up_day(quote)
    quote.close > quote.open
    #quote.close > quote.open
  end

  def full_candle_gaps(first, second)
    is_long_gap = second.open < first.low
    is_short_gap = second.open > first.high

    is_long_gap || is_short_gap
  end

  def full_candle_gap_closes(first, second)
    #gap above #gapbelow
    (second.low <= first.high && second.open > first.high) || (second.high >= first.low && second.open < first.low)
  end

  def gaps(first, second)
    fixed_open = first.open
    fixed_close = first.close

    if fixed_close < fixed_open
      fixed_close = fixed_open
      fixed_open = fixed_close
    end

    !(fixed_open..fixed_close).include?(second.open)
  end

  def gap_closes(first, second)


    #first candle is up candle
    if first.close > first.open
      #Second candle opens above first candle
      #Second candle opens below first candle
      second.open > first.close && second.low <= first.close ||
          second.open < first.open && second.high > first.open
    else
      #Second candle opens above first candle
      #Second candle opens below first candle
      second.open > first.open && second.low <= first.open ||
          second.open < first.close && second.high > first.close
    end

  end

  def is_inside_day(first, second)
    second.low > first.low && second.high < first.high
  end

  def is_day_up(quote)
    quote.close > quote.open
  end

  def is_day_down(quote)
    quote.close < quote.open
  end

  def percentage(day_up_count, total_count)
    ((day_up_count.to_f / (total_count)) * 100).round(2)
  end

  def percent_message(fraction, total, message)
    puts "#{message} #{percentage(fraction, total)}% #{fraction}/#{total}"
  end

  def day_percentage(day_up_count, day_not_up_count)
    ((day_up_count.to_f / (day_not_up_count + day_up_count)) * 100).round(2)
  end

  def candle_closes_closer_low(quote)
    (quote.close - quote.low).abs < (quote.close - quote.high).abs
  end

  def candle_closes_closer_high(quote)
    (quote.close - quote.high).abs > (quote.close - quote.low).abs
  end

  def closes_inside_range(first, second)
    second.close >= first.low && second.close <= first.high
  end

  def closes_above_range(first, second)
    second.close > first.high
  end

  def closes_below_range(first, second)
    second.close < first.low
  end

  def get_pip_difference(first, second, contract)
    multiplier = 10000
    if contract.include? 'JPY'
      multiplier = 100
    end
    ((second - first) * multiplier).round(0).abs
  end

end
