library(scales)

args <- commandArgs(trailingOnly = TRUE)

options(width=150)

library(R.utils)

input = args[1];
output = args[2]

cat(sprintf("Output is %s\n", output))
cat(getwd())

fileConn<-file(output)
writeLines(c("Hello","World"), fileConn)
close(fileConn)