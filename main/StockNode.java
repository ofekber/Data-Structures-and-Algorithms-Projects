package main;

public class StockNode {
    private String key;
    private Stock stock;
    private int size; // מספר העלים חוץ מהסנטינלס שמושרש בו
    private boolean sentinel;
    private StockNode parent;
    private StockNode left;// בן שמאלי
    private StockNode middle;
    private StockNode right;

    public StockNode() {//בשביל הסנטינלס
        this.sentinel = false;
        this.size = 0;
    }

    public StockNode(String stockId, long timestamp, float initialPrice) {
        this.key = stockId;
        this.sentinel = false;
        this.size = 1;
        this.stock = new Stock(stockId, timestamp, initialPrice);
    }

    public boolean isLeaf() {
        return this.left == null; // כי לעלים אין ילד שמאלי (ובפרט גם אמצעי וימני)
    }

    public StockNode getLeft() {return this.left;}
    public StockNode getMiddle() {return this.middle;}
    public StockNode getRight() {return this.right;}
    public StockNode getParent() {return this.parent;}

    public void setLeft(StockNode node) {this.left = node;}
    public void setMiddle(StockNode node) {this.middle = node;}
    public void setRight(StockNode node) {this.right = node;}
    public void setParent(StockNode parent) {this.parent = parent;}

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isSentinel() {
        return sentinel;
    }

    public void setSentinel(boolean sentinel) {
        this.sentinel = sentinel;
    }

    public int getSize() {return this.size;}

    public Stock getStock() {return this.stock;}

    public boolean isSmaller(String otherKey){
        return this.key.compareTo(otherKey) < 0;
    }


    public void updateKey() {
        this.key = this.getLeft().getKey();
        if (this.getMiddle() != null) {
            this.key = this.getMiddle().getKey();
        }
        if (this.getRight() != null) {
            this.key = this.getRight().getKey();
        }
    }

    public void updateSentinelKey() {
        if (this.getLeft() != null && this.getMiddle() != null && this.getLeft().isSentinel()) {
            this.getLeft().setKey(this.getMiddle().getKey());
        }
        if (this.getMiddle() != null && this.getMiddle().isSentinel()) {
            this.getMiddle().setKey(this.getLeft().getKey());
        }
        if (this.getRight() != null && this.getRight().isSentinel()) {
            this.getRight().setKey(this.getMiddle().getKey());
        }
    }

    public void updateSize() {
        this.size = this.getLeft().getSize();
        if (this.getMiddle() != null) {
            this.size += this.getMiddle().getSize();
        }
        if (this.getRight() != null) {
            this.size += this.getRight().getSize();
        }
    }

}