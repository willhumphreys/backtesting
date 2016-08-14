library(ggplot2)
library(TTR)
library("scales")

last <- function(x) { tail(x, n = 1) }
generate.plot <- function(file.in) {
    cat(file.in)
    symbol <- strsplit(file.in, split='_', fixed=TRUE)[[1]][1]
    scenario.with.extension <- strsplit(file.in, split='_', fixed=TRUE)[[1]][4]
    scenario <- (strsplit(scenario.with.extension, split='.', fixed=TRUE)[[1]])[1]
    dir.create(file.path("graphs", symbol))
    file.name <- (strsplit(file.in, split='.', fixed=TRUE)[[1]])[1]
    graph.output.dir = paste("graphs/", symbol, sep = "")
    csv.out <- paste("r-csv/", file.name, ".csv", sep = "")
    file.out <- paste(graph.output.dir, "/", file.name, ".png", sep = "")
    file.out.winLose <- paste(graph.output.dir, "/",file.name, "winLose.png", sep = "")
    file.out.sma30 <- paste(graph.output.dir, "/", file.name, "sma30.png", sep = "")
    file.out.sma30ticks <- paste(graph.output.dir, "/", file.name, "sma30Ticks.png", sep = "")

    data <- read.table(paste("results/",file.in, sep=""), header=T,sep=",")
    data$date.time=as.POSIXct(data$date, tz = "UTC", format="%Y-%m-%dT%H:%M")
    data$date <- NULL

    data$cumulative_profit <- cumsum(data$ticks)
    data$winLose <- ifelse(data$ticks > 0, 1, -1)
    data$win <- ifelse(data$ticks > 0, 1,0)
    data$cum.win <- cumsum(data$win)

    data$lose <- ifelse(data$ticks < 0, 1, 0)
    data$cum.lose <- cumsum(data$lose)

    data$cum.winLose <- cumsum(data$winLose)
    data$scaled.cum.winLose <- rescale(data$cum.winLose, to=c(-1,1))
    data$cum.winP <- (data$cum.win /  as.numeric(rownames(data))) * 100
    data$cum.loseP <- (data$cum.lose /  as.numeric(rownames(data))) * 100

    data <- data[,c(7, 1, 2, 3, 4, 5, 6, 8, 9, 10, 11, 12, 13, 14, 15, 16)]


#    data$SMA30 <- SMA(data$winLose, 30)
#    data$SMA30Ticks <- SMA(data$ticks, 30)

#    data$scaled_cumulative_profit <- rescale(data$cumulative_profit, to=c(-1,1))
#    data$scaled_SMA30 <- rescale(data$SMA30, to=c(-1,1))

    write.table(data, file=csv.out, sep=",", row.names=FALSE)

    data$date.time=as.Date(data$date.time)

    line <- paste(file.in, ",", symbol, ",", scenario, ",", last(data$cumulative_profit), ",", last(data$cum.winLose), ",", nrow(data), ",", last(data$cum.win) / last(data$cum.lose))
    write(line,file="summary.csv",append=TRUE)


#    cat(file.out)
#    ggplot(data=data, aes(x=date.time, y=scaled.cum.winLose, group = 1)) +
#    geom_line() +
#    geom_line(data=data, aes(colour=SMA30, x=date.time, y=data$SMA30)) +
#    scale_x_date(date_breaks = "3 month") +
#    theme(axis.text.x = element_text(angle = 90, hjust = 1)) +
#  #  stat_smooth() +
#    ggtitle(file.out)
#    ggsave(file=file.out)

    cat(file.out.winLose)
    ggplot(data=data, aes(x=date.time, y=cum.winLose)) +
    geom_line() +
    scale_x_date(date_breaks = "3 month") +
    theme(axis.text.x = element_text(angle = 90, hjust = 1)) +
    stat_smooth() +
    ggtitle(file.out.winLose)
    ggsave(file=file.out.winLose)

#    cat(file.out.sma30)
#    ggplot(data=data, aes(x=date.time, y=SMA30)) +
#    geom_line() +
#    scale_x_date(date_breaks = "3 month") +
#    theme(axis.text.x = element_text(angle = 90, hjust = 1)) +
#    ggtitle(file.out.sma30)
#    ggsave(file=file.out.sma30)
#
#    cat(file.out.sma30ticks)
#    ggplot(data=data, aes(x=date.time, y=SMA30Ticks)) +
#    geom_line() +
#    scale_x_date(date_breaks = "3 month") +
#    theme(axis.text.x = element_text(angle = 90, hjust = 1)) +
#    ggtitle(file.out.sma30ticks)
#    ggsave(file=file.out.sma30ticks)

    cat('finished')
}

write("file.in,symbol,scenario,cumulative_profit,win_lose_count,trade_count,win_lose_ratio", file="summary.csv", append=FALSE)

in_files <- list.files('results')


in_files <- in_files[!grepl("NewDayLow", in_files)]
sapply(in_files, function(x) generate.plot(x))
