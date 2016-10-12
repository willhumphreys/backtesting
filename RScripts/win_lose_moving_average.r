library("TTR")
library('ggplot2')

args <- commandArgs(trailingOnly = TRUE)
input <- args[1]
#results/results_bands/ruby/odds_results.csv
output <- args[2]

input <- '/home/whumphreys/code/backtesting/results/normal/data'

file <- 'AUDUSD.csv'

data <- read.table(file.path(input, file), header=T,sep=",")
data$date=as.POSIXct(data$date, tz = "UTC", format="%Y-%m-%dT%H:%M")

data$winLose <- ifelse(data$ticks > 0, 1, -1)

data$sma10 <- SMA(data$winLose,n=30)


ggplot(data=data, aes(x=date, y=sma10)) +
geom_line() +
geom_point() +
geom_hline(yintercept = 0) +
geom_hline(yintercept = -0.5, colour="#990000", linetype="dashed") +
geom_hline(yintercept = 0.5, colour="#990000", linetype="dashed") +
scale_y_continuous(breaks=seq(-1,1,0.05)) +
geom_hline(yintercept = mean(data$sma10, na.rm=TRUE), colour="royalblue1", linetype="dashed") +
ggtitle('10 day sma')
ggsave(file=file.path(output, 'graphs', 'sma10Day.png'))


unique.sma.values <- unique(data$sma10)

unique.sma.values <- unique.sma.values[!is.na(unique.sma.values)]

data[data$sma10 %in% unique.sma.values,]

data[data$sma10 == -0.4,]
