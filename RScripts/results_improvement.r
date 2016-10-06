library(R.utils)
library(plyr)

args <- commandArgs(trailingOnly = TRUE)

options(width=150)


input = args[1];
output = args[2]

cat("Executing results_improvement.r\n")

data <- read.table(file.path(input, 'results.csv'), header=T,sep=",")

data$winner_ratio <- round(data$winners / data$losers, digits = 4)

data$winning_percentage <- round((data$winners / (data$winners + data$losers) * 100), digits = 2)
data$losing_percentage <- round((data$losers / (data$winners + data$losers) * 100), digits = 2)

cat(nrow(data))


combined_symbol_data <- data.frame(symbol = character(0), tradeCount= integer(0))

generate.symbol.info <- function(file.in) {

  symbol <- strsplit(file.in, split='.', fixed=TRUE)[[1]][1]
  symbol_data <- read.table(file.path(input, 'data', file.in), header=T,sep=",")
  symbol_data$symbol <- rep(symbol,nrow(symbol_data))
  return(symbol_data)

}

input.files <- list.files(file.path(input, 'data'))

with_symbols <- lapply(input.files, function(x) generate.symbol.info(x))

symbols_merged <- do.call(rbind, with_symbols)
symbols_merged$date=as.POSIXct(symbols_merged$date, tz = "UTC", format="%Y-%m-%dT%H:%M")
symbols_merged$years <- format(as.Date(symbols_merged$date, format="%d/%m/%Y"),"%Y")
symbols_merged$winner <- symbols_merged$ticks > 0
years <- unique(symbols_merged$years)

print(xtabs(~ symbol + years, symbols_merged))

winners_by_year <- (count(symbols_merged, c('symbol', 'years', 'winner')))

print(head(winners_by_year))
#print(tail(symbols_merged))

print(head(data))

print(head(merge(data, winners_by_year,by="symbol")))

write.table(data, file=output, sep=",", row.names=FALSE)

cat("Finished executing cumulative_profit.r\n")

