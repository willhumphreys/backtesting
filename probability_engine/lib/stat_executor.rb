require 'probability_engine/version'
require_relative 'candle_operations'
require_relative 'processor'
require 'fileutils'

class Stat_Executor

  def initialize(contract, processor)
    @candle_ops = CandleOperations.new
    @processor = processor
    @contract = contract

    FileUtils.mkdir_p 'out'

    @out_csv_file = "out/#{contract}_#{@processor.name}.csv"

    if File.exist?(@out_csv_file)
      File.truncate(@out_csv_file, 0)
    end

    write_file_header
  end

  def write_file_header
    open(@out_csv_file, 'a') do |f|
      f.puts 'date.time, pips'
    end
  end

  def process_and_write(first, second)
    result = @processor.processor_function.call(first, second, @contract)
    if result != nil
      write(result, second.timestamp)
    end
  end

  def write(high_diff, timestamp)
    open(@out_csv_file, 'a') do |f|
      f.puts "#{timestamp}, #{high_diff}"
    end
  end
end