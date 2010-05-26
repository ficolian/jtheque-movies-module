package org.jtheque.movies.views.impl.panel;

import org.jtheque.errors.able.IError;
import org.jtheque.errors.able.IErrorService;
import org.jtheque.movies.controllers.able.ICleanController;
import org.jtheque.movies.controllers.able.IImageController;
import org.jtheque.movies.controllers.able.IMovieController;
import org.jtheque.movies.persistence.od.able.Movie;
import org.jtheque.movies.services.able.IFFMpegService;
import org.jtheque.movies.utils.PreciseDuration;
import org.jtheque.movies.utils.Resolution;
import org.jtheque.movies.views.able.ICategoriesView;
import org.jtheque.movies.views.able.IMovieView;
import org.jtheque.movies.views.impl.actions.clean.CleanMovieAction;
import org.jtheque.movies.views.impl.actions.movies.GetInformationsAction;
import org.jtheque.movies.views.impl.actions.movies.SaveMovieAction;
import org.jtheque.movies.views.impl.actions.movies.image.EditImageAction;
import org.jtheque.movies.views.impl.fb.IMovieFormBean;
import org.jtheque.movies.views.impl.fb.MovieFormBean;
import org.jtheque.persistence.able.IDaoNotes;
import org.jtheque.persistence.impl.DaoNotes;
import org.jtheque.primary.utils.views.actions.CancelPrincipalAction;
import org.jtheque.primary.utils.views.NotesComboBoxModel;
import org.jtheque.primary.utils.views.NoteComboRenderer;
import org.jtheque.ui.utils.builders.I18nPanelBuilder;
import org.jtheque.ui.utils.builders.PanelBuilder;
import org.jtheque.ui.utils.components.Borders;
import org.jtheque.ui.utils.constraints.ConstraintManager;
import org.jtheque.ui.utils.filthy.FilthyFileChooserPanel;
import org.jtheque.ui.utils.filthy.FilthyFormattedTextField;
import org.jtheque.utils.ui.GridBagUtils;
import org.jtheque.ui.utils.filthy.FilthyTextField;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.UIManager;
import javax.swing.text.MaskFormatter;
import java.awt.Color;
import java.awt.Insets;
import java.text.ParseException;
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
 * The panel to edit a movie.
 *
 * @author Baptiste Wicht
 */
public final class EditMoviePanel extends MoviePanel {
    private FilthyTextField fieldTitle;
    private FilthyFormattedTextField fieldDuration;
    private FilthyFormattedTextField fieldResolution;
    private FilthyFileChooserPanel fieldFile;

    private NotesComboBoxModel modelNotes;

    private static final int FIELD_COLUMNS = 25;

    @Resource
    private ICategoriesView categoriesView;

    /**
     * Construct a new EditMoviePanel.
     */
    public EditMoviePanel() {
        super(IMovieView.EDIT_VIEW);
    }

	@Override
	protected void buildView(I18nPanelBuilder builder) {
        setOpaque(false);

		setBorder(Borders.createEmptyBorder(0, 0, 0, 3));

        addTitleField(builder);
        addFileField(builder);
        addInformationsField(builder);
        addNoteField(builder);

        builder.add(categoriesView.getImpl(), builder.gbcSet(0, 5, GridBagUtils.BOTH, GridBagUtils.ABOVE_BASELINE_LEADING, 0, -1, 1.0, 1.0));

        builder.setDefaultInsets(new Insets(2, 5, 2, 3));

        PanelBuilder buttons = builder.addPanel(builder.gbcSet(0, 6, GridBagUtils.HORIZONTAL, GridBagUtils.FIRST_LINE_START, 0, 0, 1.0, 0.0));

        buttons.addButton(new SaveMovieAction(getBean(IMovieController.class)),
		        buttons.gbcSet(0, 0, GridBagUtils.NONE, GridBagUtils.BASELINE_TRAILING, 1.0, 1.0));
        buttons.addButton(new CancelPrincipalAction("movie.actions.cancel", getBean(IMovieController.class)),
                buttons.gbcSet(1, 0, GridBagUtils.NONE, GridBagUtils.BASELINE_LEADING));
	}

	/**
     * Add the field for the title.
     *
     * @param builder The builder of the view.
     */
    private void addTitleField(I18nPanelBuilder builder) {
        builder.addI18nLabel(Movie.TITLE, builder.gbcSet(0, 0));

        fieldTitle = builder.add(new FilthyTextField(FIELD_COLUMNS), builder.gbcSet(1, 0, GridBagUtils.HORIZONTAL, GridBagUtils.BASELINE_LEADING, 2, 1, 1.0, 0.0));
        ConstraintManager.configure(fieldTitle.getField(), Movie.TITLE);

        builder.addButton(new CleanMovieAction(getBean(IMovieController.class), getBean(ICleanController.class)), builder.gbcSet(3, 0));
        builder.addButton(new EditImageAction(getBean(IImageController.class)), builder.gbcSet(4, 0, GridBagUtils.NONE, GridBagUtils.BASELINE_LEADING, 0, 1));
    }

