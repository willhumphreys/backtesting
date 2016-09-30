require 'probability_engine/version'

class Results

  def initialize(winning_percentage , cut_off_percentage, winners, losers)
    @winning_percentage = winning_percentage
    @cut_off_percentage = cut_off_percentage
    @winners = winners
    @losers = losers
  end

  attr_reader :winning_percentage, :cut_off_percentage, :winners, :losers

  end