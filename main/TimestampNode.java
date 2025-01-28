package main;

public class TimestampNode {
    private long key;
    private float priceDifference;
    private int size; // מספר העלים חוץ מהסנטינלס שמושרש בו
    private boolean sentinel;
    private TimestampNode parent;
    private TimestampNode left;
    private TimestampNode middle;
    private TimestampNode right;

    public TimestampNode() { //בשביל הסנטינלס
        this.sentinel = false;
        this.size = 0;
    }

    public TimestampNode(long timestamp, float priceDifference) {
        this.key = timestamp;
        this.priceDifference = priceDifference;
        this.sentinel = false;
        this.size = 1;
    }

    public boolean isLeaf() {
        return this.left == null; // because leaves have no sons
    }

    public TimestampNode getLeft() {return this.left;}
    public TimestampNode getMiddle() {return this.middle;}
    public TimestampNode getRight() {return this.right;}
    public TimestampNode getParent() {return this.parent;}

    public void setLeft(TimestampNode node) {this.left = node;}
    public void setMiddle(TimestampNode node) {this.middle = node;}
    public void setRight(TimestampNode node) {this.right = node;}
    public void setParent(TimestampNode parent) {this.parent = parent;}

    public long getKey() {
        return this.key;
    }

    public void setKey(long key) {this.key = key;}

    public boolean isSentinel() {return sentinel;}

    public void setSentinel(boolean sentinel) {
        this.sentinel = sentinel;
    }

    public int getSize() {return this.size;}

    public float getPriceDifference() {return priceDifference;}

    public boolean isSmaller(long otherKey){
        return this.key < otherKey;
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

