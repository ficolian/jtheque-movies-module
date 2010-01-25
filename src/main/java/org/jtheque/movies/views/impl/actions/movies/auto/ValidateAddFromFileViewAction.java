package org.jtheque.movies.views.impl.actions.movies.auto;

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

import org.jtheque.core.managers.Managers;
import org.jtheque.core.managers.beans.IBeansManager;
import org.jtheque.core.managers.error.InternationalizedError;
import org.jtheque.core.managers.view.able.IViewManager;
import org.jtheque.core.managers.view.impl.actions.JThequeAction;
import org.jtheque.movies.controllers.able.IMovieController;
import org.jtheque.movies.persistence.od.able.Movie;
import org.jtheque.movies.services.able.IFilesService;
import org.jtheque.movies.services.able.IMoviesService;
import org.jtheque.movies.views.able.IAddFromFileView;

import javax.annotation.Resource;
import java.awt.event.ActionEvent;

/**
 * An action to add a movie from a file.
 *
 * @author Baptiste Wicht
 */
public final class ValidateAddFromFileViewAction extends JThequeAction {
    @Resource
    private IAddFromFileView addFromFileView;

    @Resource
    private IFilesService filesService;

    @Resource
    private IMovieController movieController;

    @Resource
    private IMoviesService moviesService;

    /**
     * Construct a new ValidateAddFromFileViewAction.
     */
    public ValidateAddFromFileViewAction(){
        super("movie.auto.actions.add");

        Managers.getManager(IBeansManager.class).inject(this);
    }

    @Override
    public void actionPerformed(ActionEvent e){
		if(addFromFileView.validateContent()){
			if(moviesService.fileExists(addFromFileView.getFilePath())){
				Managers.getManager(IViewManager.class).displayError(new InternationalizedError("movie.errors.existingfile"));
			} else {
				Movie movie = filesService.createMovie(addFromFileView.getFilePath(), addFromFileView.getSelectedParsers());

				movieController.view(movie);

				addFromFileView.closeDown();
			}
		}
    }
}