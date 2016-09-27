library('ggplot2')
library('TTR')
library('scales')

last <- function(x) { tail(x, n = 1) }

generate.plot <- function(file.in) {
    print(file.in)
    symbol <- strsplit(file.in, split='.', fixed=TRUE)[[1]][1]

    scenario <- 'onlyOne'
    dir.create(file.path("graphs", symbol))
    file.name <- (strsplit(file.in, split='.', fixed=TRUE)[[1]])[1]
    graph.output.dir = paste("graphs/", symbol, sep = "")
    csv.out <- paste("r-csv/", file.name, ".csv", sep = "")
    file.out <- paste(graph.output.dir, "/", file.name, ".png", sep = "")
    file.out.winLose <- paste(graph.output.dir, "/",file.name, "winLose.png", sep = "")
    file.out.sma30 <- paste(graph.output.dir, "/", file.name, "sma30.png", sep = "")
    file.out.sma30ticks <- paste(graph.output.dir, "/", file.name, "sma30Ticks.png", sep = "")

    data <- read.table(paste("results/acceptance_results/data/",file.in, sep=""), header=T,sep=",")
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

    write.table(data, file=csv.out, sep=",", row.names=FALSE)

    data$date.time=as.Date(data$date.time)

    line <- paste(file.in, ",", symbol, ",", scenario, ",", last(data$cumulative_profit), ",", last(data$cum.winLose), ",", nrow(data), ",", last(data$cum.win) / last(data$cum.lose), ",", sum(data$ticks) / nrow(data))
    write(line,file="summary.csv",append=TRUE)

    cat(file.out.winLose)
    ggplot(data=data, aes(x=date.time, y=cum.winLose)) +
    geom_line() +
    scale_x_date(date_breaks = "3 month") +
    theme(axis.text.x = element_text(angle = 90, hjust = 1)) +
    scale_y_continuous(limits=c(0, 200)) +
    stat_smooth() +
    ggtitle(file.out.winLose)
    ggsave(file=file.out.winLose)
    print('finished')
}

write("file.in,symbol,scenario,cumulative_profit,win_lose_count,trade_count,win_lose_ratio,ticks_per_trade", file="summary.csv", append=FALSE)

in_files <- list.files('results/acceptance_results/data')


#in_files <- in_files[!grepl("NewDayLow", in_files)]
sapply(in_files, function(x) generate.plot(x))
