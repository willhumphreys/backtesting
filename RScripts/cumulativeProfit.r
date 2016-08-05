library(ggplot2)

data <- read.table("results/eurusd_60min_10y_03082016Out.csv", header=T,sep=",")

data$date.time=as.POSIXct(data$date, tz = "UTC", format="%Y-%m-%dT%H:%M:%S")
data$date <- NULL

ggplot(data=data, aes(x=date.time, y=cumulative_profit, group = 1)) + geom_line()

