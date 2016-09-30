require 'probability_engine/version'
require 'csv'

class MT4FileRepo

  @mt4_quote_mapper

  def initialize(mapper)
    @mt4_quote_mapper = mapper

  end

  public def read_quotes(file)

    mapped_quotes = Array.new

    first_row = true

    #file = 'data/AUDUSD10080.csv'
    #file = 'data/USDCAD1440.csv'
    puts "Reading file #{file}"

    CSV.foreach(file) do |row|

      if first_row
        first_row = false
      else

        mapped_quotes.push(@mt4_quote_mapper.map(row))
      end



    end
    mapped_quotes
  end

end
