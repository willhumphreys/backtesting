


plot.profit.per.trade <- function(file.in) {

  profit.per.trade <- function(cut.off) {

    #Get all trades above the cut off
    five_better <- data[data$could_of_been_better > cut.off, ]
    #Count trade above the cut off
    five_better_count <- nrow(five_better)
    #Sum the ticks above the cut off
    five_better_ticks <- sum(five_better$ticks)
    #Calculate bonus ticks for waiting for the cut off
    five_better_ticks_bonus <- five_better_count * cut.off
    #Add bonus ticks to total ticks
    five_better_total_ticks <- five_better_ticks + five_better_ticks_bonus
    #Get the ticks per trade.
    five_better_profit_per_trade <- five_better_total_ticks / five_better_count

    return(c(five_better_total_ticks,five_better_count,five_better_profit_per_trade))
  }

  cat(file.in)

  data <- read.table(paste("results/",file.in, sep=""), header=T,sep=",")
  data$date.time=as.POSIXct(data$date, tz = "UTC", format="%Y-%m-%dT%H:%M")
  data$date <- NULL

  cuts.offs <- 1:200

  profits.per.trade.list <- lapply(cuts.offs, function(x) profit.per.trade(x))

  profits.per.trade <- data.frame(matrix(unlist(profits.per.trade.list), nrow=200, byrow=T))

  colnames(profits.per.trade) <- c("ticks", "count", "ticks_per_trade")

  # Graphs the cut off against the ticks per trade.
  # A positive value means fading with the cut off works.
  ggplot(data=profits.per.trade, aes(x=as.numeric(rownames(profits.per.trade)), y=ticks_per_trade)) +
  theme(axis.text.x = element_text(angle = 90, hjust = 1)) +
  scale_x_continuous(breaks=c(1,25,50,75,100,125,150,175,200)) +
  geom_bar(stat="identity")
  ggsave(file= paste('graphs/could_of_been_better/',file.in, '.png', sep=""))

  # Graphs the cut off against ticks.
  # A positive value maeans fading makes a profit.
  ggplot(data=profits.per.trade, aes(x=as.numeric(rownames(profits.per.trade)), y=ticks)) +
  theme(axis.text.x = element_text(angle = 90, hjust = 1)) +
  scale_x_continuous(breaks=c(1,25,50,75,100,125,150,175,200)) +
  geom_bar(stat="identity")
  ggsave(file= paste('graphs/could_of_been_better/',file.in, '-ticks.png', sep=""))

}

in_files <- list.files('results')

sapply(in_files, function(x) plot.profit.per.trade(x))
