/*
 * Copyright (c) 2023 Sung Ho Yoon. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

package ch.hsr.ogv.controller.event;

import java.util.EventObject;

public class ArgumentedEventObject extends EventObject {

    protected transient Object argument;

    protected ArgumentedEventObject(Object source) {
        this(source, null);
    }

    protected ArgumentedEventObject(Object source, Object argument) {
        super(source);
        this.argument = argument;
    }

    public Object getArgument() {
        return argument;
    }

}
