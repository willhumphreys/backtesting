library(R.utils)
library(plyr)
library(reshape2)
library(ggplot2)

cat("Executing winners by year.r\n")

args <- commandArgs(trailingOnly = TRUE)

options(width=200)


input = args[1];
output = args[2]

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

#print(xtabs(~ symbol + years, symbols_merged))

winners_by_year <- (count(symbols_merged, c('symbol', 'years', 'winner')))

winners_fixed <- dcast(winners_by_year, symbol + years ~ winner)
winners_fixed[is.na(winners_fixed)] <- 0
winners_fixed <- rename(winners_fixed, c("FALSE"="losers", "TRUE"="winners"))
#print(head(winners_by_year))

winners_fixed$win_ratio <- round(winners_fixed$winners / winners_fixed$losers, digits = 3)
winners_fixed$win_percentage <- round((winners_fixed$winners / (winners_fixed$losers + winners_fixed$winners)) * 100, digits = 3)

write.table(winners_fixed, file=output, sep=",", row.names=FALSE)

ggplot(data=winners_fixed, aes(x=years, y=win_percentage, group=symbol)) +
    geom_line(aes(colour=symbol)) +
    geom_point(aes(colour=symbol)) +
    ggtitle(output)
ggsave(file=paste(output, '.png', sep=""))

cat("Finished executing winners by year.r\n")