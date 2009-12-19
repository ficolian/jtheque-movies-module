package org.jtheque.movies.views.impl.choiceActions;

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

import org.jtheque.movies.persistence.od.able.Category;
import org.jtheque.movies.services.able.ICategoriesService;
import org.jtheque.movies.services.able.IMoviesService;
import org.jtheque.movies.views.able.ICleanMovieView;
import org.jtheque.primary.view.impl.choice.AbstractChoiceAction;

import javax.annotation.Resource;

/**
 * An action to modify the selected item.
 *
 * @author Baptiste Wicht
 */
public final class CleanChoiceAction extends AbstractChoiceAction {
    @Resource
    private ICleanMovieView cleanMovieView;
    
    @Resource
    private IMoviesService moviesService;
    
    @Override
    public boolean canDoAction(String action) {
        return "clean".equals(action);
    }

    @Override
    public void execute() {
        if (ICategoriesService.DATA_TYPE.equals(getContent())) {
            Category cat = (Category)getSelectedItem();
            
            cleanMovieView.clean(moviesService.getMoviesOf(cat));
        }
    }
}