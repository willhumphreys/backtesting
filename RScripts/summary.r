library(R.utils)

args <- commandArgs(trailingOnly = TRUE)

options(width=150)

input = args[1];
output = args[2]

data <- read.table(input, header=T,sep=",")

cat(sprintf("Input is %s\n", input))
cat(sprintf("Output is %s\n", output))
cat(getwd())

winners <- sum(data$winner_ratio > 1)
losers <- sum(data$winner_ratio < 1)
flat <- sum(data$winner_ratio == 1)

highest_winners_row <- data[which.max(data$winning_percentage),]
lowest_winners_row <- data[which.min(data$winning_percentage),]

fileConn<-file(output)
writeLines(c(sprintf("Winners: %d\nLosers: %d\n", winners, losers),
    sprintf("Highest winning percentage: %s:%f", highest_winners_row$symbol, highest_winners_row$winning_percentage),
    sprintf("Lowest winning percentage: %s:%f", lowest_winners_row$symbol, lowest_winners_row$winning_percentage)),
    con=fileConn)
close(fileConn)


