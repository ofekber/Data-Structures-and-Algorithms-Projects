public class StockTree {
    private StockNode root;

    public StockTree(StockNode root) {
        this.root = root;
    }

    public StockTree() {
        this.root = new StockNode();
        StockNode infSentinel = new StockNode();
        StockNode minusInfSentinel = new StockNode();
        infSentinel.setSentinel(true);
        minusInfSentinel.setSentinel(true);
        setChildren(this.root, minusInfSentinel, infSentinel, null);
    }

    public void setChildren(StockNode x, StockNode left, StockNode middle, StockNode right) {
        x.setLeft(left);
        x.setMiddle(middle);
        x.setRight(right);
        left.setParent(x);
        if (middle != null) {
            middle.setParent(x);
        }
        if (right != null) {
            right.setParent(x);
        }
        if (!x.isLeaf()) {
            x.updateSentinelKey();
        }
        x.updateKey();
        x.updateSize();
    }

    public StockNode insertAndSplit(StockNode x, StockNode z) {
        StockNode l = x.getLeft();
        StockNode m = x.getMiddle();
        StockNode r = x.getRight();

        if (r == null) {
            if ((!l.isSentinel()) && z.isSmaller(l.getKey())) {
                setChildren(x, z, l, m);
            } else if (m.isSentinel() || z.isSmaller(m.getKey())) {
                setChildren(x, l, z, m);
            } else {
                setChildren(x, l, m, z);
            }
            return null;
        }

        StockNode y = new StockNode();

        if (!l.isSentinel() && z.isSmaller(l.getKey())) {
            setChildren(x, z, l, null);
            setChildren(y, m, r, null);
        } else if (z.isSmaller(m.getKey())) {
            setChildren(x, l, z, null);
            setChildren(y, m, r, null);
        } else if (r.isSentinel() || z.isSmaller(r.getKey())) {
            setChildren(x, l, m, null);
            setChildren(y, z, r, null);
        } else {
            setChildren(x, l, m, null);
            setChildren(y, r, z, null);
        }
        return y;
    }

    public void insert(StockNode z) {
        StockNode y = this.root;
        while (!(y.isLeaf())) {
            if (!(y.getLeft().isSentinel()) && z.isSmaller(y.getLeft().getKey())) {
                y = y.getLeft();
            } else if (y.getRight() == null || (y.getMiddle().isSentinel()) || z.isSmaller(y.getMiddle().getKey())) {
                y = y.getMiddle();
            } else {
                y = y.getRight();
            }
        }
        StockNode x = y.getParent();
        z = insertAndSplit(x, z);
        while (x != this.root) {
            x = x.getParent();
            if (z != null) {
                z = insertAndSplit(x, z);
            } else {
                x.updateKey();
                x.updateSize();
            }
        }
        if (z != null) {
            StockNode w = new StockNode();
            setChildren(w, x, z, null);
            this.root = w;
        }
    }

    public StockNode borrowOrMerge(StockNode y) {
        StockNode z = y.getParent();
        if (y == z.getLeft()) {
            StockNode x = z.getMiddle();
            if (x.getRight() != null) {
                setChildren(y, y.getLeft(), x.getLeft(), null);
                setChildren(x, x.getMiddle(), x.getRight(), null);
            } else {
                setChildren(x, y.getLeft(), x.getLeft(), x.getMiddle());
                y = null;
                setChildren(z, x, z.getRight(), null);
            }
            return z;

        }
        if (y == z.getMiddle()) {
            StockNode x = z.getLeft();
            if (x.getRight() != null) {
                setChildren(y, x.getRight(), y.getLeft(), null);
                setChildren(x, x.getLeft(), x.getMiddle(), null);
            } else {
                setChildren(x, x.getLeft(), x.getMiddle(), y.getLeft());
                y = null;
                setChildren(z, x, z.getRight(), null);
            }
            return z;
        }
        StockNode x = z.getMiddle();
        if (x.getRight() != null) {
            setChildren(y, x.getRight(), y.getLeft(), null);
            setChildren(x, x.getLeft(), x.getMiddle(), null);
        } else {
            setChildren(x, x.getLeft(), x.getMiddle(), y.getLeft());
            y = null;
            setChildren(z, z.getLeft(), x, null);
        }
        return z;
    }

    public void delete(StockNode x) {
        StockNode y = x.getParent();
        if (x == y.getLeft()) {
            setChildren(y, y.getMiddle(), y.getRight(), null);
        } else if (x == y.getMiddle()) {
            setChildren(y, y.getLeft(), y.getRight(), null);
        } else {
            setChildren(y, y.getLeft(), y.getMiddle(), null);
        }
        while (y != null) {
            if (y.getMiddle() == null) {
                if (y != this.root) {
                    y = borrowOrMerge(y);
                } else {
                    this.root = y.getLeft();
                    y.getLeft().setParent(null);
                    y = null;
                }
            } else {
                y.updateKey();
                y.updateSize();
                y = y.getParent();
            }
        }
    }

    public StockNode search(String key) {
        StockNode current = this.root;

        while (current != null && !current.isLeaf()) {
            if (!current.getLeft().isSentinel() && !current.getLeft().isSmaller(key)) {
                current = current.getLeft();
            } else if (!current.getMiddle().isSentinel() && !current.getMiddle().isSmaller(key)) {
                current = current.getMiddle();
            } else if (current.getRight() != null && !current.getRight().isSentinel() && !current.getRight().isSmaller(key)) {
                current = current.getRight();
            } else {
                return null;
            }
        }

        if (current != null && current.getKey().equals(key)) {
            return current;
        }

        return null;
    }

    public StockNode getRoot(){return this.root;}
}




