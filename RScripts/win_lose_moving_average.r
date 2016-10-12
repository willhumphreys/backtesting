library("TTR")
library('ggplot2')

args <- commandArgs(trailingOnly = TRUE)
input <- args[1]
#results/results_bands/ruby/odds_results.csv
output <- args[2]

input <- '/home/whumphreys/code/backtesting/results/normal'
output <- '/home/whumphreys/code/backtesting/results/normal'

graph.output <- file.path(output, 'graphs/bollingers')
data.output <- file.path(output, 'data_bollingers')

dir.create(graph.output, recursive = TRUE)
dir.create(data.output, recursive = TRUE)

input.files <- list.files(file.path(input, 'data'))

generate.bollinger.data <- function(file.in) {

  symbol <- strsplit(file.in, split='.', fixed=TRUE)[[1]][1]

  data <- read.table(file.path(input, 'data', file.in), header=T,sep=",")
  data$date=as.POSIXct(data$date, tz = "UTC", format="%Y-%m-%dT%H:%M")

  data$winLose <- ifelse(data$ticks > 0, 1, -1)

  data$sma <- SMA(data$winLose,n=40)

  bands <- data.frame(BBands( data[,c("sma")], n = 70 , sd=2.0))

  data$downBB <- bands$dn
  data$upBB <- bands$up
  data$mavg <- bands$mavg

  ggplot(data=data, aes(date)) +
  geom_line(aes(y=sma)) +
  geom_line(aes(y=downBB)) +
  geom_line(aes(y=upBB)) +
  geom_line(aes(y=mavg)) +
  geom_point(aes(y=sma)) +
  geom_hline(yintercept = 0) +
  geom_hline(yintercept = -0.5, colour="#990000", linetype="dashed") +
  geom_hline(yintercept = 0.5, colour="#990000", linetype="dashed") +
  scale_y_continuous(breaks=seq(-1,1,0.05)) +
  geom_hline(yintercept = mean(data$sma10, na.rm=TRUE), colour="royalblue1", linetype="dashed") +
  ggtitle('10 day sma')
  ggsave(file=file.path(graph.output, paste(symbol, '.png', sep="")))

  write.table(data, file=file.path(data.output, paste(symbol, '-bollingers.csv', sep="")), sep=",", row.names=FALSE)

}
sapply(input.files, function(x) generate.bollinger.data(x))
