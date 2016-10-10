library(R.utils)
library(plyr)
library(reshape2)
library(ggplot2)
library(R.utils)

cat("Executing winners by year frequency\n")

args <- commandArgs(trailingOnly = TRUE)

options(width=200)

#The input and output are the same and point to results directory. e.g. results-bands and normal

input = args[1];
output = args[2]

#input = '/home/whumphreys/code/backtesting/results/normal'

file.in <- 'odds_results.csv'

data <- read.table(file.path(input, file.in), header=T,sep=",")

data$start_date=as.POSIXct(data$start_date, tz = "UTC")
data$years <- format(data$start_date,"%Y")

print('create output directories')
winners_by_year_and_symbol_dir <- file.path(output, 'winners_by_year_and_symbol')
dir.create(winners_by_year_and_symbol_dir)
dir.create(file.path(winners_by_year_and_symbol_dir, 'by_year_and_symbol'))
dir.create(file.path(winners_by_year_and_symbol_dir, 'by_year_and_symbol_facet'))
dir.create(file.path(winners_by_year_and_symbol_dir, 'by_symbol_facet'))
dir.create(file.path(winners_by_year_and_symbol_dir, 'by_year_facet'))
print('finished creating output directories')


generate.stats <- function(cut_off, moving_average_count, filtered_data) {


  filtered_data <- data[data$cut_off == cut_off & data$moving_average_count == moving_average_count, ]

  winners.count <- sum(filtered_data$winners.size)
  losers.count <- sum(filtered_data$losers.size)
  win.lose.count <- sum(filtered_data$winners.size - filtered_data$losers.size)
  trade.count <- sum(filtered_data$winners.size + filtered_data$losers.size)
  print(sprintf("MovingAveragecount: %d CutOff: %d winWinLoseCount: %d tradeCount %d", moving_average_count, cut_off, win.lose.count, trade.count))

  return(c(cut_off, moving_average_count, winners.count, losers.count, win.lose.count, trade.count))
}

