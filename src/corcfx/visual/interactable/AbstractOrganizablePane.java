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

import corcfx.Point;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public abstract class AbstractOrganizablePane extends Pane {

    private Node nodeBeingMoved;
    private Node selectedNode;
    private Point oldMousePoint;

    private EventHandler<MouseEvent> mousePressed = event -> {
        if (event.getButton() == MouseButton.PRIMARY && event.getSource() instanceof Node) {
            oldMousePoint = new Point(event.getSceneX(), event.getSceneY());

            if (selectedNode != null) {
                resetSelectedNode();
            }

            selectedNode = (Node) event.getSource();
            selectNode(selectedNode);
            nodeBeingMoved = selectedNode;
        }
    };

    private EventHandler<MouseEvent> mouseDragged = event -> {
        if (event.getButton() == MouseButton.PRIMARY) {
            Point newMouseLocation = new Point(event.getSceneX(), event.getSceneY());
            moveNode(selectedNode, newMouseLocation, oldMousePoint);
            oldMousePoint = newMouseLocation;
        }
    };

    private EventHandler<MouseEvent> mouseReleased = event -> {
        oldMousePoint = new Point(event.getSceneX(), event.getSceneY());
        onRelease(nodeBeingMoved, oldMousePoint);
        nodeBeingMoved = null;
    };

    public AbstractOrganizablePane() {
        this.getChildren().addListener((ListChangeListener<? super Node>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (Node node : c.getAddedSubList()) {
                        node.setOnMousePressed(mousePressed);
                        node.setOnMouseDragged(mouseDragged);
                        node.setOnMouseReleased(mouseReleased);
                        addNode(node);
                    }
                }
                if (c.wasRemoved()) {
                    for (Node node : c.getRemoved()) {
                        if (node.getOnMousePressed() == mousePressed) {
                            node.setOnMousePressed(null);
                        }
                        if (node.getOnMouseDragged() == mouseDragged) {
                            node.setOnMouseDragged(null);
                        }
                        if (node.getOnMouseReleased() == mouseReleased) {
                            node.setOnMouseReleased(null);
                        }
                        if (node == selectedNode) {
                            deselectNode(node);
                        }
                    }
                }
            }
        });
    }

    protected abstract void resetSelectedNode();

    protected abstract void selectNode(Node node);

    protected abstract void moveNode(Node node, Point newMouseLocation, Point previousMouseLocation);

    protected abstract void onRelease(Node node, Point mousePoint);

    protected abstract void addNode(Node node);

    protected abstract void deselectNode(Node node);

    protected abstract void orderChildren();

}
