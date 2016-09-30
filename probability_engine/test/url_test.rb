require 'minitest/autorun'
require 'probability_engine'

class UrlTest < Minitest::Test
  def test_url
    assert_equal "http://howistart.org/posts/ruby/1", Probability_Engine::Url
  end
end