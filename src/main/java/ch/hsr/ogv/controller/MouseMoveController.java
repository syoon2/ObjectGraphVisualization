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

    public void addListener(MouseMoveEventListener listener) {
        listeners.add(Objects.requireNonNull(listener));
    }

    public boolean removeListener(MouseMoveEventListener listener) {
        return listeners.remove(listener);
    }

    private void notifyListeners(Object arg) {
        synchronized (listeners) {
            MouseMoveEvent event = new MouseMoveEvent(arg);
            for (var listener : listeners) {
                listener.handle(event);
            }
        }
    }

    @FunctionalInterface
    public static interface MouseMoveEventListener extends EventListener {
        void handle(MouseMoveEvent event);
    }

    public class MouseMoveEvent extends ArgumentedEventObject {
        public MouseMoveEvent(Object arg) {
            super(MouseMoveController.this, arg);
        }

        @Override
        public MouseMoveController getSource() {
            return (MouseMoveController) super.source;
        }
    }

}
