library(scales)
library(R.utils)

args <- commandArgs(trailingOnly = TRUE)

options(width=150)


input = args[1];
output = args[2]

data <- read.table(input, header=T,sep=",")

cat(sprintf("Output is %s\n", output))
cat(getwd())

fileConn<-file(output)
writeLines(c("Hello","World", nrow(data)), fileConn)
close(fileConn)