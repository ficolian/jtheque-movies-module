package org.jtheque.movies.views.impl.actions.movies;

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

import org.jtheque.core.managers.view.impl.actions.JThequeAction;
import org.jtheque.movies.controllers.able.IMovieController;

import javax.annotation.Resource;
import java.awt.event.ActionEvent;

/**
 * Action to manually edit a film.
 *
 * @author Baptiste Wicht
 */
public final class AcManualEdit extends JThequeAction {
    private static final long serialVersionUID = 1506516842802832928L;

    @Resource
    private IMovieController movieController;

    /**
     * Construct a new AcManualEdit.
     */
    public AcManualEdit(){
        super("movie.actions.edit");
    }

    @Override
    public void actionPerformed(ActionEvent e){
        movieController.manualEdit();
    }
}