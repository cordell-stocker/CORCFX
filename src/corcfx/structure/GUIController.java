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

public abstract class GUIController extends Application {

    public void addStartButton(Pane pane, Runnable starter, String title) {
        Button start = new Button("Start Game");
        this.addNodeOnPlatformThread(pane, start);
        start.setOnAction(e -> {
            Thread gameThread = new Thread(starter);
            gameThread.setName(title + " Model Thread");
            gameThread.setDaemon(true);
            gameThread.start();
            this.removeNodeOnPlatformThread(pane, start);
        });
    }

    protected void addNodeOnPlatformThread(Pane parent, Node child) {
        Platform.runLater(() -> parent.getChildren().add(child));
    }

    protected void removeNodeOnPlatformThread(Pane parent, Node child) {
        Platform.runLater(() -> parent.getChildren().remove(child));
    }
}
