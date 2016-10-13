library("TTR")
library('ggplot2')

args <- commandArgs(trailingOnly = TRUE)
input <- args[1]
#results/results_bands/ruby/odds_results.csv
output <- args[2]

#input <- '/home/whumphreys/code/backtesting/results/normal'
#output <- '/home/whumphreys/code/backtesting/results/normal'



generate.all.crossovers <- function(long_sma, short_sma) {

  print(sprintf('Generate crossover graphs and data for %d %d', long_sma, short_sma))

  crossovers <- paste(long_sma, short_sma, sep="_")

  graph.output <- file.path(output, 'graphs/crossovers', crossovers)
  data.output <- file.path(output, 'data_crossovers', crossovers)

  dir.create(graph.output, recursive = TRUE)
  dir.create(data.output, recursive = TRUE)

  input.files <- list.files(file.path(input, 'data'))

  generate.bollinger.data <- function(file.in) {

    symbol <- strsplit(file.in, split='.', fixed=TRUE)[[1]][1]

    data <- read.table(file.path(input, 'data', file.in), header=T,sep=",")
    data$date=as.POSIXct(data$date, tz = "UTC", format="%Y-%m-%dT%H:%M")

    data$winLose <- ifelse(data$ticks > 0, 1, -1)

    data$long.crossover <- SMA(data$winLose,n=long_sma)
    data$short.crossover <- SMA(data$winLose,n=short_sma)

    ggplot(data=data, aes(date)) +
    geom_line(aes(y=long.crossover, colour="darkred")) +
    geom_line(aes(y=short.crossover, colour="green3")) +
    geom_hline(yintercept = 0) +
    geom_hline(yintercept = -0.5, colour="#990000", linetype="dashed") +
    geom_hline(yintercept = 0.5, colour="#990000", linetype="dashed") +
    scale_y_continuous(breaks=seq(-1,1,0.05)) +
    geom_hline(yintercept = mean(data$sma10, na.rm=TRUE), colour="royalblue1", linetype="dashed") +
    ggtitle(paste(crossovers, ' crossover', sep=""))
    ggsave(file=file.path(graph.output, paste(symbol, '.png', sep="")))

    write.table(data, file=file.path(data.output, paste(symbol, '-crossovers.csv', sep="")), sep=",", row.names=FALSE)

  }
  sapply(input.files, function(x) generate.bollinger.data(x))
}

moving_average_lengths <- seq(10,100, 10)
moving_average_combinations <- expand.grid(long.sma = moving_average_lengths,short.sma = moving_average_lengths)
moving_average_combinations <- moving_average_combinations[moving_average_combinations$long.sma > moving_average_combinations$short.sma, ]

apply(moving_average_combinations, 1, function(x) generate.all.crossovers(x[1], x[2]))