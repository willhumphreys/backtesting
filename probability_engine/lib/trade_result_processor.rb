require 'probability_engine/version'

class TradeResultProcessor

  attr_reader :stored_trades
  attr_writer :stored_trades

  def initialize(window_size, required_score)
    @required_score = required_score
    @window_size = window_size
    @stored_trades = []
  end

  def process_trade_result(trade_result, trade_on, winners, losers, minimum_profit)
    if new_trade_conditions_met(minimum_profit, trade_on, trade_result)
      store_new_trade_result(winners, losers, trade_result)
    end

    store_current_trade_result(trade_result)

    set_new_trade_flag
  end

  def store_new_trade_result(winners, losers, trade_result)
    if trade_result.is_not_a_loser
      winners.push(1)
    else
      losers.push(1)
    end
  end

  def set_new_trade_flag
    stored_trades_score = @stored_trades.inject(0, :+)
    if (@required_score >= 0 && stored_trades_score >= @required_score) ||
        (@required_score < 0 && stored_trades_score <= @required_score)
      trade_on = true
    else
      trade_on = false
    end
    trade_on
  end

  def store_current_trade_result(trade_result)
    if trade_result.profit > 0
      @stored_trades.push(1)
    else
      @stored_trades.push(-1)
    end

    if @stored_trades.size > @window_size
      @stored_trades = @stored_trades.drop(1)
    end
  end

  def new_trade_conditions_met(minimum_profit, trade_on, trade_result)
    trade_on && trade_result.profit.abs >= minimum_profit && window_full && trade_result.is_outside_right_band
    #trade_on && trade_result.profit.abs >= minimum_profit && window_full
  end

  def window_full
    @stored_trades.size >= @window_size
  end


end