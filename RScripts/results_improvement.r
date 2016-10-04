library(R.utils)

args <- commandArgs(trailingOnly = TRUE)

options(width=150)


input = args[1];
output = args[2]

data <- read.table(input, header=T,sep=",")

data$winner_ratio <- round(data$winners / data$losers, digits = 4)

data$winning_percentage <- round((data$winners / (data$winners + data$losers) * 100), digits = 2)
data$losing_percentage <- round((data$losers / (data$winners + data$losers) * 100), digits = 2)

cat(nrow(data))

write.table(data, file=output, sep=",", row.names=FALSE)