package org.jtheque.movies.controllers.able;

import org.jtheque.core.managers.view.able.controller.Controller;
import org.jtheque.movies.persistence.od.able.Category;
import org.jtheque.movies.persistence.od.able.Movie;
import org.jtheque.movies.views.able.ICleanView;

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
 * A clean controller specification.
 *
 * @author Baptiste Wicht
 */
public interface ICleanController extends Controller {
    @Override
    ICleanView getView();

    /**
     * Clean the specified movie.
     *
     * @param movie The movie to clean.
     */
    void clean(Movie movie);

    /**
     * Clean the category.
     *
     * @param category The category to clean.
     */
    void clean(Category category);

    /**
     * Clean the current content.
     */
	void clean();
}