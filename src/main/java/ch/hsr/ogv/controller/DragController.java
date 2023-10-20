package ch.hsr.ogv.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.List;
import java.util.Objects;

import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;

import ch.hsr.ogv.controller.event.ArgumentedEventObject;
import ch.hsr.ogv.view.PaneBox;
import ch.hsr.ogv.view.SubSceneAdapter;

public abstract class DragController {

    private volatile PaneBox selected = null;
    private volatile boolean dragInProgress = false;

    private List<DragChangeEventListener> listeners = Collections.synchronizedList(new ArrayList<>());

    protected void endOnMouseReleased(Group g, PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
        g.setOnMouseReleased((MouseEvent me) -> {
            setDragInProgress(subSceneAdapter, false);
            subSceneAdapter.getVerticalHelper().setVisible(false);
            paneBox.get().toBack();
            subSceneAdapter.getSubScene().setCursor(Cursor.DEFAULT);
        });
    }

    protected void setDragInProgress(SubSceneAdapter subSceneAdapter, boolean value) {
        this.dragInProgress = value;
        if (value) {
            subSceneAdapter.worldRestrictMouseEvents();
            subSceneAdapter.receiveMouseEvents(subSceneAdapter.getFloor(), subSceneAdapter.getVerticalHelper());
        }
        else {
            subSceneAdapter.worldReceiveMouseEvents();
            subSceneAdapter.restrictMouseEvents(subSceneAdapter.getVerticalHelper());
        }
        synchronized (listeners) {
            var event = new DragChangeEvent(this.selected);
            for (var listener : listeners) {
                listener.handle(event);
            }
        }
    }

    public boolean isDragInProgress() {
        return this.dragInProgress;
    }

    public void addListener(DragChangeEventListener listener) {
        listeners.add(Objects.requireNonNull(listener));
    }

    public boolean removeListener(DragChangeEventListener listener) {
        return listeners.remove(listener);
    }

    @FunctionalInterface
    public static interface DragChangeEventListener extends EventListener {
        void handle(DragChangeEvent event);
    }

    public class DragChangeEvent extends ArgumentedEventObject {
        public DragChangeEvent(Object arg) {
            super(DragController.this, arg);
        }

        @Override
        public DragController getSource() {
            return (DragController) super.source;
        }
    }

}
