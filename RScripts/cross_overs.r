library(ggplot2)
library(plyr)

print('Start Crossover graphs')

args <- commandArgs(trailingOnly = TRUE)

options(width=150)

input = args[1];
output = args[2]

# input <- 'results/normal'
# output <- 'results/normal'

file.name <- 'crossovers.csv'

data.out <- file.path(output, 'cross_overs')
dir.create(data.out)

data.path <- file.path(input, file.name)
print(data.path)
data <- read.table(data.path, header=T,sep=",")

ggplot(data=data, aes(x=sma, y=winning_percentage)) +
geom_bar(aes(colour=symbol), stat="identity") +
facet_wrap( ~ symbol, ncol = 2) +
geom_hline(yintercept = 50) +
geom_hline(yintercept = 60, colour="#990000", linetype="dashed") +
geom_hline(yintercept = 40, colour="#990000", linetype="dashed") +
geom_hline(yintercept = mean(data$winning_percentage, na.rm=TRUE), colour="royalblue1", linetype="dashed") +
geom_hline(yintercept = weighted.mean(data$winning_percentage, data$win_count + data$lose_count, na.rm=TRUE), colour="darkorange3", linetype="dashed") +
scale_y_continuous(breaks=seq(0,100,20)) +
theme(axis.text.x = element_text(angle = 90, hjust = 1)) +
guides(fill=FALSE) +
ggtitle(paste('cross overs facet'))
file.name <- 'CrossOvers.png'
ggsave(file=file.path(output, 'graphs', file.name))
print(sprintf("Saved %s", file.name))


generate.plots <- function(sma) {
  filtered.data <- data[data$sma == sma,]
  ggplot(data=filtered.data, aes(x=symbol, y=winning_percentage)) +
  geom_bar(aes(colour=sma), stat="identity") +
  geom_hline(yintercept = 50) +
  geom_hline(yintercept = 60, colour="#990000", linetype="dashed") +
  geom_hline(yintercept = 40, colour="#990000", linetype="dashed") +
  geom_hline(yintercept = mean(data$winning_percentage, na.rm=TRUE), colour="royalblue1", linetype="dashed") +
  geom_hline(yintercept = weighted.mean(data$winning_percentage, data$win_count + data$lose_count, na.rm=TRUE), colour="darkorange3", linetype="dashed") +
  scale_y_continuous(breaks=seq(0,100,20)) +
  theme(axis.text.x = element_text(angle = 90, hjust = 1)) +
  guides(fill=FALSE) +
  ggtitle(paste('cross overs facet by symbol ', sma))
  file.name <- paste('CrossOvers_Symbol_', sma, '.png', sep="")
  ggsave(file=file.path(output, 'graphs', file.name))
  print(sprintf("Saved %s", file.name))
}

lapply(unique(data$sma), function(x) generate.plots(x))

#Data parsing
#Overall mean
weigthed.mean <- weighted.mean(data$winning_percentage, data$win_count + data$lose_count, na.rm=TRUE)
mean <- mean(data$winning_percentage, na.rm=TRUE)

#Mean per symbol
symbol.mean <- aggregate(list(mean = data$winning_percentage), list(symbol = data$symbol), mean)
symbol.weighted.mean <- ddply(data, .(symbol), function(x) data.frame(weighted.mean=weighted.mean(x$win_count + x$lose_count, x$winning_percentage)))


trade.counts <- aggregate(list(win.count = data$win_count, lose.count = data$lose_count, trade.count = data$win_count + data$lose_count),
  list(symbol = data$symbol), sum)

symbol.means <- merge(symbol.mean, symbol.weighted.mean, by='symbol')

symbol.means <- merge(symbol.means, trade.counts, by='symbol')

write.table(symbol.means, file=file.path(data.out, 'symbol_cross_overmeans.csv'), sep=",", row.names=FALSE)

sma.mean <- aggregate(list(mean = data$winning_percentage), list(sma = data$sma), mean)
sma.weighted.mean <- ddply(data, .(sma), function(x) data.frame(weighted.mean=weighted.mean(x$win_count + x$lose_count, x$winning_percentage)))
sma.means <- merge(sma.mean, sma.weighted.mean, by='sma')

trade.counts.sma <- aggregate(list(win.count = data$win_count, lose.count = data$lose_count, trade.count = data$win_count + data$lose_count),
  list(sma = data$sma), sum)
sma.means <- merge(sma.means, trade.counts.sma, by='sma')

write.table(sma.means, file=file.path(data.out, 'sma_crossover_means.csv'), sep=",", row.names=FALSE)
