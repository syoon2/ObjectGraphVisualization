package ch.hsr.ogv.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.List;
import java.util.Objects;

import javafx.geometry.Point3D;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Box;

import ch.hsr.ogv.controller.event.ArgumentedEventObject;
import ch.hsr.ogv.view.*;

public class SelectionController implements DragController.DragChangeEventListener {

    private volatile Selectable previousSelected = null;
    private volatile Selectable currentSelected = null;

    private Point3D previousSelectionCoord;
    private Point3D currentSelectionCoord;

    /**
     * The list of listeners of this controller.
     * 
     * @since 4.0
     */
    private transient List<SelectionChangeEventListener> listeners = Collections.synchronizedList(new ArrayList<>());

    public Point3D getPreviousSelectionCoord() {
        return previousSelectionCoord;
    }

    public boolean hasPreviousSelection() {
        return this.previousSelected != null;
    }

    public Selectable getPreviousSelected() {
        return this.currentSelected;
    }

    public boolean isPreviousSelected(Selectable selectable) {
        return this.previousSelected != null && this.previousSelected.equals(selectable);
    }

    public Point3D getCurrentSelectionCoord() {
        return currentSelectionCoord;
    }

    public boolean hasCurrentSelection() {
        return this.currentSelected != null;
    }

    public Selectable getCurrentSelected() {
        return this.currentSelected;
    }

    public boolean isCurrentSelected(Selectable selectable) {
        return this.currentSelected != null && this.currentSelected.equals(selectable);
    }

    public void enableSubSceneSelection(SubSceneAdapter subSceneAdapter) {
        selectOnMouseClicked(subSceneAdapter);
    }

    public void enablePaneBoxSelection(PaneBox paneBox, SubSceneAdapter subSceneAdapter, boolean allowTopTextInput) {
        selectOnMouseClicked(paneBox, subSceneAdapter, allowTopTextInput);
        selectOnDragDetected(paneBox, subSceneAdapter);
    }

    public void enableCenterLabelSelection(PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
        selectOnMouseClicked(paneBox.getCenterLabels(), paneBox, subSceneAdapter);
    }

    public void enableArrowSelection(Arrow arrow, SubSceneAdapter subSceneAdapter) {
        selectOnMouseClicked(arrow, subSceneAdapter);
    }

    public void enableArrowLabelSelection(Arrow arrow, SubSceneAdapter subSceneAdapter) {
        selectOnMouseClicked(arrow.getLabelStartLeft(), arrow, subSceneAdapter);
        selectOnMouseClicked(arrow.getLabelStartRight(), arrow, subSceneAdapter);
        selectOnMouseClicked(arrow.getLabelEndLeft(), arrow, subSceneAdapter);
        selectOnMouseClicked(arrow.getLabelEndRight(), arrow, subSceneAdapter);
    }

    private void selectOnMouseClicked(SubSceneAdapter subSceneAdapter) {
        subSceneAdapter.getSubScene().addEventHandler(MouseEvent.MOUSE_RELEASED, (MouseEvent me) -> {
            if ((MouseButton.PRIMARY.equals(me.getButton()) || MouseButton.SECONDARY.equals(me.getButton())) && me.isDragDetect() && me.getPickResult().getIntersectedNode() instanceof SubScene) {
                setSelected(me, subSceneAdapter, true, subSceneAdapter);
            }
        });

        subSceneAdapter.getFloor().addEventHandler(MouseEvent.MOUSE_RELEASED, (MouseEvent me) -> {
            if ((MouseButton.PRIMARY.equals(me.getButton()) || MouseButton.SECONDARY.equals(me.getButton())) && me.isDragDetect()) {
                setSelected(me, subSceneAdapter.getFloor(), true, subSceneAdapter);
            }
        });
    }

