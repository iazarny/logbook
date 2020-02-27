package com.yahoo.igor.spring;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.textfield.TextArea;

public class MyCustopmTab extends Tab {

    public MyCustopmTab() {
        super();
        init();
    }

    public MyCustopmTab(String label) {
        super(label);
        init();
    }

    public MyCustopmTab(Component... components) {
        super(components);
        init();
    }

    private void init() {
        add(new TextArea(" Hello there"));
    }
}
