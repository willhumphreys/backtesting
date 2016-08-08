library(ggplot2)


last <- function(x) { tail(x, n = 1) }
generate.plot <- function(file.in) {
    file.out <- paste("graphs/",(strsplit(file.in, split='.', fixed=TRUE)[[1]])[1], ".png", sep = "")
    csv.out <- paste("r-csv/",(strsplit(file.in, split='.', fixed=TRUE)[[1]])[1], ".csv", sep = "")
    file.out.winLose <- paste("graphs/",(strsplit(file.in, split='.', fixed=TRUE)[[1]])[1], "winLose.png", sep = "")

    data <- read.table(paste("results/",file.in, sep=""), header=T,sep=",")
    data$date.time=as.POSIXct(data$date, tz = "UTC", format="%Y-%m-%dT%H:%M:%S")
    data$date <- NULL

    data$cumulative_profit <- cumsum(data$ticks)
    data$winLose <- ifelse(data$ticks > 0, 1,-1)
    data$cum.winLose <- cumsum(data$winLose)

    write.table(data, file=csv.out, sep=",")


    data$date.time=as.Date(data$date.time)

    line <- paste(file.in, ",", last(data$cumulative_profit), ",", last(data$cum.winLose), ",", nrow(data))
    write(line,file="summary.csv",append=TRUE)

    ggplot(data=data, aes(x=date.time, y=cumulative_profit, group = 1)) +
    geom_line() +
    scale_x_date(date_breaks = "3 month") +
    theme(axis.text.x = element_text(angle = 90, hjust = 1)) +
    stat_smooth() +
    ggtitle(file.in)
    ggsave(file=file.out)

    ggplot(data=data, aes(x=date.time, y=cum.winLose)) +
    geom_line() +
    scale_x_date(date_breaks = "3 month") +
    theme(axis.text.x = element_text(angle = 90, hjust = 1)) +
    stat_smooth() +
    ggtitle(file.in)
    ggsave(file=file.out.winLose)
}

write("symbol,cumulative_profit,win_lose_count,trade_count", file="summary.csv",append=TRUE)

in_files <- list.files('results')
sapply(in_files, function(x) generate.plot(x))
