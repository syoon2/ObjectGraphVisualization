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

    /**
     * The list of listeners of this controller.
     * 
     * @since 4.0
     */
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

    /**
     * Adds a listener to the listener list of this controller.
     * 
     * @param listener a listener
     * @throws NullPointerException if argument is {@code null}
     * 
     * @since 4.0
     */
    public void addListener(DragChangeEventListener listener) {
        listeners.add(Objects.requireNonNull(listener));
    }

    /**
     * Removes a listener from the listener list of this controller.
     * 
     * @param listener the listener to be removed
     * @return if the specified listener was removed by the call
     * 
     * @since 4.0
     */
    public boolean removeListener(DragChangeEventListener listener) {
        return listeners.remove(listener);
    }

    /**
     * The listener interface for receiving drag change events.
     * 
     * @author Sung Ho Yoon
     * @since 4.0
     */
    @FunctionalInterface
    public static interface DragChangeEventListener extends EventListener {
        /**
         * Invoked when an event occurs.
         * 
         * @param event the event to be processed
         */
        void handle(DragChangeEvent event);
    }

    /**
     * The event state object for drag changes.
     * 
     * @author Sung Ho Yoon
     * @since 4.0
     */
    public class DragChangeEvent extends ArgumentedEventObject {

        /**
         * Constructs a new {@code DragChangeEvent}.
         * 
         * @param arg an optional argument
         */
        public DragChangeEvent(Object arg) {
            super(DragController.this, arg);
        }

        /**
         * Returns the controller which generated this event.
         * 
         * @return the controller which generated this event
         */
        @Override
        public DragController getSource() {
            return (DragController) super.source;
        }
    }

}
