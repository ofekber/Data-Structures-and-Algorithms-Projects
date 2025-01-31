package main;

public class PriceNode {
    private float priceKey;
    private String idKey;
    private float minSubtreeValue;
    private int size;
    private boolean sentinel;
    private PriceNode parent;
    private PriceNode left;
    private PriceNode middle;
    private PriceNode right;

    public PriceNode() {
        this.sentinel = false;
        this.size = 0;
    }

    public PriceNode(StockNode stockNode) {
        this.priceKey = stockNode.getStock().getCurrentPrice();
        this.idKey = stockNode.getStock().getStockId();
        this.sentinel = false;
        this.size = 1;
        this.minSubtreeValue = this.priceKey;
    }


    public void updateMinSubtreeValue() {
        if (this.isLeaf()) {

            this.minSubtreeValue = this.priceKey;
        } else {
            float minValue = Float.MAX_VALUE;

            if (this.left != null && !this.left.isSentinel()) {
                this.left.updateMinSubtreeValue();
                minValue = Math.min(minValue, this.left.minSubtreeValue);
            }
            if (this.middle != null && !this.middle.isSentinel()) {
                this.middle.updateMinSubtreeValue();
                minValue = Math.min(minValue, this.middle.minSubtreeValue);
            }
            if (this.right != null && !this.right.isSentinel()) {
                this.right.updateMinSubtreeValue();
                minValue = Math.min(minValue, this.right.minSubtreeValue);
            }

            this.minSubtreeValue = minValue;
        }
    }


    public boolean isLeaf() {
        return this.left == null;
    }

    public PriceNode getLeft() {return this.left;}
    public PriceNode getMiddle() {return this.middle;}
    public PriceNode getRight() {return this.right;}
    public PriceNode getParent() {return this.parent;}

    public void setLeft(PriceNode node) {this.left = node;}
    public void setMiddle(PriceNode node) {this.middle = node;}
    public void setRight(PriceNode node) {this.right = node;}
    public void setParent(PriceNode parent) {this.parent = parent;}

    public String getIdKey() {
        return this.idKey;
    }

    public float getPriceKey() {
        return this.priceKey;
    }

    public void setPriceKey(float key) {this.priceKey = key;}
    public void setIdKey(String key) {this.idKey = key;}

    public boolean isSentinel() {return sentinel;}

    public void setSentinel(boolean sentinel) {this.sentinel = sentinel;}

    public int getSize() {return this.size;}

    public boolean isSmaller(PriceNode otherPriceNode){

        if (this.priceKey < otherPriceNode.getPriceKey()) {
            return true;
        } if (this.priceKey > otherPriceNode.getPriceKey()) {
                return false;
        } else {
            return this.idKey.compareTo(otherPriceNode.getIdKey()) < 0;
            }
        }

    public void updateKey() {
        this.priceKey = this.getLeft().getPriceKey();
        if (this.getMiddle() != null) {
            this.priceKey = this.getMiddle().getPriceKey();
            this.idKey = this.getMiddle().getIdKey();
        }
        if (this.getRight() != null) {
            this.priceKey = this.getRight().getPriceKey();
            this.idKey = this.getRight().getIdKey();
        }
    }

    public void updateSentinelKey() {
        if (this.getLeft() != null && this.getMiddle() != null && this.getLeft().isSentinel()) {
            this.getLeft().setPriceKey(this.getMiddle().getPriceKey());
            this.getLeft().setIdKey(this.getMiddle().getIdKey());
        }
        if (this.getMiddle() != null && this.getMiddle().isSentinel()) {
            this.getMiddle().setPriceKey(this.getLeft().getPriceKey());
            this.getMiddle().setIdKey(this.getLeft().getIdKey());
        }
        if (this.getRight() != null && this.getRight().isSentinel()) {
            this.getRight().setPriceKey(this.getMiddle().getPriceKey());
            this.getRight().setIdKey(this.getMiddle().getIdKey());
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

    public float getMinSubtreeValue() {
        return minSubtreeValue;
    }
}
