/**
 * A strict end-cases test for the StockManager code
 *
 * This class attempts various boundary and error conditions:
 *   - Using price1 > price2 in range queries
 *   - Removing non-existent stocks
 *   - Adding invalid stocks (duplicate IDs, negative price)
 *   - Updating with zero difference
 *   - Removing timestamps that don't exist
 *   - Checking the system in empty states
 *   - Checking correct results for valid usage
 */
package main;
public class test {

    public static void main(String[] args) {

        StockManager manager = new StockManager();

        // 1. initStocks() on an already-new StockManager (should be empty; no error).
        manager.initStocks();
        println("Test 1: initStocks on new manager => OK (no crash).");

        // 2. Attempt to remove a non-existent stock => expect exception
        testRemoveNonExistent(manager, "FAKE_ID");

        // 3. Try to get price of a non-existent stock => expect exception
        testGetPriceNonExistent(manager, "FAKE_ID");

        // 4. Add a stock with invalid (non-positive) price => expect exception
        testAddStockInvalidPrice(manager, "AAA", 1000000L, 0f);
        testAddStockInvalidPrice(manager, "BBB", 1000001L, -5f);

        // 5. Add a valid stock
        manager.addStock("STK1", 1000000L, 50f);
        println("Test 5: added STK1 with price=50 => OK");

        // 6. Add same stock again => expect duplicate exception
        testAddDuplicateStock(manager, "STK1", 1000002L, 60f);

        // 7. Check getStockPrice => should be 50
        float priceStk1 = manager.getStockPrice("STK1");
        myAssert(priceStk1 == 50f, "STK1 price must be 50, got: " + priceStk1);
        println("Test 7: getStockPrice(STK1) => 50 => OK");

        // 8. Update stock with priceDifference=0 => expect exception
        testUpdateWithZeroDiff(manager, "STK1", 1000003L);

        // 9. Valid update => e.g. +20
        manager.updateStock("STK1", 1000004L, 20f);
        myAssert(manager.getStockPrice("STK1") == 70f, "Expected STK1 price=70");
        println("Test 9: updated STK1 with +20 => price=70 => OK");

        // 10. Another update => e.g. -10
        manager.updateStock("STK1", 1000005L, -10f);
        myAssert(manager.getStockPrice("STK1") == 60f, "Expected STK1 price=60");
        println("Test 10: updated STK1 with -10 => price=60 => OK");

        // 11. Remove invalid timestamp => expect exception
        testRemoveTimestamp(manager, "STK1", 9999999L, false);

        // 12. Remove a real timestamp => e.g. remove the +20 (1000004L)
        testRemoveTimestamp(manager, "STK1", 1000004L, true);
        myAssert(manager.getStockPrice("STK1") == 40f, "After removing +20, price should be 40 (50 initial -10 update).");
        println("Test 12: removed update with timestamp=1000004 => price=40 => OK");

        // 13. Let's add more stocks
        manager.addStock("STK2", 1000000L, 10f);
        manager.addStock("STK3", 1000000L, 100f);
        manager.addStock("STK4", 1000000L, 40f);

        // Now STK2=10, STK3=100, STK4=40, STK1=40
        // 14. Range query => check price1>price2 => expect exception
        testRangeException(manager, 100f, 50f);

        // 15. Normal range query => e.g. [0..50]
        // STK2=10, STK1=40, STK4=40 => in range => 3 stocks; STK3=100 => out
        int count = manager.getAmountStocksInPriceRange(0f, 50f);
        myAssert(count == 3, "Expected 3 in [0..50], got: " + count);
        println("Test 15: getAmountStocksInPriceRange(0..50) => 3 => OK");

        // 16. getStocksInPriceRange => see if we get STK1, STK2, STK4
        // They should be sorted by price ascending. If there's a tie, sort by stockId ascending.
        // STK2=10, STK1=40, STK4=40 => STK1 < STK4 in alphabetical order?
        // Actually, "STK1".compareTo("STK4") => negative => STK1 < STK4
        // So expected order => STK2, STK1, STK4
        String[] arr = manager.getStocksInPriceRange(0f, 50f);
        myAssert(arr.length == 3, "Expected array length=3, got:" + arr.length);
        myAssert(arr[0].equals("STK2"), "arr[0]=STK2?");
        myAssert(arr[1].equals("STK1"), "arr[1]=STK1?");
        myAssert(arr[2].equals("STK4"), "arr[2]=STK4?");
        println("Test 16: getStocksInPriceRange(0..50) => [STK2, STK1, STK4] => OK");

        // 17. Remove stock => check it disappears from range queries
        manager.removeStock("STK2");
        myAssertThrown(() -> manager.getStockPrice("STK2"), "Expected exception for getStockPrice(STK2) after removal");
        // Now range [0..50] => STK2 gone => STK1(40), STK4(40)
        count = manager.getAmountStocksInPriceRange(0f, 50f);
        myAssert(count == 2, "Expected 2 in range after removing STK2");
        println("Test 17: removeStock(STK2) => OK, not in range => OK");

        println("=== All end-case tests PASSED successfully! ===");
    }

