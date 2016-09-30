require 'minitest/autorun'
require 'probability_engine'
require_relative '../../probability_engine/lib/quote'

describe 'Test the logic of the higher high close inside' do

  before do
    @processor_map = Processors.new.processors

    @first_quote = Quote.new(
        symbol: 'AUDUSD',
        timestamp: '1234',
        trading_day: '1',
        open: 5,
        high: 12,
        low: 4,
        close: 9,
        volume: 10,
        open_interest: 1
    )

    @second_quote = Quote.new(
        symbol: 'AUDUSD',
        timestamp: '1234',
        trading_day: '1',
        open: 7,
        high: 15,
        low: 3,
        close: 13,
        volume: 10,
        open_interest: 1
    )

  end

  it 'should return nil if we do not put in a higher high' do
    processor = @processor_map[:higher_high_close_inside]
    @second_quote = Quote.new(
        symbol: 'AUDUSD',
        timestamp: '1234',
        trading_day: '1',
        open: 7,
        high: 10,
        low: 3,
        close: 13,
        volume: 10,
        open_interest: 1
    )

    expect(processor.processor_function.call(@first_quote, @second_quote, 'AUDUSD')).must_equal(nil)
  end

  it 'should return nil if we put in a higher high but still close outside.' do
    processor = @processor_map[:higher_high_close_inside]
    @second_quote = Quote.new(
        symbol: 'AUDUSD',
        timestamp: '1234',
        trading_day: '1',
        open: 7,
        high: 15,
        low: 3,
        close: 13,
        volume: 10,
        open_interest: 1
    )

    # 15 > 12
    expect(processor.processor_function.call(@first_quote, @second_quote, 'AUDUSD')).must_equal(nil)
  end

  it 'should return the difference between highs if we put in a higher high and close inside the range.' do
    processor = @processor_map[:higher_high_close_inside]
    @second_quote = Quote.new(
        symbol: 'AUDUSD',
        timestamp: '1234',
        trading_day: '1',
        open: 7,
        high: 16,
        low: 3,
        close: 11,
        volume: 10,
        open_interest: 1
    )

    #16 - 12
    expect(processor.processor_function.call(@first_quote, @second_quote, 'AUDUSD')).must_equal(40000)
  end
end