require 'probability_engine/version'

class Processor
  attr_reader :name, :processor_function

  def initialize(name, processor_function)
    @name = name
    @processor_function = processor_function
  end

end