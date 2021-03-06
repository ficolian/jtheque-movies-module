package org.jtheque.movies.views.impl.panel;

import org.jtheque.errors.Error;
import org.jtheque.i18n.LanguageService;
import org.jtheque.images.ImageService;
import org.jtheque.movies.IMoviesModule;
import org.jtheque.movies.MoviesResources;
import org.jtheque.movies.persistence.od.able.Category;
import org.jtheque.movies.persistence.od.able.Movie;
import org.jtheque.movies.views.able.IMovieView;
import org.jtheque.movies.views.impl.fb.IMovieFormBean;
import org.jtheque.persistence.DaoNotes;
import org.jtheque.ui.Controller;
import org.jtheque.ui.components.Borders;
import org.jtheque.ui.components.Components;
import org.jtheque.ui.components.I18nLabel;
import org.jtheque.ui.utils.actions.ActionFactory;
import org.jtheque.ui.utils.actions.JThequeAction;
import org.jtheque.ui.utils.builders.I18nPanelBuilder;
import org.jtheque.ui.utils.builders.PanelBuilder;
import org.jtheque.ui.utils.models.SimpleListModel;
import org.jtheque.utils.StringUtils;
import org.jtheque.utils.ui.GridBagUtils;

import org.jdesktop.swingx.JXImagePanel;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.ListCellRenderer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.util.Collection;

import static org.jtheque.ui.components.filthy.FilthyConstants.TITLE_FONT;

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
 * A panel to view the movie.
 *
 * @author Baptiste Wicht
 */
public final class ViewMoviePanel extends MoviePanel {
    private static final float TITLE_FONT_SIZE = 18.0f;
    private static final float BUTTON_FONT_SIZE = 14.0f;

    private JLabel titleLabel;
    private JLabel labelFile;

    private I18nLabel labelDate;
    private I18nLabel labelSize;
    private I18nLabel labelDuration;
    private I18nLabel labelResolution;

    private JXImagePanel notePanel;
    private JXImagePanel imagePanel;

    private SimpleListModel<Category> categoriesModel;

    /**
     * Construct a new ViewMoviePanel.
     */
    public ViewMoviePanel() {
        super(IMovieView.VIEW_VIEW);
    }

    @Override
    protected void buildView(I18nPanelBuilder builder) {
        @SuppressWarnings("unchecked") //Safe because of the Spring Container
                Controller<IMovieView> movieController = getBean("movieController", Controller.class);

        setBorder(Borders.createEmptyBorder(0, 0, 0, 3));

        PanelBuilder title = builder.addPanel(builder.gbcSet(0, 0, GridBagUtils.HORIZONTAL, GridBagUtils.FIRST_LINE_START, 0, 1, 1.0, 0.0));

        titleLabel = title.add(Components.newI18nLabel("", TITLE_FONT.deriveFont(TITLE_FONT_SIZE), Color.white),
                builder.gbcSet(0, 0, GridBagUtils.HORIZONTAL, GridBagUtils.FIRST_LINE_START, 1.0, 0.0));

        JThequeAction action = ActionFactory.createAction("movie.actions.view", movieController);
        action.setIcon(getService(ImageService.class).getIcon(MoviesResources.PLAY_ICON));

        JButton button = title.addButton(action,
                builder.gbcSet(1, 0, GridBagUtils.NONE, GridBagUtils.BASELINE_TRAILING, 0, 1, 1.0, 0.0));
        button.setFont(button.getFont().deriveFont(Font.BOLD).deriveFont(BUTTON_FONT_SIZE));

        builder.setDefaultInsets(new Insets(2, 3, 2, 3));

        PanelBuilder buttons = builder.addPanel(builder.gbcSet(0, 1, GridBagUtils.NONE, GridBagUtils.BASELINE_LEADING, 0, 1));

        buttons.addButton(ActionFactory.createAction("movie.actions.edit", movieController), buttons.gbcSet(0, 0));
        buttons.addButton(ActionFactory.createAction("movie.actions.delete", movieController), buttons.gbcSet(1, 0));

        addFileField(builder);
        addNoteField(builder);

        PanelBuilder center = builder.addPanel(builder.gbcSet(0, 4, GridBagUtils.BOTH, GridBagUtils.ABOVE_BASELINE_LEADING, 0, 1, 1.0, 1.0));

        addCategoriesView(center);
        addImagePanel(center);

        addFileInformations(builder);
    }

