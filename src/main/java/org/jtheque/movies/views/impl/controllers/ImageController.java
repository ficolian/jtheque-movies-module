package org.jtheque.movies.views.impl.controllers;

import org.jtheque.errors.able.IErrorService;
import org.jtheque.errors.utils.Errors;
import org.jtheque.movies.persistence.od.able.Movie;
import org.jtheque.movies.services.able.IFFMpegService;
import org.jtheque.movies.services.able.IMoviesService;
import org.jtheque.movies.views.able.IImageView;
import org.jtheque.movies.views.able.IMovieView;
import org.jtheque.ui.able.IController;
import org.jtheque.ui.utils.AbstractController;
import org.jtheque.utils.StringUtils;

import javax.annotation.Resource;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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

public class ImageController extends AbstractController<IImageView> {
    @Resource
    private IMoviesService moviesService;

    @Resource
    private IErrorService errorService;

    @Resource
    private IFFMpegService ffmpegService;

    @Resource
    private IController<IMovieView> movieController;

    public ImageController() {
        super(IImageView.class);
    }

    @Override
    protected Map<String, String> getTranslations() {
        Map<String, String> translations = new HashMap<String, String>(3);

        translations.put("movie.image.actions.ffmpeg.random", "generateRandom");
        translations.put("movie.image.actions.ffmpeg.fixed", "generateFixed");
        translations.put("movie.image.actions.file", "generateFile");
        translations.put("movie.image.actions.validate", "save");
        translations.put("movie.image.actions.cancel", "close");
        translations.put("movie.actions.image", "edit");
        
        return translations;
    }

    private void close(){
        getView().closeDown();
    }

    private void edit(){
        getView().displayMovie(getCurrentMovie());
        getView().display();
    }

    public void generateRandom() {
        Movie movie = getCurrentMovie();

        File file = new File(movie.getFile());

        if (StringUtils.isNotEmpty(movie.getFile()) && file.exists()) {
            getView().setImage(ffmpegService.generateRandomPreviewImage(file));
        } else {
            displayFileNotFoundError();
        }
    }

    private void generateFixed(){
        Movie movie = getCurrentMovie();

        File file = new File(movie.getFile());

        if (StringUtils.isNotEmpty(movie.getFile()) && file.exists()) {
            getView().setImage(ffmpegService.generatePreviewImage(file, getView().getTime()));
        } else {
            displayFileNotFoundError();
        }
    }

    private void generateFile(){
        File file = new File(getView().getImagePath());

        if (StringUtils.isNotEmpty(getView().getImagePath()) && file.exists()) {
            getView().setImage(ffmpegService.generateImageFromUserInput(file));
        } else {
            displayFileNotFoundError();
        }
    }

    private void save(){
        moviesService.saveImage(getCurrentMovie(), getView().getImage());

        getView().closeDown();
    }

    /**
     * Return the current movie.
     *
     * @return The current movie.
     */
    private Movie getCurrentMovie() {
        return movieController.getView().getModel().getCurrentMovie();
    }

    /**
     * Display a file not found error.
     */
    private void displayFileNotFoundError() {
        errorService.addError(Errors.newI18nError("movie.errors.filenotfound"));
    }
}