package org.jtheque.movies.views.impl.panel;

import org.jtheque.core.managers.Managers;
import org.jtheque.core.managers.resource.IResourceManager;
import org.jtheque.core.utils.ui.Borders;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/*
 * This file is part of JTheque.
 *
 * JTheque is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * JTheque is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JTheque.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * A filthy renderer for this combo box.
 *
 * @author Baptiste Wicht
 */
public final class FilthyRenderer extends DefaultListCellRenderer {
    private final Color background;
    private final Color selectionBackground = new Color(170, 170, 170);
    private final Border border;

    /**
     * Construct a new FilthyRenderer.
     */
    public FilthyRenderer() {
        super();

        background = Managers.getManager(IResourceManager.class).getColor("filthyInputColor");
        border = Borders.createEmptyBorder(0, 0, 0, 0);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        setBorder(border);
        setForeground(Color.white);
        setOpaque(true);

        if (isSelected) {
            setBackground(selectionBackground);
        } else {
            setBackground(background);
        }

        return this;
    }
}