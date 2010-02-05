package org.jtheque.movies.views.impl.panel.players;

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

import org.jtheque.movies.views.impl.actions.view.QuitPlayerViewAction;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

/**
 * A abstract viewer panel. This class construct the base of a frame and let the
 * extended class to build the rest of the view.
 *
 * @author Baptiste Wicht
 */
public final class ViewerPanel extends JPanel {
    private final JLabel labelFile;

    private final IMoviePlayer player;

    /**
     * Construct a new ViewerPanel.
     *
     * @param player The movie player.
     */
    public ViewerPanel(IMoviePlayer player) {
        super();

        this.player = player;

        setLayout(new BorderLayout());

        GridBagConstraints cons = new GridBagConstraints();
        Container playerFilePanel = new JPanel(new GridBagLayout());

        JComponent playerFileLabel = new JLabel("File: ");
        playerFileLabel.setOpaque(false);
        cons.gridx = 0;
        cons.gridy = 0;
        cons.insets = new Insets(2, 2, 2, 0);
        cons.fill = GridBagConstraints.HORIZONTAL;
        playerFilePanel.add(playerFileLabel, cons);

        labelFile = new JLabel();
        cons.gridx++;
        cons.weightx = 1;

        playerFilePanel.add(labelFile, cons);

        cons.gridx++;

        playerFilePanel.add(new JButton(new QuitPlayerViewAction()), cons);

        add(playerFilePanel, BorderLayout.NORTH);

        add(player.getComponent(), BorderLayout.CENTER);
    }

    /**
     * Set the file to read in the viewer.
     *
     * @param file The file to open.
     */
    public void setFile(File file) {
        labelFile.setText(file.getAbsolutePath());
        player.load(file);
    }

    /**
     * Stop the movie.
     */
    public void stop() {
        player.stop();
    }
}