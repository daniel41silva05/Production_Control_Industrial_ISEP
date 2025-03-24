package org.project.ui;

import org.project.common.NaryTree;
import org.project.common.NaryTreeNode;
import org.project.controller.ProductionController;
import org.project.exceptions.DatabaseException;
import org.project.exceptions.OperationException;
import org.project.exceptions.ProductException;
import org.project.model.*;
import org.project.ui.utils.Utils;

import java.util.List;

public class ConsultProductionTreeUI implements Runnable {

    private final ProductionController controller;

    public ConsultProductionTreeUI() {
        this.controller = new ProductionController();
    }

    public void run() {

        try {

        String productID = Utils.readLineFromConsole("Enter Product ID: ");

        NaryTree<ProductionElement> tree = controller.getProductionTree(productID);

        if (tree == null) {
            System.out.println("\nProduction tree failed.");
        } else {
            showTree(tree);
        }

        } catch (ProductException | OperationException | DatabaseException e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    private void showTree(NaryTree<ProductionElement> tree) {
        printTree(tree.getRoot(), "", true);
    }

    private void printTree(NaryTreeNode<ProductionElement> node, String prefix, boolean isLast) {
        ProductionElement element = node.getElement();
        String elementStr = formatProductionElement(element);
        System.out.println(prefix + (isLast ? "└── " : "├── ") + elementStr);

        List<NaryTreeNode<ProductionElement>> children = node.getChildren();
        for (int i = 0; i < children.size(); i++) {
            printTree(children.get(i), prefix + (isLast ? "    " : "│   "), i == children.size() - 1);
        }
    }

    private String formatProductionElement(ProductionElement element) {
        Part part = element.getPart();
        Operation operation = element.getOperation();
        double quantity = element.getQuantity();

        StringBuilder sb = new StringBuilder();

        sb.append("[");
        if (part instanceof Product) {
            sb.append("Product");
        } else if (part instanceof Component) {
            sb.append("Component");
        } else if (part instanceof RawMaterial) {
            sb.append("RawMaterial");
        }
        sb.append(":").append(part.getId()).append("] ");

        sb.append(part.getName()).append(" ");

        sb.append("(OpID:").append(operation.getId())
                .append(", ").append(operation.getName()).append(") ");

        sb.append("x ").append(quantity);

        return sb.toString();
    }

}
