library(R.utils)

print('Start summary ruby')

args <- commandArgs(trailingOnly = TRUE)

options(width=150)

input = args[1];
output = args[2]

winners_by_year_and_symbol_dir <- '../graphs/winners_by_year_and_symbol/summary'

data <- read.table(file.path(input, winners_by_year_and_symbol_dir, "summary_all.csv"), header=T,sep=",")

(sprintf("Input is %s\n", input))
cat(sprintf("Output is %s\n", output))
cat(sprintf("Current working directory %s\n",getwd()))

average.winning.percentage <- mean(data$winning.percentage)
observations <- nrow(data)

summary.output <- file.path(output, '../graphs/winners_by_year_and_symbol/summary_ruby.txt')

fileConn<- file(summary.output, "wt")
writeLines(c(sprintf("Average winning percentage %.02f%%\n", average.winning.percentage),
sprintf("Observations %d", observations)),
con=fileConn,sep="\n")

#cat("\nSymbol information\n", file=fileConn, append=TRUE)
close(fileConn)


print('Finished summary ruby')



