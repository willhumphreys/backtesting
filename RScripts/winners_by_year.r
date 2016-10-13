library(R.utils)
library(plyr)
library(reshape2)
library(ggplot2)

cat("Executing winners by year\n")

args <- commandArgs(trailingOnly = TRUE)

options(width=200)

#The input and output are the same and point to results directory. e.g. results-bands and normal

input = args[1];
output = args[2]

graph.output.dir <- file.path(output, "graphs")
dir.create(graph.output.dir, recursive = TRUE)

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

winners_by_year <- (plyr::count(symbols_merged, c('symbol', 'years', 'winner')))

# winners by year and symbol

winners_fixed <- dcast(winners_by_year, symbol + years ~ winner)
winners_fixed[is.na(winners_fixed)] <- 0
winners_fixed <- rename(winners_fixed, c("FALSE"="losers", "TRUE"="winners"))
#print(head(winners_by_year))

winners_fixed$win_ratio <- round(winners_fixed$winners / winners_fixed$losers, digits = 3)
winners_fixed$win_percentage <- round((winners_fixed$winners / (winners_fixed$losers + winners_fixed$winners)) * 100, digits = 3)

write.table(winners_fixed, file=file.path(output, 'winners_by_year.csv'), sep=",", row.names=FALSE)


ggplot(data=winners_fixed, aes(x=years, y=win_percentage, group=symbol)) +
    geom_line(aes(colour=symbol)) +
    geom_point(aes(colour=symbol)) +
    geom_hline(yintercept = 50) +
    geom_hline(yintercept = 60, colour="#990000", linetype="dashed") +
    geom_hline(yintercept = 40, colour="#990000", linetype="dashed") +
    scale_y_continuous(breaks=seq(0,100,5)) +
    geom_hline(yintercept = mean(winners_fixed$win_percentage, na.rm=TRUE), colour="royalblue1", linetype="dashed") +
    ggtitle('winners by year and symbol')
ggsave(file=file.path(output, 'graphs', 'winners_by_year_and_symbol.png'))

ggplot(data=winners_fixed, aes(x=years, y=win_percentage, fill=symbol)) +
    geom_bar(colour="black", stat="identity") +
    facet_wrap( ~ symbol, ncol = 2) +
    theme(axis.text.x = element_text(angle = 90, hjust = 1)) +
    geom_hline(yintercept = 50) +
    geom_hline(yintercept = 60, colour="#990000", linetype="dashed") +
    geom_hline(yintercept = 40, colour="#990000", linetype="dashed") +
    geom_hline(yintercept = mean(winners_fixed$win_percentage, na.rm=TRUE), colour="royalblue1", linetype="dashed") +
    scale_y_continuous(breaks=seq(0,100,20)) +
    guides(fill=FALSE) +
    ggtitle('winners_by_symbolwinners by year and symbol facet')
ggsave(file=file.path(output, 'graphs', 'winners_by_year_and_symbol_facet.png'))

# Winners by symbol

winners_by_symbol <- aggregate(cbind(winners_fixed$winners, winners_fixed$losers), list(symbol = winners_fixed$symbol), sum)
winners_by_symbol <- rename(winners_by_symbol, c("V1"="winners", "V2"="losers"))
winners_by_symbol$win_ratio <- round(winners_by_symbol$winners / winners_by_symbol$losers, digits = 3)
winners_by_symbol$win_percentage <- round((winners_by_symbol$winners / (winners_by_symbol$losers + winners_by_symbol$winners)) * 100, digits = 3)

ggplot(data=winners_by_symbol, aes(x=symbol, y=win_percentage, fill=symbol)) +
    geom_bar(colour="black", stat="identity") +
    guides(fill=FALSE) +
    geom_hline(yintercept = 50) +
    geom_hline(yintercept = 60, colour="#990000", linetype="dashed") +
    geom_hline(yintercept = 40, colour="#990000", linetype="dashed") +
    geom_hline(yintercept = mean(winners_by_symbol$win_percentage, na.rm=TRUE), colour="royalblue1", linetype="dashed") +
    scale_y_continuous(breaks=seq(0,100,5)) +
    ggtitle('winners by symbol')
ggsave(file=file.path(output, 'graphs', 'winners_by_symbol.png'))

# Winners by year

winners_by_year <- aggregate(cbind(winners_fixed$winners, winners_fixed$losers), list(year = winners_fixed$years), sum)
winners_by_year <- rename(winners_by_year, c("V1"="winners", "V2"="losers"))
winners_by_year$win_ratio <- round(winners_by_year$winners / winners_by_year$losers, digits = 3)
winners_by_year$win_percentage <- round((winners_by_year$winners / (winners_by_year$losers + winners_by_year$winners)) * 100, digits = 3)

ggplot(data=winners_by_year, aes(x=year, y=win_percentage, fill=year)) +
    geom_bar(colour="black", stat="identity") +
    geom_hline(yintercept = 50) +
    geom_hline(yintercept = 60, colour="#990000", linetype="dashed") +
    geom_hline(yintercept = 40, colour="#990000", linetype="dashed") +
    geom_hline(yintercept = mean(winners_by_year$win_percentage, na.rm=TRUE), colour="royalblue1", linetype="dashed") +
    scale_y_continuous(breaks=seq(0,100,5)) +
    guides(fill=FALSE) +
    ggtitle('winners by year')
ggsave(file=file.path(output, 'graphs', 'winners_by_year.png'))


cat("Finished executing winners by year.r\n")