    /**
     * Add the field for the file.
     *
     * @param builder The builder of the view.
     */
    private void addFileField(I18nPanelBuilder builder) {
        builder.addI18nLabel(Movie.FILE, builder.gbcSet(0, 1));

        fieldFile = builder.add(new FilthyFileChooserPanel(false), builder.gbcSet(1, 1, GridBagUtils.HORIZONTAL, GridBagUtils.BASELINE_LEADING, 0, 1));
        fieldFile.setFilesOnly();
        ConstraintManager.configure(fieldFile.getTextField(), Movie.FILE);
    }

    /**
     * Add the informations fields.
     *
     * @param parent The builder to add the fields to.
     */
    private void addInformationsField(I18nPanelBuilder parent) {
        parent.addI18nLabel(Movie.DURATION, parent.gbcSet(0, 2));
        parent.addI18nLabel(Movie.RESOLUTION, parent.gbcSet(0, 3));

        try {
            fieldDuration = new FilthyFormattedTextField(new MaskFormatter("##:##:##.###"));
            fieldDuration.setText("00:00:00:000");
            fieldDuration.getField().setColumns(10);
            parent.add(fieldDuration, parent.gbcSet(1, 2));
        } catch (ParseException e) {
            LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
        }

        try {
            fieldResolution = new FilthyFormattedTextField(new MaskFormatter("####x####"));
            fieldResolution.setText("0000x0000");
            fieldResolution.getField().setColumns(10);
            parent.add(fieldResolution, parent.gbcSet(1, 3));
        } catch (ParseException e) {
            LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
        }

        PanelBuilder builder =
                parent.addPanel(parent.gbcSet(2, 2, GridBagUtils.HORIZONTAL, GridBagUtils.LINE_START, 2, 2, 1.0, 0.0));

        builder.getPanel().setBackground(Color.blue);

        builder.addButton(new GetInformationsAction(this, getBean(IFFMpegService.class), getService(IErrorService.class)),
                builder.gbcSet(0, 0, GridBagUtils.NONE, GridBagUtils.LINE_START, 1.0, 1.0));
    }

    /**
     * Add the field for the note.
     *
     * @param builder The builder of the view.
     */
    private void addNoteField(I18nPanelBuilder builder) {
        builder.addI18nLabel(Movie.NOTE, builder.gbcSet(0, 4));

	    IDaoNotes daoNotes = getService(IDaoNotes.class);

        modelNotes = new NotesComboBoxModel(daoNotes);

        JComboBox box = new JComboBox(modelNotes);
        box.setRenderer(new NoteComboRenderer(daoNotes));

        box.setOpaque(false);
        box.setBackground(INPUT_COLOR);

        UIManager.put("ComboBox.selectionBackground", Color.black);

        for (int i = 0; i < box.getComponentCount(); i++) {
            if (box.getComponent(i) instanceof AbstractButton) {
                ((AbstractButton) box.getComponent(i)).setBorderPainted(false);
            }
        }

        builder.add(box, builder.gbcSet(1, 4, GridBagUtils.NONE, GridBagUtils.BASELINE_LEADING, 2, 1));
    }

    @Override
    public void setMovie(Movie movie) {
        fieldTitle.setText(movie.getTitle());
        fieldFile.setFilePath(movie.getFile());
        fieldDuration.setText(movie.getDuration() == null ? "00:00:00.000" : movie.getDuration().toString());
        fieldResolution.setText(movie.getResolution() == null ? "000x000" : movie.getResolution().toString());
        modelNotes.setSelectedItem(movie.getNote());

        categoriesView.reload(movie);
    }

    @Override
    public void validate(Collection<IError> errors) {
        ConstraintManager.validate(Movie.TITLE, fieldTitle.getText(), errors);
        ConstraintManager.validate(Movie.FILE, fieldFile.getFilePath(), errors);

        categoriesView.validate(errors);
    }

    @Override
    public IMovieFormBean fillMovieFormBean() {
        IMovieFormBean fb = new MovieFormBean();

        fb.setTitle(fieldTitle.getText());
        fb.setFile(fieldFile.getFilePath());
        fb.setResolution(new Resolution(fieldResolution.getText()));
        fb.setDuration(new PreciseDuration(fieldDuration.getText()));

        if (modelNotes.getSelectedNote() != null) {
            fb.setNote(modelNotes.getSelectedNote());
        } else {
	        fb.setNote(getService(IDaoNotes.class).getNote(DaoNotes.NoteType.UNDEFINED));
        }

        categoriesView.fillFilm(fb);

        return fb;
    }

    /**
     * Return the entered file path.
     *
     * @return The entered file path.
     */
    public String getFilePath() {
        return fieldFile.getFilePath();
    }

    /**
     * Set the resolution.
     *
     * @param resolution The resolution to set.
     */
    public void setResolution(Resolution resolution) {
        if(resolution != null){
            fieldResolution.setText(resolution.toString());
        }
    }

    /**
     * Set the duration.
     *
     * @param duration The duration to set.
     */
    public void setDuration(PreciseDuration duration) {
        if(duration != null){
            fieldDuration.setText(duration.toString());
        }
    }
}