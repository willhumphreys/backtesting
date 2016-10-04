require 'probability_engine/version'

class Options

  attr_reader :options

  def parse
    @options = {:input_directory => nil, :output_directory => nil, :profile => nil, :data_set => nil, :ruby_graph_output => nil}

    parser = OptionParser.new do |opts|

      opts.on('-p', '--profile profile', 'Profile') do |profile|
        @options[:profile] = profile;
      end

      opts.on('-d', '--data_set data_set', 'Data Set') do |data_set|
        @options[:data_set] = data_set
        @options[:output_directory] = "../results/#{data_set}/ruby"
        @options[:ruby_graph_output] = "../results/#{data_set}/graphs"
        @options[:input_directory] = "../results/#{data_set}/data"
      end

      opts.on('-h', '--help', 'Displays Help') do
        puts opts
        exit
      end
    end

    parser.parse!

    raise OptionParser::MissingArgument if @options[:data_set].nil?
    raise OptionParser::MissingArgument if @options[:profile].nil?

    @options
  end
end