    // --------------------- Helpers ---------------------

    private static void testRemoveNonExistent(StockManager manager, String stockId) {
        try {
            manager.removeStock(stockId);
            fail("Expected exception removing non-existent stock: " + stockId);
        } catch (IllegalArgumentException e) {
            println("Test 2: removeStock(non-existent) => threw exception => OK");
        }
    }

    private static void testGetPriceNonExistent(StockManager manager, String stockId) {
        try {
            manager.getStockPrice(stockId);
            fail("Expected exception getStockPrice(non-existent): " + stockId);
        } catch (IllegalArgumentException e) {
            println("Test 3: getStockPrice(non-existent) => threw exception => OK");
        }
    }

    private static void testAddStockInvalidPrice(StockManager manager, String stockId, long ts, float price) {
        try {
            manager.addStock(stockId, ts, price);
            fail("Expected exception adding stock with invalid price: " + price);
        } catch (IllegalArgumentException e) {
            println("Test 4: addStock('" + stockId + "', " + price + ") => threw exception => OK");
        }
    }

    private static void testAddDuplicateStock(StockManager manager, String stockId, long ts, float price) {
        try {
            manager.addStock(stockId, ts, price);
            fail("Expected exception adding duplicate stock: " + stockId);
        } catch (IllegalArgumentException e) {
            println("Test 6: addStock(duplicate '" + stockId + "') => threw exception => OK");
        }
    }

    private static void testUpdateWithZeroDiff(StockManager manager, String stockId, long ts) {
        try {
            manager.updateStock(stockId, ts, 0f);
            fail("Expected exception updating with difference=0");
        } catch (IllegalArgumentException e) {
            println("Test 8: updateStock(...) with 0 => threw exception => OK");
        }
    }

    private static void testRemoveTimestamp(StockManager manager, String stockId, long ts, boolean expectSuccess) {
        try {
            manager.removeStockTimestamp(stockId, ts);
            if (!expectSuccess) {
                fail("Expected exception removing non-existent timestamp: " + ts);
            } else {
                println("Test 12 detail: removeStockTimestamp(" + stockId + ", " + ts + ") => success => OK");
            }
        } catch (IllegalArgumentException e) {
            if (expectSuccess) {
                fail("Unexpected exception removing valid timestamp: " + ts);
            } else {
                println("Test 11: removeStockTimestamp(non-existent " + ts + ") => threw exception => OK");
            }
        }
    }

    private static void testRangeException(StockManager manager, float p1, float p2) {
        try {
            manager.getAmountStocksInPriceRange(p1, p2);
            fail("Expected exception for range with p1>p2: " + p1 + " > " + p2);
        } catch (IllegalArgumentException e) {
            println("Test 14: getAmountStocksInPriceRange(" + p1 + ", " + p2 + ") => threw => OK");
        }
    }

    // A helper for asserting a condition
    private static void myAssert(boolean condition, String messageIfFail) {
        if (!condition) {
            throw new AssertionError("Assertion failed: " + messageIfFail);
        }
    }

    // Helper for expecting an exception
    private static void myAssertThrown(Runnable action, String messageIfNoException) {
        try {
            action.run();
            fail(messageIfNoException);
        } catch (IllegalArgumentException e) {
            // OK
        }
    }

    private static void fail(String msg) {
        throw new AssertionError("FAIL: " + msg);
    }

    private static void println(String msg) {
        System.out.println(msg);
    }
}
