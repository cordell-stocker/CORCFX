/*
 * Copyright 2019, Cordell Stocker (cordellstocker@gmail.com)
 * All rights reserved.
 *
 * This file is part of CORCFX.
 *
 *     CORCFX is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CORCFX is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CORCFX.  If not, see <https://www.gnu.org/licenses/>.
 */
package corcfx.visual.interactable;

import corcfx.experimental.CardImageView;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

/**
 * A user interactable {@link Pane} that allows for the reordering of
 * the contained {@link CardImageView}s by clicking and dragging the
 * child Nodes.
 * <p>
 * The most recently clicked on child Node will be shifted the specified
 * amount in the constructor. That Node is retrievable through the
 * {@link OrganizablePane#getSelectedNode()} method.
 * <p>
 * Only supports placing Nodes horizontally, with a vertical shift
 * when selected.
 */
public class OrganizablePane extends Pane {

    private final double SELECTED_VERTICAL_SHIFT;
    private final double HORIZONTAL_SPACING;

    private Node nodeBeingMoved;
    private Node selectedNode;
    private double mouseX;

    private EventHandler<MouseEvent> mouseReleased;
    private EventHandler<MouseEvent> mouseDragged;
    private EventHandler<MouseEvent> mousePressed;

    /**
     * Creates a {@link Pane} that can have its children
     * reordered by the user by clicking and dragging the
     * child {@link Node}s.
     *
     * @param spacing       the spacing between each child Node.
     * @param verticalShift the amount to vertically shift the selected Node.
     */
    public OrganizablePane(double spacing, double verticalShift) {
        this.HORIZONTAL_SPACING = spacing;
        this.SELECTED_VERTICAL_SHIFT = verticalShift;
        this.setMouseActions();
        this.setListener();
    }

    private void setListener() {
        this.getChildren().addListener((ListChangeListener<Node>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (Node node : c.getAddedSubList()) {
                        node.setOnMousePressed(this.mousePressed);
                        node.setOnMouseDragged(this.mouseDragged);
                        node.setOnMouseReleased(this.mouseReleased);
                        addNodeAfterLast(node);
                    }
                }
                if (c.wasRemoved()) {
                    for (Node node : c.getRemoved()) {
                        node.setOnMousePressed(null);
                        node.setOnMouseDragged(null);
                        node.setOnMouseReleased(null);
                        if (node == this.selectedNode) {
                            selectedNode.setTranslateY(0);
                            selectedNode = null;
                        }
                    }
                }
            }
        });
    }

    private void setMouseActions() {
        this.setMousePressed();
        this.setMouseDragged();
        this.setMouseReleased();
    }

    private void setMousePressed() {
        this.mousePressed = e -> {
            try {
                if (e.getButton() == MouseButton.PRIMARY && e.getSource() instanceof Node) {
                    mouseX = e.getSceneX();
                    nodeBeingMoved = (Node) e.getSource();

                    if (selectedNode != null) {
                        selectedNode.setTranslateY(0);
                    }

                    selectedNode = nodeBeingMoved;
                    selectedNode.setTranslateY(SELECTED_VERTICAL_SHIFT);
                    selectedNode.setViewOrder(-1.0);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        };
    }

    private void setMouseDragged() {
        this.mouseDragged = e -> {
            try {
                if (e.getButton() == MouseButton.PRIMARY) {
                    double deltaX = e.getSceneX() - mouseX;
                    double currentX = nodeBeingMoved.getLayoutX();
                    nodeBeingMoved.setLayoutX(currentX + deltaX);
                    mouseX = e.getSceneX();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        };
    }

    private void setMouseReleased() {
        this.mouseReleased = e -> {
            try {
                nodeBeingMoved = null;
                selectedNode.setViewOrder(0.0);
                orderChildren();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        };
    }

    /**
     * Sets the layoutX of each child {@link Node} to follow
     * the spacing specified by the constructor as well as
     * fix the layering, with Nodes with a smaller layoutX
     * being behind Nodes with a larger layoutX.
     */
    protected synchronized void orderChildren() {
        ObservableList<Node> children = this.getChildren();
        List<Node> copyOfChildren = new ArrayList<>(children);
        copyOfChildren.sort((o1, o2) -> (int) (o1.getLayoutX() - o2.getLayoutX()));

        for (int i = 0; i < copyOfChildren.size(); i++) {
            if (i == 0) {
                copyOfChildren.get(0).setLayoutX(this.getLayoutX());
                copyOfChildren.get(0).setViewOrder(copyOfChildren.size());
            } else {
                addNodeAfterOther(copyOfChildren.get(i - 1), copyOfChildren.get(i), copyOfChildren.size() - i);
            }
        }
    }

    private void addNodeAfterOther(Node lead, Node follow, double order) {
        double lastNodeMaxX = lead.getLayoutX() + lead.getBoundsInLocal().getWidth();
        follow.setLayoutX(lastNodeMaxX + this.HORIZONTAL_SPACING);
        follow.setViewOrder(order);
    }

    /**
     * Sets the specified {@link Node} to have a layoutX value
     * to be after the existing right most child Node, following
     * the horizontal spacing specified by the constructor.
     *
     * @param node the Node to have its layoutX set.
     */
    protected void setNodeAfterLast(Node node) {
        ObservableList<Node> children = this.getChildren();
        if (!children.isEmpty()) {
            Node last = children.get(0);
            for (Node child : children) {
                if (child.getLayoutX() > last.getLayoutX()) {
                    last = child;
                }
            }
            double lastNodeMaxX = last.getLayoutX() + last.getBoundsInLocal().getWidth();
            node.setLayoutX(lastNodeMaxX + this.HORIZONTAL_SPACING);
        } else {
            node.setLayoutX(this.getLayoutX());
        }

    }

    /**
     * Adds the specified {@link Node} to appear after the
     * existing right most child Node.
     *
     * Calls {@link OrganizablePane#setNodeAfterLast(Node)}
     * and then adds the Node to this {@link Pane}s children.
     *
     * @param node the Node to be added.
     */
    public void addNodeAfterLast(Node node) {
        setNodeAfterLast(node);
        this.getChildren().add(node);
    }

    public double getSelectedVerticalShift() {
        return SELECTED_VERTICAL_SHIFT;
    }

    public double getHorizontalSpacing() {
        return HORIZONTAL_SPACING;
    }

    /**
     * Gets the last clicked child {@link Node} if it exists.
     *
     * @return the last clicked child Node.
     */
    public Node getSelectedNode() {
        return selectedNode;
    }

    /**
     * Deselects the last clicked child {@link Node}.
     * This will reset the Node's vertical shift.
     */
    public void clearSelectedNode() {
        selectedNode.setTranslateY(0);
        selectedNode = null;
    }
}
