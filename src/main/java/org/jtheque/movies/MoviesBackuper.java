package org.jtheque.movies;

import org.jtheque.collections.DaoCollections;
import org.jtheque.file.ModuleBackup;
import org.jtheque.file.ModuleBackuper;
import org.jtheque.movies.persistence.dao.able.IDaoCategories;
import org.jtheque.movies.persistence.dao.able.IDaoMovies;
import org.jtheque.movies.persistence.od.able.Category;
import org.jtheque.movies.persistence.od.able.Movie;
import org.jtheque.movies.utils.PreciseDuration;
import org.jtheque.movies.utils.Resolution;
import org.jtheque.persistence.DaoNotes;
import org.jtheque.persistence.Note;
import org.jtheque.utils.StringUtils;
import org.jtheque.utils.bean.Version;
import org.jtheque.xml.utils.Node;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Collection;

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
 * A backuper for the movies. It backups the movies and the categories.
 *
 * @author Baptiste Wicht
 */
public class MoviesBackuper implements ModuleBackuper {
    private static final String[] DEPENDENCIES = {"jtheque-primary-backuper"};

    @Resource
    private IDaoCategories daoCategories;

    @Resource
    private IDaoMovies daoMovies;

    @Resource
    private DaoCollections daoCollections;

    @Resource
    private DaoNotes daoNotes;

    @Override
    public String getId() {
        return "jtheque-primary-backuper";
    }

    @Override
    public String[] getDependencies() {
        return DEPENDENCIES;
    }

    @Override
    public ModuleBackup backup() {
        Collection<Node> nodes = new ArrayList<Node>(100);

        addCategories(nodes);
        addMovies(nodes);

        return new ModuleBackup(Version.get("1.0"), getId(), nodes);
    }

    /**
     * Add the categories to the nodes.
     *
     * @param nodes The nodes collection to fill.
     */
    private void addCategories(Collection<Node> nodes) {
        for (Category category : daoCategories.getAll()) {
            Node node = new Node("category");

            node.addSimpleChildValue("id", category.getId());
            node.addSimpleChildValue("name", category.getTitle());
            node.addSimpleChildValue("parent", category.getParent() == null ? -1 : category.getParent().getId());
            node.addSimpleChildValue("collection", category.getTheCollection() == null ? -1 : category.getTheCollection().getId());

            nodes.add(node);
        }
    }

    /**
     * Add the movies to the nodes collection.
     *
     * @param nodes The nodes collections to fill.
     */
    private void addMovies(Collection<Node> nodes) {
        for (Movie movie : daoMovies.getAll()) {
            Node node = new Node("movie");

            node.addSimpleChildValue("id", movie.getId());
            node.addSimpleChildValue("title", movie.getTitle());
            node.addSimpleChildValue("file", movie.getFile());
            node.addSimpleChildValue("image", movie.getImage());
            node.addSimpleChildValue("note", movie.getNote().intValue());
            node.addSimpleChildValue("duration", movie.getDuration() == null ? 0 : movie.getDuration().getTime());
            node.addSimpleChildValue("resolution", movie.getResolution() == null ? "" : movie.getResolution().toString());
            node.addSimpleChildValue("collection", movie.getTheCollection() == null ? -1 : movie.getTheCollection().getId());

            for (Category category : movie.getCategories()) {
                node.addSimpleChildValue("category", category.getId());
            }

            nodes.add(node);
        }
    }

    @Override
    public void restore(ModuleBackup backup) {
        assert getId().equals(backup.getId()) : "This backuper can only restore its own backups";

        Collection<Node> nodes = backup.getNodes();

        restoreCategories(nodes);
        restoreMovies(nodes);
    }

    /**
     * Restore the categories.
     *
     * @param nodes The iterator to the nodes.
     */
    private void restoreCategories(Iterable<Node> nodes) {
        Collection<Category> categories = new ArrayList<Category>(25);

        for (Node node : nodes) {
            if ("category".equals(node.getName())) {
                Category category = daoCategories.create();

                category.getTemporaryContext().setId(node.getChildIntValue("id"));
                category.setTitle(node.getChildValue("name"));
                category.setTheCollection(daoCollections.getCollectionByTemporaryId(node.getChildIntValue("collection")));
                category.setTemporaryParent(node.getChildIntValue("parent"));

                daoCategories.save(category);

                categories.add(category);
            }
        }

        for (Category category : categories) {
            category.setParent(daoCategories.getCategoryByTemporaryId(category.getTemporaryParent()));

            daoCategories.save(category);
        }
    }

    /**
     * Restore the movies.
     *
     * @param nodes The iterator on the nodes.
     */
    private void restoreMovies(Iterable<Node> nodes) {
        for (Node node : nodes) {
            if ("movie".equals(node.getName())) {
                Movie movie = daoMovies.create();

                movie.setTitle(node.getChildValue("title"));
                movie.setFile(node.getChildValue("file"));
                movie.setImage(node.getChildValue("image"));
                movie.setNote(Note.fromIntValue(node.getChildIntValue("note")));

                movie.setTheCollection(daoCollections.getCollectionByTemporaryId(node.getChildIntValue("collection")));

                restoreDuration(node, movie);
                restoreResolution(node, movie);
                restoreCategories(node, movie);

                daoMovies.save(movie);
            }
        }
    }

    private static void restoreDuration(Node node, Movie movie) {
        long duration = node.getChildLongValue("duration");

        if (duration != 0L) {
            movie.setDuration(new PreciseDuration(duration));
        }
    }

    private static void restoreResolution(Node node, Movie movie) {
        String resolution = node.getChildValue("resolution");

        if (StringUtils.isNotEmpty(resolution)) {
            movie.setResolution(new Resolution(resolution));
        }
    }

    private void restoreCategories(Node node, Movie movie) {
        Collection<Category> categories = new ArrayList<Category>(5);

        for (Node catNode : node.getChildrens()) {
            if ("category".equals(catNode.getName())) {
                categories.add(daoCategories.getCategoryByTemporaryId(catNode.getInt()));
            }
        }

        movie.setCategories(categories);
    }
}