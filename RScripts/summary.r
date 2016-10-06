library(R.utils)

print('Start summary.r')

args <- commandArgs(trailingOnly = TRUE)

options(width=150)

input = args[1];
output = args[2]

data <- read.table(file.path(input, "results_improved.csv"), header=T,sep=",")

(sprintf("Input is %s\n", input))
cat(sprintf("Output is %s\n", output))
cat(sprintf("Current working directory %s\n",getwd()))

winners <- sum(data$winner_ratio > 1)
losers <- sum(data$winner_ratio < 1)
flat <- sum(data$winner_ratio == 1)

highest_winners_row <- data[which.max(data$winning_percentage),]
lowest_winners_row <- data[which.min(data$winning_percentage),]

fileConn<-file(output, "wt")
writeLines(c(sprintf("Winners: %d\nLosers: %d\n", winners, losers),
    sprintf("Highest winning percentage: %s:%f", highest_winners_row$symbol, highest_winners_row$winning_percentage),
    sprintf("Lowest winning percentage: %s:%f", lowest_winners_row$symbol, lowest_winners_row$winning_percentage)),
    con=fileConn,sep="\n")

symbol <- 'AUDUSD'
symbol_data <- read.table(file.path(input, paste("data/", symbol, ".csv", sep="")), header=T,sep=",")
trade_count <- nrow(symbol_data)
cat("\nSymbol information\n", file=fileConn, append=TRUE)
cat(sprintf("%s trade count %d\n", symbol, trade_count), file=fileConn, append=TRUE)

close(fileConn)
print('Finished summary.r')


