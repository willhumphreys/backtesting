
class Profile

  attr_reader :moving_average_counts, :minimum_profits, :cut_offs

  def initialize(moving_average_counts:, cut_offs:, minimum_profits:)
    @moving_average_counts = moving_average_counts
    @cut_offs = cut_offs
    @minimum_profits = minimum_profits
  end



end
