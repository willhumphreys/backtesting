library(R.utils)

args <- commandArgs(trailingOnly = TRUE)

options(width=150)

input = args[1];
output = args[2]

data <- read.table(input, header=T,sep=",")

cat(sprintf("Output is %s\n", output))
cat(getwd())

winners <- sum(data$winner_ratio > 1)
losers <- sum(data$winner_ratio < 1)
flat <- sum(data$winner_ratio == 1)

fileConn<-file(output)
writeLines(sprintf("Winners: %d\nLosers: %d\n", winners, losers), fileConn)
close(fileConn)