class BollingerOutResult

  def initialize(sma:, win_count:, lose_count:, winning_percentage:)
    @sma = sma
    @win_count = win_count
    @lose_count = lose_count
    @winning_percentage = winning_percentage
  end

  attr_reader :sma, :win_count, :lose_count, :winning_percentage
end