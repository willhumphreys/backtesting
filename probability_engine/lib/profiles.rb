class Profiles

  attr_reader :data

  def initialize

    moving_average_counts = 2.step(10, 2).to_a # How big is the moving average window.
    cut_offs = -10.step(10, 1).to_a # How successful do the trades need to be.
    minimum_profits = 2.step(100, 5).to_a # What is the minimum profit our new trade needs to be traded.

    full_profile = Profile.new(moving_average_counts: moving_average_counts, cut_offs: cut_offs, minimum_profits: minimum_profits)

    moving_average_counts = 8.step(10, 2).to_a # How big is the moving average window.
    cut_offs = -10.step(10, 5).to_a # How successful do the trades need to be.
    minimum_profits = 80.step(100, 5).to_a # What is the minimum profit our new trade needs to be traded.

    quick_profile = Profile.new(moving_average_counts: moving_average_counts, cut_offs: cut_offs, minimum_profits: minimum_profits)

    @data = {:full => full_profile, :quick => quick_profile}

  end
end
