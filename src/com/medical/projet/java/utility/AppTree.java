/*
 * 
 */
package com.medical.projet.java.utility;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

// TODO: Auto-generated Javadoc
/**
 * The Class AppTree.
 */
public class AppTree {

    /**
     * Prints the scene.
     *
     * @param scene the scene
     */
    public static void printScene(Scene scene) {
        System.out.println("///////////////Tree//////////////////");
        System.out.println("Scene Details:");
        System.out.println("Width: " + scene.getWidth());
        System.out.println("Height: " + scene.getHeight());
        System.out.println("Stylesheets: " + scene.getStylesheets());

        Parent root = scene.getRoot();
        printNode(root, 0);
    }

    /**
     * Prints the node.
     *
     * @param node the node
     * @param depth the depth
     */
    private static void printNode(javafx.scene.Node node, int depth) {
        if (node instanceof Parent) {
            Parent parent = (Parent) node;
            System.out.println(getIndent(depth) + "Parent: " + parent);

            for (javafx.scene.Node child : parent.getChildrenUnmodifiable()) {
                printNode(child, depth + 1);
            }
        } else {
            if (node instanceof Text) {
                Text textNode = (Text) node;
                System.out.println(getIndent(depth) + "Text: \"" + textNode.getText() + "\"");
            }

            if (node instanceof Label) {
                Label label = (Label) node;
                System.out.println(getIndent(depth + 1) + "Text: " + label.getText());
            }
        }
    }

    /**
     * Gets the indent.
     *
     * @param depth the depth
     * @return the indent
     */
    private static String getIndent(int depth) {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            indent.append("  ");
        }
        return indent.toString();
    }

}
