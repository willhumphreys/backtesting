class BollingerOutResult

  def initialize(sma:, symbol:, win_count:, lose_count:, winning_percentage:)
    @sma = sma
    @symbol = symbol
    @win_count = win_count
    @lose_count = lose_count
    @winning_percentage = winning_percentage
  end

  attr_reader :sma, :symbol, :win_count, :lose_count, :winning_percentage
end