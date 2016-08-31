file.in <- 'eurusd_FadeTheBreakoutNormalDaily.csv'

data <- read.table(paste("results/",file.in, sep=""), header=T,sep=",")
data$date.time=as.POSIXct(data$date, tz = "UTC", format="%Y-%m-%dT%H:%M")
data$date <- NULL


trade_count <- nrow(data)

winners <- data[data$ticks >0,]
winners_count <- nrow(winners)


no_improvement_possible <- data[data$could_of_been_better == 0,]
lost_ticks <- sum(no_improvement_possible$ticks) #768

lost_count <- nrow(no_improvement_possible) #15

improvement_possible_percentage <- 100 - (lost_count / winners_count) * 100 #93.24%


five_ticks_lost <- data[data$could_of_been_better < 6,]
five_lost_ticks <- sum(five_ticks_lost$ticks) #2043

five_lost_count <- nrow(five_ticks_lost) #57

winners_with_5_tick_improvement <- data[data$could_of_been_better > 5,]

winners_with_5_tick_improvement_count <- nrow(winners_with_5_tick_improvement)



free_five_ticks_count <- nrow(data[data$could_of_been_better > 5,])
total_free_five_ticks <- free_five_ticks_count * 5

