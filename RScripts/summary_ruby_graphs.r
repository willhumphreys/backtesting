library(ggplot2)

print('Start summary.r')

args <- commandArgs(trailingOnly = TRUE)

options(width=150)

input = args[1];
output = args[2]

winners_by_year_and_symbol_dir <- 'winners_by_year_and_symbol'

data <- read.table(file.path(input, winners_by_year_and_symbol_dir, "summary.csv"), header=T,sep=",")

generate.plots <- function(data, type) {

  aggreaged_data <- aggregate(data, by=list(data$window.ratio),FUN=mean, na.rm=TRUE)

  ggplot(data=data, aes(x=window.ratio, y=winning.percentage)) +
  geom_bar(colour="black", stat="identity", position = "dodge") +
  geom_hline(yintercept = 50) +
  geom_hline(yintercept = 60, colour="#990000", linetype="dashed") +
  geom_hline(yintercept = 40, colour="#990000", linetype="dashed") +
  geom_hline(yintercept = mean(data$winning.percentage), colour="royalblue1", linetype="dashed") +
  guides(fill=FALSE) +
  ggtitle(paste('window ratio vs winning percentage', type))
  file.name <- paste('windowRatioVsWinningPercentage', type, '.png', sep="")
  ggsave(file=file.path(output, winners_by_year_and_symbol_dir, file.name))
  print(sprintf("Saved %s", file.name))


  ggplot(data=aggreaged_data, aes(x=window.ratio, y=winning.percentage)) +
  geom_bar(colour="black", stat="identity", position = "dodge") +
  geom_hline(yintercept = 50) +
  geom_hline(yintercept = 60, colour="#990000", linetype="dashed") +
  geom_hline(yintercept = 40, colour="#990000", linetype="dashed") +
  geom_hline(yintercept = mean(data$winning.percentage), colour="royalblue1", linetype="dashed") +
  guides(fill=FALSE) +
  ggtitle(paste('window ratio vs winning percentage aggregated', type))
  file.name <- paste('windowRatioVsWinningPercentageAggregated', type, '.png', sep="")
  ggsave(file=file.path(output, winners_by_year_and_symbol_dir, file.name))
  print(sprintf("Saved %s", file.name))


  data$cut_off_moving_average_count <- paste(data$cut_off, data$moving_average_count, sep=" ")


  ggplot(data=data, aes(x=cut_off_moving_average_count, y=winning.percentage)) +
  geom_bar(colour="black", stat="identity", position = "dodge") +
  geom_hline(yintercept = 50) +
  geom_hline(yintercept = 60, colour="#990000", linetype="dashed") +
  geom_hline(yintercept = 40, colour="#990000", linetype="dashed") +
  geom_hline(yintercept = mean(data$winning.percentage), colour="royalblue1", linetype="dashed") +
  guides(fill=FALSE) +
  theme(axis.text.x = element_text(angle = 90, hjust = 1)) +
  ggtitle(paste('cut off moving average count vs winning percentage', type))
  file.name <- paste('cutOffMovingAverageCountVsWinningPercentageAggregated', type, '.png', sep="")
  ggsave(file=file.path(output, winners_by_year_and_symbol_dir, file.name))
  print(sprintf("Saved %s", file.name))

}

generate.plots(data,'all')

negative_cut_off <- data[data$cut_off < 0, ]
generate.plots(negative_cut_off,'negative_cut_off')

positive_cut_off <- data[data$cut_off > 0, ]
generate.plots(positive_cut_off,'positive_cut_off')

print('Finished summary.r')



