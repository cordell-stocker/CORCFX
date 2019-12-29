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

package corcfx.structure;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

/**
 * Provides base utility for View operations in the
 * Model-View-Controller paradigm.
 */
public abstract class GUIController extends Application {

    /**
     * Creates and adds a start {@link Button} to the specified
     * {@link Pane}. When the button is clicked, the specified
     * {@link Runnable} will be ran in a new {@link Thread}.
     * <p>
     * The start button will add and remove itself from the specified
     * Pane.
     *
     * @param parent  the Pane to add the start Button to.
     * @param starter the Runnable that will start the Model Thread.
     * @param title   the name for the Model Thread.
     */
    public void addStartButton(Pane parent, Runnable starter, String title) {
        Button startButton = new Button("Start Game");
        this.addNodeOnPlatformThread(parent, startButton);
        startButton.setOnAction(e -> {
            Thread gameThread = new Thread(starter);
            gameThread.setName(title + " Model Thread");
            gameThread.setDaemon(true);
            gameThread.start();
            this.removeNodeOnPlatformThread(parent, startButton);
        });
    }

    /**
     * Will add the child {@link Node} to the parent Node's children.
     *
     * @param parent the Node to have the child added to.
     * @param child  the Node to add to the parent Node.
     */
    protected void addNodeOnPlatformThread(Pane parent, Node child) {
        Platform.runLater(() -> parent.getChildren().add(child));
    }

    /**
     * Will remove the child {@link Node} to the parent Node's children.
     *
     * @param parent the Node to have the child removed from.
     * @param child  the Node to remove from the parent Node.
     */
    protected void removeNodeOnPlatformThread(Pane parent, Node child) {
        Platform.runLater(() -> parent.getChildren().remove(child));
    }
}
