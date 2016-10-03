class Profiles

  attr_reader :data

  # moving_average_counts: How big is the moving average window.
  # cut_offs: How successful do the trades need to be.
  # minimum_profits: What is the minimum profit our new trade needs to be traded.
  def initialize

    full_profile = Profile.new(moving_average_counts: 2.step(10, 2).to_a,
                               cut_offs: -10.step(10, 1).to_a,
                               minimum_profits: 2.step(100, 5).to_a)



    quick_profile = Profile.new(moving_average_counts: 8.step(10, 2).to_a,
                                cut_offs: -10.step(10, 5).to_a,
                                minimum_profits: 80.step(100, 5).to_a)

    @data = {:full => full_profile, :quick => quick_profile}

  end
end
