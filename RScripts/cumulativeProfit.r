library(ggplot2)

generate.plot <- function(file.in) {
    file.out <- paste("graphs/",(strsplit(file.in, split='.', fixed=TRUE)[[1]])[1], ".png", sep = "")

    data <- read.table(paste("results/",file.in, sep=""), header=T,sep=",")
    data$date.time=as.POSIXct(data$date, tz = "UTC", format="%Y-%m-%dT%H:%M:%S")
    data$date <- NULL

    ggplot(data=data, aes(x=date.time, y=cumulative_profit, group = 1)) + geom_line()
    ggsave(file=file.out)
}
in_files <- list.files('results')

sapply(in_files, function(x) generate.plot(x))
