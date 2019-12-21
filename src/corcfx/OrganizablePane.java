/*
Copyright 2019, Cordell Stocker (cordellstocker@gmail.com)
All rights reserved.

This file is part of CORCFX.

    CORCFX is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    CORCFX is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with CORCFX.  If not, see <https://www.gnu.org/licenses/>.
*/
package corcfx;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Only supports placing Nodes horizontally, with a vertical shift when selected.
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

    public synchronized void orderChildren() {
        ObservableList<Node> children = this.getChildren();
        List<Node> copyOfChildren = new ArrayList<>(children);
        copyOfChildren.sort(new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return (int)(o1.getLayoutX() - o2.getLayoutX());
            }
        });

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

    public void setNodeAfterLast(Node node) {
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

    private Node findLeftMost(double startX) {
        ObservableList<Node> children = this.getChildren();
        Node leftMost = children.get(0);
        for (Node child : children) {
            if (child.getLayoutX() < leftMost.getLayoutX()) {
                leftMost = child;
            }
        }
        return leftMost;
    }

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

    public Node getSelectedNode() {
        return selectedNode;
    }

    public void clearSelectedNode() {
        selectedNode.setTranslateY(0);
        selectedNode = null;
    }
}
