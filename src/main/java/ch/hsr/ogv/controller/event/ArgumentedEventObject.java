/*
 * Copyright (c) 2023 Sung Ho Yoon. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

package ch.hsr.ogv.controller.event;

import java.util.EventObject;

/**
 * A type of event state object that contains an optional
 * argument.
 * 
 * @author Sung Ho Yoon
 * @since 4.0
 */
public class ArgumentedEventObject extends EventObject {

    /** Optional argument for the event. */
    protected transient Object argument;

    /**
     * Constructs a new {@code ArgumentedEventObject} without argument.
     * 
     * @param source the object on which the event initially occurred
     * @throws IllegalArgumentException if argument is {@code null}
     */
    protected ArgumentedEventObject(Object source) {
        this(source, null);
    }

    /**
     * Constructs a new {@code ArgumentedEventObject} with an argument.
     * 
     * @param source the object on which the event initially occurred
     * @throws IllegalArgumentException if {@code source} is {@code null}
     */
    protected ArgumentedEventObject(Object source, Object argument) {
        super(source);
        this.argument = argument;
    }

    /**
     * Returns the argument provided for the event.
     * 
     * @return the argument for the event, or {@code null} if no argument was
     *         provided
     */
    public Object getArgument() {
        return argument;
    }

}
