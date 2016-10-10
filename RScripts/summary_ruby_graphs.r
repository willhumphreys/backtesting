library(ggplot2)

print('Start summary ruby graphs')

args <- commandArgs(trailingOnly = TRUE)

options(width=150)

input = args[1];
output = args[2]

winners_by_year_and_symbol_dir <- '../graphs/winners_by_year_and_symbol'
winners_by_year_and_symbol_graph_dir <- file.path(winners_by_year_and_symbol_dir, 'summary_graphs')
dir.create(file.path(output, winners_by_year_and_symbol_graph_dir), showWarnings = FALSE)

generate.plots <- function(data, type) {

  ggplot(data=data, aes(x=window.ratio, y=winning.percentage)) +
  geom_bar(colour="black", stat="identity", position = "dodge") +
  geom_hline(yintercept = 50) +
  geom_hline(yintercept = 60, colour="#990000", linetype="dashed") +
  geom_hline(yintercept = 40, colour="#990000", linetype="dashed") +
  geom_hline(yintercept = mean(data$winning.percentage, na.rm=TRUE), colour="royalblue1", linetype="dashed") +
  geom_hline(yintercept = weighted.mean(data$winning.percentage, data$trade.count, na.rm=TRUE), colour="darkorange3", linetype="dashed") +
  guides(fill=FALSE) +
  ggtitle(paste('window ratio vs winning percentage', type))
  file.name <- paste('windowRatioVsWinningPercentage', type, '.png', sep="")
  ggsave(file=file.path(output, winners_by_year_and_symbol_graph_dir, file.name))
  print(sprintf("Saved %s", file.name))


  aggreaged_data <- aggregate(data, by=list(data$window.ratio),FUN=mean, na.rm=TRUE)

  ggplot(data=aggreaged_data, aes(x=window.ratio, y=winning.percentage)) +
  geom_bar(colour="black", stat="identity", position = "dodge") +
  geom_hline(yintercept = 50) +
  geom_hline(yintercept = 60, colour="#990000", linetype="dashed") +
  geom_hline(yintercept = 40, colour="#990000", linetype="dashed") +
  geom_hline(yintercept = mean(aggreaged_data$winning.percentage, na.rm=TRUE), colour="royalblue1", linetype="dashed") +
  geom_hline(yintercept = weighted.mean(data$winning.percentage, data$trade.count, na.rm=TRUE), colour="darkorange3", linetype="dashed") +
  guides(fill=FALSE) +
  ggtitle(paste('window ratio vs winning percentage aggregated', type))
  file.name <- paste('windowRatioVsWinningPercentageAggregated', type, '.png', sep="")
  ggsave(file=file.path(output, winners_by_year_and_symbol_graph_dir, file.name))
  print(sprintf("Saved %s", file.name))

  data$cut_off_moving_average_count <- paste(data$cut_off, data$moving_average_count, sep=" ")

  ggplot(data=data, aes(x=cut_off_moving_average_count, y=winning.percentage)) +
  geom_bar(colour="black", stat="identity", position = "dodge") +
  geom_hline(yintercept = 50) +
  geom_hline(yintercept = 60, colour="#990000", linetype="dashed") +
  geom_hline(yintercept = 40, colour="#990000", linetype="dashed") +
  geom_hline(yintercept = mean(data$winning.percentage, na.rm=TRUE), colour="royalblue1", linetype="dashed") +
  geom_hline(yintercept = weighted.mean(data$winning.percentage, data$trade.count, na.rm=TRUE), colour="darkorange3", linetype="dashed") +
  guides(fill=FALSE) +
  theme(axis.text.x = element_text(angle = 90, hjust = 1)) +
  ggtitle(paste('cut off moving average count vs winning percentage', type))
  file.name <- paste('cutOffMovingAverageCountVsWinningPercentageAggregated', type, '.png', sep="")
  ggsave(file=file.path(output, winners_by_year_and_symbol_graph_dir, file.name))
  print(sprintf("Saved %s", file.name))

}

generate.all.plots <- function(file.name) {
  data.path <- file.path(input, winners_by_year_and_symbol_dir, 'summary', file.name)
  print(data.path)
  data <- read.table(data.path, header=T,sep=",")

  name.and.ext <- strsplit(file.name, "_")[[1]][2]
  name <- strsplit(name.and.ext, ".", fixed = TRUE)[[1]][1]

  generate.plots(data, paste('all', name, sep="_"))

  negative_cut_off <- data[data$cut_off < 0, ]
  generate.plots(negative_cut_off,paste('negative_cut_off', name, sep="_"))

  positive_cut_off <- data[data$cut_off > 0, ]
  generate.plots(positive_cut_off, paste('positive_cut_off', name, sep="_"))
}

all.summary.files <- list.files(file.path(input, winners_by_year_and_symbol_dir, 'summary'))

lapply(all.summary.files, function(x) generate.all.plots(x))

print('Finished summary ruby graphs')



