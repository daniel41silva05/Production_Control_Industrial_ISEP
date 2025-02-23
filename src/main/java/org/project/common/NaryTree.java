/**
 * Represents a generic n-ary tree structure, where each node can have multiple children.
 * This structure supports operations such as finding a node and merging subtrees.
 *
 * @param <T> The type of elements stored in the tree.
 */
package org.project.common;

public class NaryTree<T> {

    private NaryTreeNode<T> root;

    /**
     * Constructs an N-ary tree with the specified root data.
     *
     * @param rootData The data to be stored in the root node of the tree.
     */
    public NaryTree(T rootData) {
        this.root = new NaryTreeNode<>(rootData, null);
    }

    /**
     * Gets the root node of the tree.
     *
     * @return The root node of the tree.
     */
    public NaryTreeNode<T> getRoot() {
        return root;
    }

    /**
     * Sets the root node of the tree.
     *
     * @param root The root node to set.
     */
    public void setRoot(NaryTreeNode<T> root) {
        this.root = root;
    }

    /**
     * Finds a node in the tree that contains the specified element.
     *
     * @param element The element to find in the tree.
     * @return The node containing the element, or {@code null} if not found.
     */
    public NaryTreeNode<T> findNode(T element) {
        return findNodeRecursive(root, element);
    }

    /**
     * Recursively searches for a node containing the specified element starting from the given node.
     *
     * @param node    The node to start the search from.
     * @param element The element to find.
     * @return The node containing the element, or {@code null} if not found.
     */
    private NaryTreeNode<T> findNodeRecursive(NaryTreeNode<T> node, T element) {
        if (node == null) return null;
        if (node.getElement().equals(element)) return node;
        for (NaryTreeNode<T> child : node.getChildren()) {
            NaryTreeNode<T> result = findNodeRecursive(child, element);
            if (result != null) return result;
        }
        return null;
    }

    /**
     * Merges a subtree into the current tree by adding all children of the root of the subtree
     * to the target node in the current tree.
     *
     * @param targetNode The node in the current tree to which the subtree will be merged.
     * @param subtree    The subtree to merge into the current tree.
     */
    public void mergeTree(NaryTreeNode<T> targetNode, NaryTree<T> subtree) {
        for (NaryTreeNode<T> child : subtree.getRoot().getChildren()) {
            NaryTreeNode<T> clonedSubtreeRoot = cloneSubtree(child, targetNode);
            targetNode.getChildren().add(clonedSubtreeRoot);
        }
    }

    /**
     * Recursively clones a subtree, creating a copy of the structure starting from the given node.
     *
     * @param node   The node to clone.
     * @param parent The parent node for the cloned subtree.
     * @return The root node of the cloned subtree.
     */
    private NaryTreeNode<T> cloneSubtree(NaryTreeNode<T> node, NaryTreeNode<T> parent) {
        NaryTreeNode<T> newNode = new NaryTreeNode<>(node.getElement(), parent);
        for (NaryTreeNode<T> child : node.getChildren()) {
            newNode.getChildren().add(cloneSubtree(child, newNode));
        }
        return newNode;
    }

    /**
     * Returns a string representation of the n-ary tree, showing the root's element.
     *
     * @return A string representation of the tree.
     */
    @Override
    public String toString() {
        return "Tree{" +
                "root=" + (root != null ? root.getElement() : "null") +
                '}';
    }
}
