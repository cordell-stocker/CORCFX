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

public abstract class OrganizablePane extends Pane {

    private Node nodeBeingMoved;
    private Node selectedNode;
    private Point oldMousePoint;

    private EventHandler<MouseEvent> mousePressed = event -> {
        if (event.getButton() == MouseButton.PRIMARY && event.getSource() instanceof Node) {
            oldMousePoint = new Point(event.getSceneX(), event.getSceneY());

            if (selectedNode != null) {
                resetSelectedNode(selectedNode);
            }

            selectedNode = (Node) event.getSource();
            nodeSelected(selectedNode);
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

    public OrganizablePane() {
        this.getChildren().addListener((ListChangeListener<? super Node>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (Node node : c.getAddedSubList()) {
                        node.setOnMousePressed(mousePressed);
                        node.setOnMouseDragged(mouseDragged);
                        node.setOnMouseReleased(mouseReleased);
                        nodeAdded(node);
                    }
                    orderChildren();
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
                            deselectNode();
                        }
                        nodeRemoved(node);
                    }
                    orderChildren();
                }
            }
        });
    }

    /**
     * Get the {@link Node} that most recently had the primary mouse
     * button pressed on it.
     *
     * @return the most recent Node pressed on by the mouse.
     */
    public Node getSelectedNode() {
        return selectedNode;
    }

    /**
     * Resets the current last selected {@link Node} if it exists and
     * sets the most recent selected Node to null.
     */
    public void deselectNode() {
        resetSelectedNode(selectedNode);
        selectedNode = null;
    }

    /**
     * Called whenever a child {@link Node} is added to this.
     * <p>
     * By default, this method is empty and can be overridden as an
     * API hook. Any override SHOULD call the super method.
     *
     * @param node the Node that was added.
     */
    protected void nodeAdded(Node node) {
    }

    /**
     * Called whenever a child {@link Node} is removed from this.
     * <p>
     * By default, this method is empty and can be overridden as an
     * API hook. Any override SHOULD call the super method.
     *
     * @param node the Node that was removed.
     */
    protected void nodeRemoved(Node node) {
    }

    /**
     * Called on the previously selected {@link Node} when a new Node
     * is pressed. Can also be called on the currently selected Node
     * by the {@link OrganizablePane#deselectNode()} method.
     * <p>
     * By default, this method is empty and can be overridden as an
     * API hook. Any override SHOULD call the super method.
     *
     * @param node the Node to reset.
     */
    protected void resetSelectedNode(Node node) {

    }

    /**
     * Called whenever a child {@link Node} has the primary mouse
     * button pressed on it.
     * <p>
     * The previous node that was selected will have the
     * {@link OrganizablePane#resetSelectedNode(Node)} method called
     * on it.
     * <p>
     * By default, this method is empty and can be overridden as an
     * API hook. Any override SHOULD call the super method.
     *
     * @param node the Node that had the primary mouse button pressed on.
     */
    protected void nodeSelected(Node node) {
    }

    /**
     * Called whenever the currently selected {@link Node} has the
     * primary mouse button pressed and the mouse is moving.
     * <p>
     * The mouse locations use the {@link javafx.scene.Scene}'s
     * coordinate plane.
     * <p>
     * By default, this method is empty and can be overridden as an
     * API hook. Any override SHOULD call the super method.
     *
     * @param node                  the Node that is being dragged by
     *                              the mouse.
     * @param newMouseLocation      the new location of the mouse since
     *                              the previous call to this method.
     * @param previousMouseLocation the location of the mouse from the
     *                              previous call to this method.
     */
    protected void moveNode(Node node, Point newMouseLocation, Point previousMouseLocation) {

    }

    /**
     * Called whenever the currently selected {@link Node} has the
     * primary mouse button released from being pressed.
     * <p>
     * The mouse location uses the {@link javafx.scene.Scene}'s
     * coordinate plane.
     * <p>
     * By default, this method is empty and can be overridden as an
     * API hook. Any override SHOULD call the super method.
     *
     * @param node       the Node that is no longer being pressed on.
     * @param mousePoint the location of the mouse upon release.
     */
    protected void onRelease(Node node, Point mousePoint) {

    }

    /**
     * Called after a child {@link Node} is a added or removed from
     * this.
     * <p>
     * By default, this method is empty and can be overridden as an
     * API hook. Any override SHOULD call the super method.
     */
    protected void orderChildren() {

    }

}
