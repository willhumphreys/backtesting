library(ggplot2)

print('Start Outside Bollinger graphs')

args <- commandArgs(trailingOnly = TRUE)

options(width=150)

input = args[1];
output = args[2]

input <- 'results/normal'
output <- 'results/normal/graphs'

file.name <- '/outside_bollinger.csv'

data.path <- file.path(input, file.name)
print(data.path)
data <- read.table(data.path, header=T,sep=",")

ggplot(data=data, aes(x=symbol, y=winning_percentage)) +
geom_bar(aes(colour=symbol), stat="identity") +
facet_wrap( ~ sma, ncol = 2) +
geom_hline(yintercept = 50) +
geom_hline(yintercept = 60, colour="#990000", linetype="dashed") +
geom_hline(yintercept = 40, colour="#990000", linetype="dashed") +
geom_hline(yintercept = mean(data$winning_percentage, na.rm=TRUE), colour="royalblue1", linetype="dashed") +
geom_hline(yintercept = weighted.mean(data$winning_percentage, data$win_count + data$lose_count, na.rm=TRUE), colour="darkorange3", linetype="dashed") +
scale_y_continuous(breaks=seq(0,100,20)) +
theme(axis.text.x = element_text(angle = 90, hjust = 1)) +
guides(fill=FALSE) +
ggtitle(paste('outside bollingers facet'))
file.name <- 'OutsideBollingers.png'
ggsave(file=file.path(output, file.name))
print(sprintf("Saved %s", file.name))

#Data parsing
weigthed.mean <- weighted.mean(data$winning_percentage, data$win_count + data$lose_count, na.rm=TRUE)
mean <- mean(data$winning_percentage, na.rm=TRUE)