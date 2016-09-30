library('ggthemes')
library('ggplot2')

print('------------- GO ---------------------')

args <- commandArgs(trailingOnly = TRUE)
input <- args[1]
#results/results_bands/ruby/summary_high_scores-2-100-bands.csv
output <- args[2]

#results/results_bands/graphs
dir.out <- file.path(output,'years')
dir.create(dir.out, showWarnings = FALSE)
data <- read.table(input, header=T,sep=",")

print(sprintf("Row count from data %d", nrow(data)))
data$start_date.time=as.Date(as.POSIXct(data$start_date, tz = "UTC", format="%Y-%m-%dT%H:%M:%S"))
data$year <- format(data$start_date.time, '%Y')
data <- data[complete.cases(data), ]
data <- data[data$minimum_profit == 80,]

by_cut_off_min <- aggregate(cbind(winners.size, losers.size)~cut_off_percentage+year, data=data, sum, na.rm=TRUE)
by_cut_off_min$ave <- (by_cut_off_min$winners.size / (by_cut_off_min$winners.size + by_cut_off_min$losers.size)) * 100

ggplot(data=by_cut_off_min, aes(x=cut_off_percentage, y=ave)) +
  facet_grid(year ~ .) +
  geom_hline(aes(yintercept=30), colour="#990000", linetype="dashed") +
  geom_hline(aes(yintercept=60), colour="#990000", linetype="dashed") +
  geom_bar(stat="identity")


get.year <- function(date, split_character) {
    return ((strsplit(date, split_character)[[1]])[1])
}

append.year.filename <- function(filename, start_year, end_year) {
    complete.file.name <- (paste(dir.out, filename, get.year(start_year,"-"), "-", get.year(end_year,"-"), ".png", sep=""))

    print(complete.file.name)
    return (complete.file.name)
}

generate.plots.by.date <- function(start_date, end_date, data) {
  print(sprintf("Going to generate plots from %s to %s", start_date, end_date))

  # start_date <- '2015-08-02T00:00:00+00:00'
  # end_date <- '2017-08-02T00:00:00+00:00'

  start_year = get.year(start_date,"_")
  end_year = get.year(end_date,"_")

  plot_name <- append.year.filename("/summary_high_scores-2-100-bands_", start_year, end_year)
  print(sprintf("Going to plot to %s", plot_name))


  filtered_data <- data[data$start_date == start_date,]
  filtered_data <- filtered_data[filtered_data$end_date == end_date,]


  by_cut_off_min <- aggregate(cbind(winners.size, losers.size)~cut_off_percentage+minimum_profit, data=filtered_data, sum, na.rm=TRUE)
  by_cut_off_min$ave <- (by_cut_off_min$winners.size / (by_cut_off_min$winners.size + by_cut_off_min$losers.size)) * 100

  ggplot(data=by_cut_off_min, aes(x=cut_off_percentage, y=ave)) +
    facet_grid(minimum_profit ~ .) +
    geom_hline(aes(yintercept=30), colour="#990000", linetype="dashed") +
    geom_hline(aes(yintercept=60), colour="#990000", linetype="dashed") +
    geom_bar(stat="identity")
  ggsave(file=plot_name, width = 21, height = 15)


}

unique_start_and_end_dates <-  unique(data[c("start_date", "end_date")])

apply(unique_start_and_end_dates, 1, function(x) generate.plots.by.date(x[1], x[2], data))