generate.plot <- function(cut_off, moving_average_count, data) {

  print(sprintf("MovingAveragecount: %d CutOff: %d", moving_average_count, cut_off))

  filtered_data <- data[data$cut_off == cut_off & data$moving_average_count == moving_average_count, ]



  # Winners by year and symbol

  ggplot(data=filtered_data, aes(x=years, y=winning_percentage, group=data_set)) +
  geom_line(aes(colour=data_set)) +
  geom_point(aes(colour=data_set)) +
  geom_hline(yintercept = 50) +
  geom_hline(yintercept = 60, colour="#990000", linetype="dashed") +
  geom_hline(yintercept = 40, colour="#990000", linetype="dashed") +
  ggtitle(paste('winners by year and symbol CO', cut_off, ' MA_', moving_average_count))
  file.name <- paste('winners_by_year_and_symbol_CO_', cut_off, '_MA_', moving_average_count, '.png',sep="")
  ggsave(file=file.path(winners_by_year_and_symbol_dir, 'by_year_and_symbol', file.name))
paste('winners_by_year_facet_CO_', cut_off, '_MA_', moving_average_count, '.png', sep="")
  print(sprintf("Saved %s", file.name))

  # Winners by year and symbol facet

  ggplot(data=filtered_data, aes(x=years, y=winning_percentage, fill=data_set)) +
  geom_bar(colour="black", stat="identity") +
  facet_wrap( ~ data_set, ncol = 2) +
  theme(axis.text.x = element_text(angle = 90, hjust = 1)) +
  geom_hline(yintercept = 50) +
  geom_hline(yintercept = 60, colour="#990000", linetype="dashed") +
  geom_hline(yintercept = 40, colour="#990000", linetype="dashed") +
  guides(fill=FALSE) +
  ggtitle(paste('winners by year and symbol facet CO ', cut_off, ' MA_', moving_average_count))
  file.name <- paste('winners_by_year_and_symbol_facet_CO_', cut_off, '_MA_', moving_average_count, '.png', sep="")
  ggsave(file=file.path(winners_by_year_and_symbol_dir, 'by_year_and_symbol_facet', file.name))

  print(sprintf("Saved %s", file.name))

  winners_by_symbol <- aggregate(cbind(filtered_data$winners, filtered_data$losers.size), list(data_set = filtered_data$data_set), sum)
  winners_by_symbol <- rename(winners_by_symbol, c("V1"="winners", "V2"="losers"))
  winners_by_symbol$win_ratio <- round(winners_by_symbol$winners / winners_by_symbol$losers, digits = 3)
  winners_by_symbol$win_percentage <- round((winners_by_symbol$winners / (winners_by_symbol$losers + winners_by_symbol$winners)) * 100, digits = 3)

  ggplot(data=winners_by_symbol, aes(x=data_set, y=win_percentage, fill=data_set)) +
  geom_bar(colour="black", stat="identity") +
  guides(fill=FALSE) +
  geom_hline(yintercept = 50) +
  geom_hline(yintercept = 60, colour="#990000", linetype="dashed") +
  geom_hline(yintercept = 40, colour="#990000", linetype="dashed") +
  scale_y_continuous(limits=c(0, 100)) +
  ggtitle(paste('winners by symbol facet CO ', cut_off, ' MA_', moving_average_count))
  file.name <- paste('winners_by_symbol_facet_CO_', cut_off, '_MA_', moving_average_count, '.png', sep="")
  ggsave(file=file.path(winners_by_year_and_symbol_dir,'by_symbol_facet', file.name))

  print(sprintf("Saved %s", file.name))

  winners_by_year <- aggregate(cbind(filtered_data$winners, filtered_data$losers), list(year = filtered_data$years), sum)
  winners_by_year <- rename(winners_by_year, c("V1"="winners", "V2"="losers"))
  winners_by_year$win_ratio <- round(winners_by_year$winners / winners_by_year$losers, digits = 3)
  winners_by_year$win_percentage <- round((winners_by_year$winners / (winners_by_year$losers + winners_by_year$winners)) * 100, digits = 3)

  ggplot(data=winners_by_year, aes(x=year, y=win_percentage, fill=year)) +
  geom_bar(colour="black", stat="identity") +
  geom_hline(yintercept = 50) +
  geom_hline(yintercept = 60, colour="#990000", linetype="dashed") +
  geom_hline(yintercept = 40, colour="#990000", linetype="dashed") +
  guides(fill=FALSE) +
  ggtitle(paste('winners by year facet CO ', cut_off, ' MA_', moving_average_count))
  file.name <- paste('winners_by_year_facet_CO_', cut_off, '_MA_', moving_average_count, '.png', sep="")
  ggsave(file=file.path(winners_by_year_and_symbol_dir, 'by_year_facet', file.name))
  print(sprintf("Saved %s", file.name))

}
#plyr::count(data_min_profit, c('cut_off', 'moving_average_count'))

filtered_data <- data[data$minimum_profit == 2, ]

unique_cut_offs <- unique(filtered_data[c("cut_off", "moving_average_count")])

apply(unique_cut_offs, 1, function(x) generate.plot(x[1], x[2], filtered_data))

stats <- as.data.frame(t(apply(unique_cut_offs, 1, function(x) generate.stats(x[1], x[2], filtered_data))))
stats <- plyr::rename(stats, c("V3"="winners.count", "V4"="losers.count", "V5"="win.lose.count", "V6"="trade.count"))

stats$win.ratio <- round(stats$winners.count / stats$losers.count, digits = 3)
stats$winning.percentage <- round((stats$winners.count / (stats$winners.count + stats$losers.count)) * 100, digits = 3)
stats$window.ratio <- stats$cut_off / stats$moving_average_count

write.table(stats, file=file.path(winners_by_year_and_symbol_dir, "summary.csv"), sep=",", row.names=FALSE)

cat("Finished executing winners by year.r\n")
