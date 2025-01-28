package main;

public class PriceTree {
    private PriceNode root;

    public PriceTree(PriceNode root) {
        this.root = root;
    }

    public PriceTree() {
        this.root = new PriceNode();
        PriceNode infSentinel = new PriceNode();
        PriceNode minusInfSentinel = new PriceNode();
        infSentinel.setSentinel(true); // מימוש של סנטינלס בעזרת בוילאן, איך עושים בעזרת מפתח אולי
        minusInfSentinel.setSentinel(true);
        setChildren(this.root, minusInfSentinel, infSentinel, null);

    }

    public void setChildren(PriceNode x, PriceNode left, PriceNode middle, PriceNode right) {
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
        x.updateMinSubtreeValue();
    }

    public PriceNode insertAndSplit(PriceNode x, PriceNode z) {
        PriceNode l = x.getLeft();
        PriceNode m = x.getMiddle();
        PriceNode r = x.getRight();

        if (r == null) {
            if ((!l.isSentinel()) && z.isSmaller(l)) {
                setChildren(x, z, l, m);
            } else if (m.isSentinel() || z.isSmaller(m)) {
                setChildren(x, l, z, m);
            } else {
                setChildren(x, l, m, z);
            }
            return null;
        }

        PriceNode y = new PriceNode();

        if (!l.isSentinel() && z.isSmaller(l)) {
            setChildren(x, z, l, null);
            setChildren(y, m, r, null);
        } else if (z.isSmaller(m)) {
            setChildren(x, l, z, null);
            setChildren(y, m, r, null);
        } else if (r.isSentinel() || z.isSmaller(r)) {
            setChildren(x, l, m, null);
            setChildren(y, z, r, null);
        } else {
            setChildren(x, l, m, null);
            setChildren(y, r, z, null);
        }
        return y;
    }

    public void insert(PriceNode z) {
        PriceNode y = this.root;
        while (!(y.isLeaf())) {
            if (!(y.getLeft().isSentinel()) && z.isSmaller(y.getLeft())) {
                y = y.getLeft();
            } else if (y.getRight() == null || (y.getMiddle().isSentinel()) || z.isSmaller(y.getMiddle())) {
                y = y.getMiddle();
            } else {
                y = y.getRight();
            }
        }
        PriceNode x = y.getParent();
        z = insertAndSplit(x, z);
        while (x != this.root) {
            x = x.getParent();
            if (z != null) {
                z = insertAndSplit(x, z);
            } else {
                x.updateKey();
                x.updateSize();
                x.updateMinSubtreeValue();
            }
        }
        if (z != null) {
            PriceNode w = new PriceNode();
            setChildren(w, x, z, null);
            this.root = w;
        }
    }

    public PriceNode borrowOrMerge(PriceNode y) {
        PriceNode z = y.getParent();
        if (y == z.getLeft()) {
            PriceNode x = z.getMiddle();
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
            PriceNode x = z.getLeft();
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
        PriceNode x = z.getMiddle();
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

    public void delete(PriceNode x) {
        PriceNode y = x.getParent();
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
                y.updateMinSubtreeValue();
                y = y.getParent();
                // צריך לבדוק אם צריך לשים פה את המינימום בעץ שיתעדכן גם    !!!!!!!!!!!!!
            }
        }
    }

    public PriceNode getRoot() {
        return this.root;
    }


    private int numOfSmallerRec(PriceNode node, Float price) {
        if (node == null || node.isSentinel()) {
            return 0;
        }
        int count = 0;

        if (node.isLeaf()) {
            if (node.getPriceKey() < price) {
                count++;
            }
            return count; // Return count after processing the leaf
        }

        // Accumulate counts from applicable branches
        if (node.getLeft() != null && price > node.getLeft().getMinSubtreeValue()) {
            count += numOfSmallerRec(node.getLeft(), price);
        }
        if (node.getMiddle() != null && price > node.getMiddle().getMinSubtreeValue()) {
            count += numOfSmallerRec(node.getMiddle(), price);
        }
        if (node.getRight() != null && price > node.getRight().getMinSubtreeValue()) {
            count += numOfSmallerRec(node.getRight(), price);
        }

        return count;
    }

    private int numOfHigherRec(PriceNode node, Float price) {
        if (node == null || node.isSentinel()) {
            return 0;
        }
        int count = 0;

        if (node.isLeaf()) {
            if (node.getPriceKey() > price) {
                count++;
            }
            return count; // Return count after processing the leaf
        }

        // Accumulate counts from applicable branches
        if (node.getLeft() != null && price < node.getLeft().getPriceKey()) {
            count += numOfHigherRec(node.getLeft(), price);
        }
        if (node.getMiddle() != null && price < node.getMiddle().getPriceKey()) {
            count += numOfHigherRec(node.getMiddle(), price);
        }
        if (node.getRight() != null && price < node.getRight().getPriceKey()) {
            count += numOfHigherRec(node.getRight(), price);
        }

        return count;
    }


    public int getCountInRange(PriceNode node, Float price1, Float price2) {
        int count = 0;
        count = numOfSmallerRec(node, price1) + numOfHigherRec(node, price2);
        return this.root.getSize() - count;
    }


    public String[] stocksInRange(PriceNode priceNode, Float price1, Float price2) {
        // Create an array to hold the results and pass it through recursion.
        String[] result = new String[getCountInRange(priceNode, price1, price2)];
        priceNodesInRange(priceNode, price1, price2, result, new int[]{0});
        return result;
    }

    private void priceNodesInRange(PriceNode node, Float price1, Float price2, String[] result, int[] index) {
        if (node == null || node.isSentinel()) {
            return;
        }

        // Check if the current node's subtree is entirely irrelevant
        if (node.getMinSubtreeValue() > price2) {
            return; // All nodes in this subtree are larger than price2
        }

        if (node.getPriceKey() < price1) {
            return; // All nodes in this subtree are smaller than price1
        }

        // Traverse the left subtree if potentially relevant
        if (node.getLeft() != null && node.getLeft().getMinSubtreeValue() <= price2) {
            priceNodesInRange(node.getLeft(), price1, price2, result, index);
        }

        // Check current node
        if (node.isLeaf() && node.getPriceKey() >= price1 && node.getPriceKey() <= price2) {
            result[index[0]++] = node.getIdKey();
        }

        // Traverse the middle subtree if potentially relevant
        if (node.getMiddle() != null && node.getMiddle().getMinSubtreeValue() <= price2) {
            priceNodesInRange(node.getMiddle(), price1, price2, result, index);
        }

        // Traverse the right subtree if potentially relevant
        if (node.getRight() != null && node.getRight().getMinSubtreeValue() <= price2) {
            priceNodesInRange(node.getRight(), price1, price2, result, index);
        }
    }

}

