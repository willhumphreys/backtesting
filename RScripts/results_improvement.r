library(R.utils)

args <- commandArgs(trailingOnly = TRUE)

options(width=150)


input = args[1];
output = args[2]

data <- read.table(input, header=T,sep=",")

cat(nrow(data))

write.table(data, file=output, sep=",", row.names=FALSE)