library(R.utils)
library(plyr)
require(reshape2)

args <- commandArgs(trailingOnly = TRUE)

options(width=200)


input = args[1];
output = args[2]

cat("Executing results_improvement.r\n")

data <- read.table(file.path(input, 'results.csv'), header=T,sep=",")

data$winner_ratio <- round(data$winners / data$losers, digits = 4)

data$winning_percentage <- round((data$winners / (data$winners + data$losers) * 100), digits = 2)
data$losing_percentage <- round((data$losers / (data$winners + data$losers) * 100), digits = 2)

cat(nrow(data))

write.table(data, file=output, sep=",", row.names=FALSE)

cat("Finished executing cumulative_profit.r\n")

