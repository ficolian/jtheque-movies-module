package org.jtheque.movies.controllers.impl.states;

import org.jtheque.movies.controllers.able.IMovieController;
import org.jtheque.primary.utils.controller.AbstractControllerState;

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

/**
 * A Movie controller state.
 *
 * @author Baptiste Wicht
 */
public abstract class MovieState extends AbstractControllerState {
    private IMovieController controller;

    /**
     * Return the movie controller of the state.
     *
     * @return The movie controller of the state.
     */
    IMovieController getController() {
        return controller;
    }

    /**
     * Set the controller of the state.
     *
     * @param controller The movie controller. 
     */
    public void setController(IMovieController controller) {
        this.controller = controller;
    }
}