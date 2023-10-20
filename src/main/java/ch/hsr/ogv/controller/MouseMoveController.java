package ch.hsr.ogv.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.List;
import java.util.Objects;

import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;

import ch.hsr.ogv.controller.event.ArgumentedEventObject;
import ch.hsr.ogv.view.Floor;
import ch.hsr.ogv.view.PaneBox;
import ch.hsr.ogv.view.VerticalHelper;

public class MouseMoveController {

    /**
     * The list of listeners of this controller.
     * 
     * @since 4.0
     */
    private transient List<MouseMoveEventListener> listeners = Collections.synchronizedList(new ArrayList<>());

    public void enableMouseMove(Floor floor) {

        floor.addEventHandler(MouseEvent.MOUSE_MOVED, (MouseEvent me) -> {
            PickResult pick = me.getPickResult();
            Point3D movePoint = pick.getIntersectedNode().localToParent(pick.getIntersectedPoint());
            notifyListeners(movePoint);
        });

    }

    public void enableMouseMove(VerticalHelper verticalHelper) {
        verticalHelper.addEventHandler(MouseEvent.MOUSE_MOVED, (MouseEvent me) -> {
            PickResult pick = me.getPickResult();
            Point3D movePoint = pick.getIntersectedNode().localToParent(pick.getIntersectedPoint());
            notifyListeners(movePoint);
        });
    }

    public void enableMouseMove(PaneBox paneBox) {
        paneBox.get().addEventHandler(MouseEvent.MOUSE_MOVED, (MouseEvent me) -> {
            notifyListeners(paneBox);
        });
    }

    /**
     * Adds a listener to the listener list of this controller.
     * 
     * @param listener a listener
     * @throws NullPointerException if argument is {@code null}
     * 
     * @since 4.0
     */
    public void addListener(MouseMoveEventListener listener) {
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
    public boolean removeListener(MouseMoveEventListener listener) {
        return listeners.remove(listener);
    }

    /**
     * Notifies all listeners
     * 
     * @param arg the optional argument for the event
     * 
     * @since 4.0
     */
    private void notifyListeners(Object arg) {
        synchronized (listeners) {
            MouseMoveEvent event = new MouseMoveEvent(arg);
            for (var listener : listeners) {
                listener.handle(event);
            }
        }
    }

    /**
     * The listener interface for receiving mouse move events.
     * 
     * @author Sung Ho Yoon
     * @since 4.0
     */
    @FunctionalInterface
    public static interface MouseMoveEventListener extends EventListener {
        /**
         * Invoked when an event occurs.
         * 
         * @param event the event to be processed
         */
        void handle(MouseMoveEvent event);
    }

    /**
     * The event state object for mouse moves.
     * 
     * @author Sung Ho Yoon
     * @since 4.0
     */
    public class MouseMoveEvent extends ArgumentedEventObject {
        /**
         * Constructs a new {@code MouseMoveEvent}.
         * 
         * @param arg an optional argument
         */
        public MouseMoveEvent(Object arg) {
            super(MouseMoveController.this, arg);
        }

        /**
         * Returns the controller which generated this event.
         * 
         * @return the controller which generated this event
         */
        @Override
        public MouseMoveController getSource() {
            return (MouseMoveController) super.source;
        }
    }

}
