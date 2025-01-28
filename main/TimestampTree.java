package main;

public class TimestampTree {
    private TimestampNode root;


    public TimestampTree(TimestampNode root){
        this.root = root;
    }


    public TimestampTree() {
        this.root = new TimestampNode();
        TimestampNode infSentinel = new TimestampNode();
        TimestampNode minusInfSentinel = new TimestampNode();
        infSentinel.setSentinel(true); // מימוש של סנטינלס בעזרת בוילאן, איך עושים בעזרת מפתח אולי
        minusInfSentinel.setSentinel(true);
        setChildren(this.root, minusInfSentinel, infSentinel, null);

    }

    public void setChildren(TimestampNode x, TimestampNode left, TimestampNode middle, TimestampNode right) {
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

    public TimestampNode insertAndSplit(TimestampNode x, TimestampNode z) {
        TimestampNode l = x.getLeft();
        TimestampNode m = x.getMiddle();
        TimestampNode r = x.getRight();

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

        TimestampNode y = new TimestampNode();

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
    public void insert(TimestampNode z) {
        TimestampNode y = this.root;
        while (!(y.isLeaf())) {
            if (!(y.getLeft().isSentinel()) && z.isSmaller(y.getLeft().getKey())) {
                y = y.getLeft();
            } else if (y.getRight() == null || (y.getMiddle().isSentinel()) || z.isSmaller(y.getMiddle().getKey())) {
                y = y.getMiddle();
            } else {
                y = y.getRight();
            }
        }
        TimestampNode x = y.getParent();
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
            TimestampNode w = new TimestampNode();
            setChildren(w, x, z, null);
            this.root = w;
        }
    }

    public TimestampNode borrowOrMerge(TimestampNode y) {
        TimestampNode z = y.getParent();
        if (y == z.getLeft()) {
            TimestampNode x = z.getMiddle();
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
            TimestampNode x = z.getLeft();
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
        TimestampNode x = z.getMiddle();
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

    public void delete(TimestampNode x) {
        TimestampNode y = x.getParent();
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

    public TimestampNode search(long key) {
        if (this.root != null && this.root.isLeaf()) {
            if (this.root.getKey() == key) { // שיניתי את השורה המקורית ממקסים ושחר, לוודא שעובד טוב
                return this.root;
            } else {
                return null;
            }
        }
        if ((!root.getLeft().isSentinel()) && (!this.root.getLeft().isSmaller(key))) {
            return new TimestampTree(this.root.getLeft()).search(key);
        } else if (!this.root.getMiddle().isSmaller(key)) {
            return new TimestampTree(this.root.getMiddle()).search(key);
        } else {
            if (this.root.getRight() == null) {
                return null;
            }
            return new TimestampTree(this.root.getRight()).search(key);
        }
    }

    public TimestampNode getRoot() {return this.root;}
}



