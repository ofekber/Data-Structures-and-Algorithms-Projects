# Stock Management System

A Java application for managing stocks and their price history using custom **2-3 tree** data structures. The system supports adding/removing stocks, updating prices over time, and efficient range queries by price.

## Overview

The project implements a stock management system where each stock has a unique ID, an initial price at a given timestamp, and a history of price changes (deltas) at different timestamps. The **current price** of a stock is the initial price plus the sum of all applied price deltas. Two main indices are maintained: one by **stock ID** for lookups and updates, and one by **current price** for range queries.

## Project Structure

```
├── Main.java              # Demo and tests
├── README.md
└── src/
    ├── StockManager.java  # Main API (facade)
    ├── Stock.java         # Single stock with price history
    ├── StockNode.java     # Node for stock-ID tree
    ├── StockTree.java     # 2-3 tree keyed by stock ID
    ├── PriceNode.java     # Node for price tree
    ├── PriceTree.java     # 2-3 tree keyed by current price
    ├── TimestampNode.java # Node for timestamp history
    └── TimestampTree.java # 2-3 tree keyed by timestamp (per stock)
```

## Data Structures

### 2-3 Trees

All three trees are **2-3 trees** (nodes have 2 or 3 children) with sentinel nodes for consistent structure. They support:

- **Insert** and **split** when a node gets a fourth child
- **Delete** with **borrow or merge** to keep the tree balanced
- **Search** by key

### StockTree (by Stock ID)

- **Key:** Stock ID (string)
- **Purpose:** O(log n) lookup and update of a stock by ID
- **Nodes:** `StockNode` — each holds a `Stock` and uses the stock ID as the key

### PriceTree (by Current Price)

- **Key:** Current price (float), with stock ID as tiebreaker for equal prices
- **Purpose:** Count and list stocks in a given price range
- **Nodes:** `PriceNode` — each corresponds to one stock’s current price; linked to the stock via an “equivalent” reference so both trees stay in sync when a price changes
- **Extra:** Each internal node stores the minimum price in its subtree (`minSubtreeValue`) to speed up range queries

### TimestampTree (per Stock)

- **Key:** Timestamp (long)
- **Stored value:** Price difference (float) at that time
- **Purpose:** Per-stock history of price updates; current price = initial price + sum of all stored deltas
- **Nodes:** `TimestampNode` — key = timestamp, value = price delta

## StockManager API

| Method | Description |
|--------|-------------|
| `initStocks()` | Initialize (or reset) the system with empty stock and price trees. |
| `addStock(stockId, timestamp, price)` | Add a new stock with the given ID, initial timestamp, and initial price. Throws if the ID already exists or inputs are invalid. |
| `removeStock(stockId)` | Remove the stock. Throws if the stock does not exist. |
| `updateStock(stockId, timestamp, priceDifference)` | Apply a price delta at the given timestamp. Updates current price and the price tree. Throws if stock not found or inputs invalid. |
| `getStockPrice(stockId)` | Return the current price of the stock. |
| `removeStockTimestamp(stockId, timestamp)` | Remove one price-update (timestamp) from the stock’s history and recompute current price. Cannot remove the initial timestamp. |
| `getAmountStocksInPriceRange(price1, price2)` | Return the number of stocks whose current price is in `[price1, price2]` (inclusive). |
| `getStocksInPriceRange(price1, price2)` | Return an array of stock IDs whose current price is in `[price1, price2]`, ordered by price (and by ID for ties). |

## Design Notes

- **Dual indexing:** Each stock appears in both `StockTree` (by ID) and `PriceTree` (by price). When a price changes, the old `PriceNode` is removed and a new one is inserted so range queries always reflect current prices.
- **Price as sum of deltas:** A stock’s current price is computed from the initial price and all timestamped deltas in its `TimestampTree`. Removing a timestamp subtracts that delta from the current price.
- **Range queries:** The price tree uses `minSubtreeValue` and recursive range traversal to count and collect stock IDs in a price interval without scanning all stocks.

## Running the Project

1. Ensure Java is installed.
2. Compile and run from the project root. `Main.java` is in package `main`; sources under `src/` need to be on the classpath (or in the same package structure as your IDE/runner expects).

Example from the project root, if `Main.java` and `src/` are set up for default package or your IDE handles packages:

```bash
javac -d out src/*.java main/Main.java   # adjust paths/packages as needed
java -cp out main.Main
```

Or open the project in an IDE, set `src` as source folder, and run `Main`.

## Main.java Demo

`Main` demonstrates:

1. Adding 10 stocks with different IDs, timestamps, and initial prices.
2. Removing 3 stocks and checking that getting their price throws as expected.
3. Updating several stocks with additional (timestamp, price delta) pairs.
4. Removing some timestamps from stocks and verifying updated prices.
5. Querying stocks in price ranges and asserting counts and IDs (e.g. range [50, 170] returns 4 stocks in a specific order).

You can use `Main` as a reference for how to call `StockManager` and what behavior to expect.
