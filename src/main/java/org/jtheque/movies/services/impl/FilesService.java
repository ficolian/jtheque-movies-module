package org.jtheque.movies.services.impl;

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
import org.jtheque.core.managers.error.InternationalizedError;
import org.jtheque.core.managers.view.able.IViewManager;
import org.jtheque.core.utils.CoreUtils;
import org.jtheque.core.utils.db.DaoNotes;
import org.jtheque.movies.IMovieConfiguration;
import org.jtheque.movies.IMoviesModule;
import org.jtheque.movies.persistence.od.able.Category;
import org.jtheque.movies.persistence.od.able.Movie;
import org.jtheque.movies.services.able.ICategoriesService;
import org.jtheque.movies.services.able.IFilesService;
import org.jtheque.movies.services.able.IMoviesService;
import org.jtheque.movies.services.impl.parsers.FileParser;
import org.jtheque.movies.utils.PreciseDuration;
import org.jtheque.movies.utils.Resolution;
import org.jtheque.utils.StringUtils;
import org.jtheque.utils.collections.CollectionUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * A files service implementation.
 *
 * @author Baptiste Wicht
 */
public final class FilesService implements IFilesService {
    @Resource
    private IMoviesService moviesService;

    @Resource
    private ICategoriesService categoriesService;

    @Resource
    private IMoviesModule moviesModule;
    private static final Pattern PATTERN = Pattern.compile(", ");

    @Override
    public void importMovies(Collection<File> files, Collection<FileParser> parsers){
        assert !files.isEmpty() : "Files cannot be empty";

		boolean fileNotCreated = false;

        for (File f : files){
			if (moviesService.fileExists(f.getAbsolutePath())){
				fileNotCreated = true;
			} else {
				createMovie(f.getAbsolutePath(), parsers);
			}
        }

		if(fileNotCreated){
			Managers.getManager(IViewManager.class).displayError(new InternationalizedError("movie.errors.filenotcreated"));
		}

    }

    @Override
    public Movie createMovie(String filePath, Collection<FileParser> parsers){
        Movie movie = moviesService.getEmptyMovie();

        movie.setNote(DaoNotes.getInstance().getNote(DaoNotes.NoteType.UNDEFINED));
        movie.setFile(filePath);

		File file = new File(filePath);

		movie.setResolution(getResolution(file));
		movie.setDuration(getDuration(file));

        extractCategoriesAndTitle(filePath, parsers, movie);

        moviesService.create(movie);

        return movie;
    }

    /**
     * Extract the categories and the title from the file.
     *
     * @param filePath The path to the file.
     * @param parsers  The parsers to use.
     * @param movie    The movie to fill.
     */
    private void extractCategoriesAndTitle(String filePath, Iterable<FileParser> parsers, Movie movie){
        File file = new File(filePath);

        String title = file.getName();

        Collection<Category> categories = new ArrayList<Category>(5);

        for (FileParser parser : parsers){
            parser.parseFilePath(file);
            categories.addAll(parser.getExtractedCategories());
            title = parser.clearFileName(title);
        }

        movie.addCategories(categories);
        movie.setTitle(title);

        createUnsavedCategories(categories);
    }

    /**
     * Create all the unsaved categories of the collection.
     *
     * @param categories A collection of categories.
     */
    private void createUnsavedCategories(Iterable<Category> categories){
        for (Category category : categories){
            if (!category.isSaved()){
                categoriesService.create(category);
            }
        }
    }

    @Override
    public Collection<File> getMovieFiles(File folder){
        if (folder.isDirectory()){
            Collection<File> files = new ArrayList<File>(50);

            readFolder(folder, files);

            return files;
        } else {
            return CollectionUtils.emptyList();
        }
    }

    /**
     * Read the folder and all the files of the folder in the collection.
     *
     * @param folder The folder to read.
     * @param files  The collection to add the files to.
     */
    private static void readFolder(File folder, Collection<File> files){
        for (File file : folder.listFiles(new MovieFileNameFilter())){
            if (file.isDirectory()){
                readFolder(file, files);
            } else {
                files.add(file);
            }
        }
    }

    @Override
    public Resolution getResolution(File f){
        IMovieConfiguration config = moviesModule.getConfig();

        if(ffmpegIsInstalled()){
            Scanner scanner = getInformations(f, config);

            if(scanner != null){
                while(scanner.hasNextLine()){
                    String line = scanner.nextLine().trim();

                    if(line.startsWith("Stream #0.0: Video:")){
                        String resolution = PATTERN.split(line)[2].trim();

						if(resolution.contains(" ")){
							resolution = resolution.substring(0, resolution.indexOf(' '));
						}

                        return new Resolution(resolution);
                    }
                }
            }
        }

        return null;
    }

    @Override
    public PreciseDuration getDuration(File f){
        IMovieConfiguration config = moviesModule.getConfig();

        if(ffmpegIsInstalled()){
            Scanner scanner = getInformations(f, config);

            if(scanner != null){
                while(scanner.hasNextLine()){
                    String line = scanner.nextLine().trim();

                    if(line.startsWith("Duration:")){
                        String duration = line.substring(10, line.indexOf(',')) + "00";

                        return new PreciseDuration(duration);
                    }
                }
            }
        }

        return null;
    }

    private boolean ffmpegIsInstalled() {
        IMovieConfiguration config = moviesModule.getConfig();

        boolean notInstalled = StringUtils.isEmpty(config.getFFmpegLocation()) || !new File(config.getFFmpegLocation()).exists();

        if(notInstalled){
            Managers.getManager(IViewManager.class).displayError(new InternationalizedError("movie.errors.ffmpeg"));
        }

        return !notInstalled;
    }

    private Scanner getInformations(File f, IMovieConfiguration config) {
        SimpleApplicationConsumer p = new SimpleApplicationConsumer(config.getFFmpegLocation(), "-i", f.getAbsolutePath());

        try {
            p.consume();

            return new Scanner(p.getResult());
        } catch (IOException e) {
            CoreUtils.getLogger(getClass()).error(e);
        }

        return null;
    }
}