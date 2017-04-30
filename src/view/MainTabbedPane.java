package view;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Created by Кумпутер on 14.03.2017.
 */
public class MainTabbedPane extends JTabbedPane {

    public MainTabbedPane(List<Component> components, List<String> names) {
        super();
        if (components.size() != names.size()) {
            return;
        }
        for (int i = 0; i < components.size(); i++) {
            addTab(names.get(i), components.get(i));
        }
    }
}
