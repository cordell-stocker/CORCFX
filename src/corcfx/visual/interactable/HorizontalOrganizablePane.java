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
import corcfx.experimental.CardImageView;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 * A user interactable {@link Pane} that allows for the reordering of
 * the contained {@link CardImageView}s by clicking and dragging the
 * child Nodes.
 * <p>
 * The most recently clicked on child Node will be shifted the specified
 * amount in the constructor. That Node is retrievable through the
 * {@link HorizontalOrganizablePane#getSelectedNode()} method.
 * <p>
 * Placing Nodes horizontally, with a vertical shift when selected.
 */
public class HorizontalOrganizablePane extends OrganizablePane {

    private final double SELECTED_VERTICAL_SHIFT;
    private final double HORIZONTAL_SPACING;

    /**
     * Creates a {@link Pane} that can have its children
     * reordered by the user by clicking and dragging the
     * child {@link Node}s.
     *
     * @param spacing       the spacing between each child Node.
     * @param verticalShift the amount to vertically shift the selected Node.
     */
    public HorizontalOrganizablePane(double spacing, double verticalShift) {
        this.HORIZONTAL_SPACING = spacing;
        this.SELECTED_VERTICAL_SHIFT = verticalShift;
    }

    /**
     * Sets the layoutX of each child {@link Node} to follow
     * the spacing specified by the constructor as well as
     * fix the layering, with Nodes with a smaller layoutX
     * being behind Nodes with a larger layoutX.
     */
    @Override
    protected synchronized void orderChildren() {
        ObservableList<Node> children = this.getChildren();
        List<Node> copyOfChildren = new ArrayList<>(children);
        copyOfChildren.sort((o1, o2) -> (int) (o1.getLayoutX() - o2.getLayoutX()));

        for (int i = 0; i < copyOfChildren.size(); i++) {
            if (i == 0) {
                copyOfChildren.get(0).setLayoutX(this.getLayoutX());
                // larger view order = farther behind
                copyOfChildren.get(0).setViewOrder(copyOfChildren.size());
            } else {
                setNodeAfterOther(copyOfChildren.get(i - 1), copyOfChildren.get(i));
                copyOfChildren.get(i).setViewOrder(copyOfChildren.size() - i);
            }
        }
    }

    private void setNodeAfterOther(Node lead, Node follow) {
        double lastNodeMaxX = lead.getLayoutX() + lead.getBoundsInLocal().getWidth();
        follow.setLayoutX(lastNodeMaxX + this.HORIZONTAL_SPACING);
    }

    /**
     * Set the Node to be the rightmost child.
     *
     * @param node the Node that was added.
     */
    @Override
    protected void nodeAdded(Node node) {
        setNodeAfterLast(node);
    }

    private void setNodeAfterLast(Node node) {
        ObservableList<Node> children = this.getChildren();
        if (children.size() == 1) {
            node.setLayoutX(this.getLayoutX());
        } else {
            Node last = children.get(0);
            for (Node child : children) {
                if (child.getLayoutX() > last.getLayoutX()) {
                    last = child;
                }
            }
            double lastNodeMaxX = last.getLayoutX() + last.getBoundsInLocal().getWidth();
            node.setLayoutX(lastNodeMaxX + this.HORIZONTAL_SPACING);
        }
    }

    /**
     * Unshift the node.
     *
     * @param node the Node to reset.
     */
    @Override
    protected void resetSelectedNode(Node node) {
        node.setTranslateY(0);
    }

    /**
     * Shifts the node by the vertical shift specified in the
     * constructor.
     *
     * @param node the Node that had the primary mouse button pressed on.
     */
    @Override
    protected void nodeSelected(Node node) {
        node.setTranslateY(SELECTED_VERTICAL_SHIFT);
        node.setViewOrder(-1.0); // Move to front
    }

    /**
     * Allows the Node to be dragged horizontally.
     *
     * @param node             the Node that is being dragged by
     *                         the mouse.
     * @param newMouseLocation the new location of the mouse since
     *                         the previous call to this method.
     * @param oldMouseLocation the location of the mouse from the
     *                         previous call to this method.
     */
    @Override
    protected void moveNode(Node node, Point newMouseLocation, Point oldMouseLocation) {
        double deltaX = newMouseLocation.X - oldMouseLocation.X;
        double currentX = node.getLayoutX();
        node.setLayoutX(currentX + deltaX);
    }

    /**
     * After releasing a Node, order all children.
     *
     * @param node       the Node that is no longer being pressed on.
     * @param mousePoint the location of the mouse upon release.
     */
    @Override
    protected void onRelease(Node node, Point mousePoint) {
        this.orderChildren();
    }

}
