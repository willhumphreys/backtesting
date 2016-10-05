library('ggplot2')
library('TTR')
library('scales')

last <- function(x) { tail(x, n = 1) }

generate.plot <- function(file.in, input, output) {
    print(file.in)
    print(output)
    dir.create(output)
    symbol <- strsplit(file.in, split='.', fixed=TRUE)[[1]][1]

    scenario <- 'onlyOne'
    graph.output.dir <- file.path(output, "graphs", symbol)
    dir.create(graph.output.dir, recursive = TRUE)

    file.name <- (strsplit(file.in, split='.', fixed=TRUE)[[1]])[1]
    csv.out.dir <- file.path(output, "r-csv")
    dir.create(csv.out.dir)
    csv.file <- paste(file.name, ".csv", sep="")
    csv.out <- file.path(csv.out.dir, csv.file)

    graph.file <- paste(file.name, ".png", sep="")
    file.out <- file.path(graph.output.dir, graph.file)

    winLose.file <- paste(file.name, "winLose.png", sep="")
    file.out.winLose <- file.path(graph.output.dir, winLose.file)

    data <- read.table(paste(input, '/', file.in, sep=""), header=T,sep=",")
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
    write(line,file=file.path(output,"summary.csv"),append=TRUE)

    cat(file.out.winLose)
    ggplot(data=data, aes(x=date.time, y=cum.winLose)) +
    geom_line() +
    scale_x_date(date_breaks = "3 month") +
    theme(axis.text.x = element_text(angle = 90, hjust = 1)) +
    scale_y_continuous(limits=c(-50, 200)) +
    stat_smooth() +
    ggtitle(file.out.winLose)
    ggsave(file=file.out.winLose)
    print('finished')
}

args <- commandArgs(trailingOnly = TRUE)
#Set the input file
#input = "/home/will/Code/macchiato/matcha/live-data/549d5843521836129546bdd8_BuyAtTime/flatSimulation.csv"
input <- args[1]

output <- args[2]
#output = "/home/will/Code/macchiato/matcha/live-data/549d5843521836129546bdd8_BuyAtTime/flatSimulationMerged.csv"

cat("Executing cumulative_profit.r\n")
cat(sprintf("Read input %s\n", input ))
cat(sprintf("Output %s\n", output))

write("file.in,symbol,scenario,cumulative_profit,win_lose_count,trade_count,win_lose_ratio,ticks_per_trade", file=file.path(output, "summary.csv"), append=FALSE)




#'results/acceptance_results/data'

in_files <- list.files(input)




#in_files <- in_files[!grepl("NewDayLow", in_files)]
sapply(in_files, function(x) generate.plot(x, input, output))
cat("Finished executing cumulative_profit.r\n")