    /**
     * Add the field for the file.
     *
     * @param builder The builder of the panel.
     */
    private void addFileField(I18nPanelBuilder builder) {
        builder.addI18nLabel("movie.file", Font.BOLD, builder.gbcSet(0, 2, GridBagUtils.NONE, GridBagUtils.BASELINE_LEADING, -1, 1));

        labelFile = builder.addLabel("", builder.gbcSet(1, 2, GridBagUtils.HORIZONTAL, GridBagUtils.BASELINE_LEADING, 0, 1));
    }

    /**
     * Add the field for the note.
     *
     * @param builder The builder of the panel.
     */
    private void addNoteField(I18nPanelBuilder builder) {
        builder.addI18nLabel("movie.note", Font.BOLD, builder.gbcSet(0, 3, GridBagUtils.NONE, GridBagUtils.BASELINE_LEADING, -1, 1));

        notePanel = builder.add(new JXImagePanel(), builder.gbcSet(1, 3, GridBagUtils.NONE, GridBagUtils.BASELINE_LEADING, 0, 1));
        notePanel.setOpaque(false);
    }

    /**
     * Add the categories view.
     *
     * @param builder The builder of the panel.
     */
    private void addCategoriesView(PanelBuilder builder) {
        categoriesModel = new SimpleListModel<Category>();

        ListCellRenderer renderer = Components.newIconListRenderer(getService(ImageService.class).getIcon(MoviesResources.BOX_ICON));

        builder.addScrolledList(categoriesModel, renderer, builder.gbcSet(0, 0, GridBagUtils.BOTH, GridBagUtils.ABOVE_BASELINE_LEADING, -1, 1, 1.0, 1.0));
    }

    /**
     * Add the image panel to the view.
     *
     * @param builder The panel builder.
     */
    private void addImagePanel(PanelBuilder builder) {
        imagePanel = builder.add(new JXImagePanel(), builder.gbcSet(1, 0));
        imagePanel.setOpaque(false);
    }

    /**
     * Add the file informations to the panel.
     *
     * @param builder The builder of the panel.
     */
    private void addFileInformations(I18nPanelBuilder builder) {
        labelDate = builder.addI18nLabel("", builder.gbcSet(0, 5, GridBagUtils.NONE, GridBagUtils.BASELINE_LEADING, 0, 1));
        labelSize = builder.addI18nLabel("", builder.gbcSet(0, 6, GridBagUtils.NONE, GridBagUtils.BASELINE_LEADING, 0, 1));
        labelDuration = builder.addI18nLabel("", builder.gbcSet(0, 7, GridBagUtils.NONE, GridBagUtils.BASELINE_LEADING, 0, 1));
        labelResolution = builder.addI18nLabel("", builder.gbcSet(0, 8, GridBagUtils.NONE, GridBagUtils.BASELINE_LEADING, 0, 1));
    }

    @Override
    public void setMovie(Movie movie) {
        titleLabel.setText(getService(LanguageService.class).getMessage("movie.view.movie.title", movie.getDisplayableText()));
        labelFile.setText(movie.getFile());
        notePanel.setImage(getService(DaoNotes.class).getImage(movie.getNote()));

        labelDate.setTextKey("movie.view.file.date", movie.getFileLastModifiedDate());
        labelSize.setTextKey("movie.view.file.size", movie.getFileSize());
        labelDuration.setTextKey("movie.view.file.duration", movie.getDuration());
        labelResolution.setTextKey("movie.view.file.resolution", movie.getResolution());

        if (StringUtils.isNotEmpty(movie.getImage())) {
            imagePanel.setImage(getService(ImageService.class).getImageFromFile(
                    getBean(IMoviesModule.class).getThumbnailFolderPath() + movie.getImage()));
        } else {
            imagePanel.setImage(null);
        }

        categoriesModel.setElements(movie.getCategories());
    }

    @Override
    public void validate(Collection<Error> errors) {
        //Nothing to validate
    }

    @Override
    public IMovieFormBean fillMovieFormBean() {
        return null;
    }
}
