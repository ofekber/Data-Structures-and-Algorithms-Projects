package main;

public class StockManager {
    private StockTree StockTree;
    private PriceTree PriceTree;

    public StockManager() {
        initStocks();
    }

    // 1. Initialize the system
    public void initStocks() {
        StockTree = new StockTree();
        PriceTree = new PriceTree();
    }

    // 2. Add a new stock
    public void addStock(String stockId, long timestamp, Float price) {
        if ((stockId != null && timestamp > 0 && price != null && price > 0) && this.StockTree.search(stockId) == null) {
            StockNode stockToInsert = new StockNode(stockId, timestamp, price);
            this.StockTree.insert(stockToInsert);
            PriceNode priceNodeToInsert = new PriceNode(stockToInsert);
            this.PriceTree.insert(priceNodeToInsert);
            stockToInsert.getStock().setEquivalent(priceNodeToInsert);
        } else {
            throw new IllegalArgumentException();
        }
    }

    // 3. Remove a stock
    public void removeStock(String stockId) {
        if ((stockId != null && this.StockTree.search(stockId) != null)) {
           StockNode stockToRemove = this.StockTree.search(stockId);
           this.StockTree.delete(stockToRemove);
           PriceNode priceNodeToRemove = stockToRemove.getStock().getEquivalent();
           this.PriceTree.delete(priceNodeToRemove);
        } else {
            throw new IllegalArgumentException();
        }
    }

    // 4. Update a stock price
    public void updateStock(String stockId, long timestamp, Float priceDifference) {
        if (stockId != null && priceDifference != null && priceDifference != 0 && this.StockTree.search(stockId) != null) {
            StockNode stockNodeToUpdate = this.StockTree.search(stockId);
            stockNodeToUpdate.getStock().updatePrice(timestamp, priceDifference);
            this.PriceTree.delete(stockNodeToUpdate.getStock().getEquivalent());
            PriceNode updatedPriceNode = new PriceNode(stockNodeToUpdate);
            stockNodeToUpdate.getStock().setEquivalent(updatedPriceNode);
            this.PriceTree.insert(updatedPriceNode);

            } else {throw new IllegalArgumentException();}
    }

    // 5. Get the current price of a stock
    public Float getStockPrice(String stockId) {
        if ((stockId != null) && this.StockTree.search(stockId) != null){
            return this.StockTree.search(stockId).getStock().getCurrentPrice();
        } else {
            throw new IllegalArgumentException();
        }
    }

    // 6. Remove a specific timestamp from a stock's history
    public void removeStockTimestamp(String stockId, long timestamp) {
        if (stockId != null && this.StockTree.search(stockId) != null) {
            StockNode stockToDeleteTimestamp = this.StockTree.search(stockId);
            stockToDeleteTimestamp.getStock().deleteTimestamp(timestamp);
            this.PriceTree.delete(stockToDeleteTimestamp.getStock().getEquivalent());
            PriceNode updatedPriceNode = new PriceNode(stockToDeleteTimestamp);
            stockToDeleteTimestamp.getStock().setEquivalent(updatedPriceNode);
            this.PriceTree.insert(updatedPriceNode);
        } else {throw new IllegalArgumentException();}

    }

    // 7. Get the amount of stocks in a given price range
    public int getAmountStocksInPriceRange(Float price1, Float price2) {
        if (price1 == null || price2 == null || price1 > price2) {throw new IllegalArgumentException();
        } else {
            return this.PriceTree.getCountInRange(this.PriceTree.getRoot(), price1, price2);
        }
    }

    // 8. Get a list of stock IDs within a given price range
    public String[] getStocksInPriceRange(Float price1, Float price2) {
        if (price1 == null || price2 == null || price1 > price2) {throw new IllegalArgumentException();
    } else {
            return this.PriceTree.stocksInRange(this.PriceTree.getRoot(), price1, price2);
        }
    }
}


