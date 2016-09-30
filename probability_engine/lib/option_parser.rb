require 'probability_engine/version'

class Options

  attr_reader :options

  def parse
    @options = {:input_directory => nil, :output_directory => nil}

    parser = OptionParser.new do |opts|
      opts.banner = 'Usage: [options]'
      opts.on('-i', '--input_directory input_directory ', 'InputDirectory') do |input_directory|
        @options[:input_directory] = input_directory;
      end

      opts.on('-o', '--output_directory output_directory', 'OutputDirectory') do |output_directory|
        @options[:output_directory] = output_directory;
      end

      opts.on('-h', '--help', 'Displays Help') do
        puts opts
        exit
      end
    end

    parser.parse!

    raise OptionParser::MissingArgument if @options[:input_directory].nil?
    raise OptionParser::MissingArgument if @options[:output_directory].nil?

    @options
  end
end
