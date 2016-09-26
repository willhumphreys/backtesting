file.in <- 'results_bands/results.csv'

data <- read.table(file.in), header=T,sep=",")
data$date.time=as.POSIXct(data$date, tz = "UTC", format="%Y-%m-%dT%H:%M")
data$date <- NULL