package org.jtheque.movies.views.impl.panel.players;

/*
 * Copyright JTheque (Baptiste Wicht)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import javax.swing.JComponent;

import java.io.File;

import chrriis.dj.nativeswing.swtimpl.components.JVLCPlayer;

/**
 * A VLC player implementation.
 *
 * @author Baptiste Wicht
 */
public final class VLCPlayer implements IMoviePlayer {
    private final JVLCPlayer player;

    /**
     * Construct a new VLCPlayer.
     */
    public VLCPlayer() {
        super();

        player = new JVLCPlayer();
        player.setControlBarVisible(true);
    }

    @Override
    public void stop() {
        player.getVLCPlaylist().stop();
    }

    @Override
    public void load(File f) {
        player.load(f.getAbsolutePath());
    }

    @Override
    public JComponent getComponent() {
        return player;
    }
}