    private void selectOnMouseClicked(PaneBox paneBox, SubSceneAdapter subSceneAdapter, boolean allowTopTextInput) {

        paneBox.getBox().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
            if (MouseButton.PRIMARY.equals(me.getButton()) || MouseButton.SECONDARY.equals(me.getButton())) {
                paneBox.setAllLabelSelected(false);
                setSelected(me, paneBox, true, subSceneAdapter);
            }
        });

        paneBox.getCenter().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
            if (MouseButton.PRIMARY.equals(me.getButton()) || MouseButton.SECONDARY.equals(me.getButton())) {
                paneBox.setAllLabelSelected(false);
                setSelected(me, paneBox, true, subSceneAdapter);
            }
        });

        paneBox.getSelection().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
            if (MouseButton.PRIMARY.equals(me.getButton()) || MouseButton.SECONDARY.equals(me.getButton())) {
                paneBox.setAllLabelSelected(false);
            }
        });

        paneBox.getTopLabel().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
            if (MouseButton.PRIMARY.equals(me.getButton()) || MouseButton.SECONDARY.equals(me.getButton())) {
                paneBox.setLabelSelected(paneBox.getTopLabel(), true);
                setSelected(me, paneBox, true, subSceneAdapter);
            }
            if (allowTopTextInput && MouseButton.PRIMARY.equals(me.getButton()) && paneBox.isSelected() && me.getClickCount() >= 2) {
                paneBox.allowTopTextInput(true);
            }
        });
    }

    private void selectOnMouseClicked(List<Label> centerLabels, PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
        for (Label centerLabel : centerLabels) {
            centerLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
                if (MouseButton.PRIMARY.equals(me.getButton()) || MouseButton.SECONDARY.equals(me.getButton())) {
                    paneBox.setLabelSelected(centerLabel, true);
                    setSelected(me, paneBox, true, subSceneAdapter);
                    me.consume(); // otherwise this centerLabel's parent = getCenter() will be called
                }
                if (MouseButton.PRIMARY.equals(me.getButton()) && paneBox.isSelected() && me.getClickCount() >= 2) {
                    paneBox.allowCenterFieldTextInput(centerLabel, true);
                }
            });
        }
    }

    private void selectOnMouseClicked(Arrow arrow, SubSceneAdapter subSceneAdapter) {
        arrow.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
            if ((MouseButton.PRIMARY.equals(me.getButton()) || MouseButton.SECONDARY.equals(me.getButton()))) {
                arrow.setAllLabelSelected(false);
                setSelected(me, arrow, true, subSceneAdapter);
            }
        });

        for (Box lineSelectionHelper : arrow.getLineSelectionHelpers()) {
            lineSelectionHelper.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
                if ((MouseButton.PRIMARY.equals(me.getButton()) || MouseButton.SECONDARY.equals(me.getButton()))) {
                    arrow.setAllLabelSelected(false);
                    setSelected(me, arrow, true, subSceneAdapter);
                }
            });
        }

        arrow.getStartSelectionHelper().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
            if ((MouseButton.PRIMARY.equals(me.getButton()) || MouseButton.SECONDARY.equals(me.getButton()))) {
                arrow.setAllLabelSelected(false);
                setSelected(me, arrow, true, subSceneAdapter);
            }
        });

        arrow.getEndSelectionHelper().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
            if ((MouseButton.PRIMARY.equals(me.getButton()) || MouseButton.SECONDARY.equals(me.getButton()))) {
                arrow.setAllLabelSelected(false);
                setSelected(me, arrow, true, subSceneAdapter);
            }
        });
    }

    private void selectOnMouseClicked(ArrowLabel arrowLabel, Arrow arrow, SubSceneAdapter subSceneAdapter) {
        arrowLabel.getArrowText().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
            arrow.setAllLabelSelected(false);
            if (MouseButton.PRIMARY.equals(me.getButton()) || MouseButton.SECONDARY.equals(me.getButton())) {
                arrowLabel.setLabelSelected(true);
                setSelected(me, arrow, true, subSceneAdapter);
                me.consume();
            }
            if (MouseButton.PRIMARY.equals(me.getButton()) && arrow.isSelected() && me.getClickCount() >= 2) {
                arrowLabel.allowTextInput(true);
            }
        });
    }

    private void selectOnDragDetected(PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
        paneBox.getTopLabel().addEventHandler(MouseEvent.DRAG_DETECTED, (MouseEvent me) -> {
            if (MouseButton.PRIMARY.equals(me.getButton()) && me.isDragDetect() && !paneBox.isSelected()) {
                paneBox.setAllLabelSelected(false);
                setSelected(me, paneBox, true, subSceneAdapter);
            }
        });

        paneBox.getCenter().addEventHandler(MouseEvent.DRAG_DETECTED, (MouseEvent me) -> {
            if (MouseButton.PRIMARY.equals(me.getButton()) && me.isDragDetect() && !paneBox.isSelected()) {
                paneBox.setAllLabelSelected(false);
                setSelected(me, paneBox, true, subSceneAdapter);
            }
        });

        paneBox.getBox().addEventHandler(MouseEvent.DRAG_DETECTED, (MouseEvent me) -> {
            if (MouseButton.PRIMARY.equals(me.getButton()) && me.isDragDetect() && !paneBox.isSelected()) {
                paneBox.setAllLabelSelected(false);
                setSelected(me, paneBox, true, subSceneAdapter);
            }
        });
    }

    private void setSelected(MouseEvent me, Selectable selectable, boolean selected, SubSceneAdapter subSceneAdapter) {
        this.currentSelectionCoord = new Point3D(me.getX(), me.getY(), me.getZ());
        setSelected(selectable, selected, subSceneAdapter);
    }

    public void setSelected(Selectable selectable, boolean selected, SubSceneAdapter subSceneAdapter) {
        selectable.setSelected(selected);

        if (selected) {
            if (this.currentSelected != null && selectable != this.currentSelected && subSceneAdapter != null) {
                this.previousSelected = this.currentSelected; // current selection becomes previous selected object
                this.previousSelectionCoord = this.currentSelectionCoord;
                setSelected(this.currentSelected, false, subSceneAdapter); // deselect the old selected object
            }
            this.currentSelected = selectable;
            selectable.requestFocus();
            if (subSceneAdapter != null)
                subSceneAdapter.getFloor().toFront();
        }
        else {
            this.currentSelected = null;
        }
        synchronized (listeners) {
            var event = new SelectionChangeEvent(selectable);
            for (var listener : listeners) {
                listener.handle(event);
            }
        }
    }

    /**
     * Adds a listener to the listener list of this controller.
     * 
     * @param listener a listener
     * @throws NullPointerException if argument is {@code null}
     * 
     * @since 4.0
     */
    public void addListener(SelectionChangeEventListener listener) {
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
    public boolean removeListener(SelectionChangeEventListener listener) {
        return listeners.remove(listener);
    }

    /**
     * @since 4.0
     */
    @Override
    public void handle(DragController.DragChangeEvent event) {
        DragController dragController = event.getSource();
        if (!dragController.isDragInProgress() && hasCurrentSelection()) {
            setSelected(getCurrentSelected(), true, null);
        }
    }

    /**
     * The listener interface for receiving selection change events.
     * 
     * @author Sung Ho Yoon
     * @since 4.0
     */
    @FunctionalInterface
    public static interface SelectionChangeEventListener extends EventListener {
        /**
         * Invoked when an event occurs.
         * 
         * @param event the event to be processed
         */
        void handle(SelectionChangeEvent event);
    }

    /**
     * The event state object for mouse moves.
     * 
     * @author Sung Ho Yoon
     * @since 4.0
     */
    public class SelectionChangeEvent extends ArgumentedEventObject {
        /**
         * Constructs a new {@code SelectionChangeEvent}.
         * 
         * @param arg an optional argument
         */
        public SelectionChangeEvent(Object arg) {
            super(SelectionController.this, arg);
        }

        /**
         * Returns the controller which generated this event.
         * 
         * @return the controller which generated this event
         */
        @Override
        public SelectionController getSource() {
            return (SelectionController) super.source;
        }
    }

}
