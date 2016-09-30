require 'probability_engine/version'
require 'csv'


def read_quotes(file)

  mapped_quotes = Array.new

  first_row = true

  #file = 'data/AUDUSD10080.csv'
  #file = 'data/USDCAD1440.csv'
  puts "Reading file #{file}"

  CSV.foreach(file) do |row|

    if first_row
      first_row = false
    else
      #25/07/2016
      date = DateTime.strptime("#{row[0]}", '%d/%m/%Y')
      mapped_quotes.push(date)
    end
  end
  mapped_quotes
end