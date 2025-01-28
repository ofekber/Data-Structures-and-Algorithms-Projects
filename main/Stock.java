package main;

public class Stock {
    private final String stockId;
    private float currentPrice;
    private TimestampTree timestampTree;
    private final long initializationTimestamp;
    private PriceNode equivalent;

    //אתחול מניה
    public Stock(String stockId, long timestamp, float initialPrice) {
        this.stockId = stockId;
        this.timestampTree = new TimestampTree();
        this.initializationTimestamp = timestamp;
        this.currentPrice = initialPrice;
        this.timestampTree.insert(new TimestampNode(timestamp, initialPrice));
    }

    public void updatePrice(long timestamp, float price){ //לוודא שלא מעלה את הסיבוכיות לlog בריבוע
        TimestampNode timestampNodeToInsert = new TimestampNode(timestamp, price);
        this.timestampTree.insert(timestampNodeToInsert);
        this.currentPrice += price;
        this.currentPrice = roundPrice(this.currentPrice);
    }

    public long getInitializationTime() {return this.initializationTimestamp;}

    public void deleteTimestamp (long timestamp) {
        if (this.initializationTimestamp < timestamp && this.timestampTree.search(timestamp) != null) {
            TimestampNode timestampToDelete = this.timestampTree.search(timestamp);
            this.timestampTree.delete(timestampToDelete);
            this.currentPrice -= timestampToDelete.getPriceDifference();
            this.currentPrice = this.roundPrice(this.currentPrice);
        } else {throw new IllegalArgumentException();}
    }
    public float getCurrentPrice() {return this.currentPrice;}

    public String getStockId() {return this.stockId;}
    public void setEquivalent(PriceNode equivalent){
        this.equivalent = equivalent;
    }
    public PriceNode getEquivalent() {return this.equivalent;}

    private float roundPrice(float current){
        int temp = (int)(current * 100 + 0.5f);
        return temp / 100f;
    }

}
