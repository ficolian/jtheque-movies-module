package org.jtheque.movies.views.impl;

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

import org.jdesktop.swingx.JXTree;
import org.jtheque.core.managers.Managers;
import org.jtheque.core.managers.error.IErrorManager;
import org.jtheque.core.managers.error.JThequeError;
import org.jtheque.core.managers.log.IJThequeLogger;
import org.jtheque.core.managers.persistence.able.Entity;
import org.jtheque.core.managers.resource.IResourceManager;
import org.jtheque.core.managers.resource.ImageType;
import org.jtheque.core.managers.view.able.IView;
import org.jtheque.core.managers.view.impl.actions.utils.DisplayViewAction;
import org.jtheque.core.managers.view.impl.components.filthy.FilthyCardPanel;
import org.jtheque.core.managers.view.impl.components.panel.AbstractDelegatedView;
import org.jtheque.core.managers.view.impl.components.panel.AbstractPanelView;
import org.jtheque.core.managers.view.impl.components.panel.CardPanel;
import org.jtheque.core.utils.CoreUtils;
import org.jtheque.core.utils.ui.Borders;
import org.jtheque.core.utils.ui.FilthyPanelBuilder;
import org.jtheque.core.utils.ui.PanelBuilder;
import org.jtheque.movies.IMoviesModule;
import org.jtheque.movies.persistence.od.able.Movie;
import org.jtheque.movies.services.able.IMoviesService;
import org.jtheque.movies.views.able.IMovieView;
import org.jtheque.movies.views.able.models.IMoviesModel;
import org.jtheque.movies.views.impl.actions.movies.CollapseAction;
import org.jtheque.movies.views.impl.actions.movies.ExpandAction;
import org.jtheque.movies.views.impl.actions.movies.RefreshAction;
import org.jtheque.movies.views.impl.fb.IMovieFormBean;
import org.jtheque.movies.views.impl.models.FilthyCellRenderer;
import org.jtheque.movies.views.impl.models.MoviesModel;
import org.jtheque.movies.views.impl.panel.EditMoviePanel;
import org.jtheque.movies.views.impl.panel.MoviePanel;
import org.jtheque.movies.views.impl.panel.ViewMoviePanel;
import org.jtheque.primary.view.impl.actions.principal.CreateNewPrincipalAction;
import org.jtheque.primary.view.impl.listeners.CurrentObjectListener;
import org.jtheque.primary.view.impl.listeners.DisplayListListener;
import org.jtheque.primary.view.impl.listeners.ObjectChangedEvent;
import org.jtheque.primary.view.impl.models.tree.JThequeTreeModel;
import org.jtheque.primary.view.impl.sort.SortManager;
import org.jtheque.utils.ui.GridBagUtils;
import org.jtheque.utils.ui.ImageUtils;
import org.jtheque.utils.ui.SizeTracker;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.JLabel;
import javax.swing.event.TreeSelectionListener;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Panel to display movies.
 *
 * @author Baptiste Wicht
 */
public final class MovieView extends AbstractDelegatedView<MovieView.MovieViewImpl> implements CurrentObjectListener, IMovieView {
    private JXTree treeMovies;

    private CardPanel<MoviePanel> layeredPanel;

    private IMoviesModel model;
    private JThequeTreeModel treeModel;

    @Resource
    private Font filthyTitleFont;

    @Resource
    private LinearGradientPaint backgroundGradient;

    @PostConstruct
    public void init() {
		filthyTitleFont = filthyTitleFont.deriveFont(25f);

        model = new MoviesModel();

        buildInEDT();
    }

    @Override
    public IMoviesModel getModel() {
        return model;
    }

    @Override
    protected void buildDelegatedView() {
        MovieViewImpl impl = new MovieViewImpl();
        setDelegate(impl);
        impl.build();

        model.addCurrentObjectListener(this);
        model.addDisplayListListener(impl);
    }

    final class MovieViewImpl extends AbstractPanelView<IMoviesModel> implements DisplayListListener {
        private final SortManager SORTER = new SortManager();
        private final SizeTracker tracker = new SizeTracker(this);

        private Image gradientImage;
        private final BufferedImage light = Managers.getManager(IResourceManager.class).getImage(
                IMoviesModule.IMAGES_BASE_NAME, "light", ImageType.PNG);

        private static final double LIST_COLUMN = 0.3;

        /**
         * Build the view.
         */
        @PostConstruct
        private void build(){
            PanelBuilder builder = new FilthyPanelBuilder(this);

            buildPanelList(builder);
            buildPanelMovie(builder);

            treeMovies.addTreeSelectionListener(CoreUtils.<TreeSelectionListener>getBean("movieController"));

            selectFirst();
        }

        /**
         * Build the internal panel list.
         *
         * @param parent The parent builder.
         */
        private void buildPanelList(PanelBuilder parent){
            parent.setDefaultInsets(new Insets(2, 2, 2, 5));

            PanelBuilder builder = parent.addPanel(new BorderLayout(2,2),
					parent.gbcSet(0, 0, GridBagUtils.BOTH, GridBagUtils.FIRST_LINE_START, LIST_COLUMN, 1.0));

            parent.setDefaultInsets(new Insets(2, 2, 2, 2));

            builder.getPanel().setBorder(Borders.createEmptyBorder(10,10,10,20));
            builder.getPanel().setMinimumSize(new Dimension(165, 400));

			addTitle(builder);
			addTree(builder);
			addActions(builder);
        }

		private void addActions(PanelBuilder builder){
			PanelBuilder panelButtons = builder.addPanel(BorderLayout.SOUTH);

			panelButtons.addI18nLabel("movie.panel.list.new", Font.BOLD,
					builder.gbcSet(0, 0, GridBagUtils.NONE, GridBagUtils.BASELINE_LEADING, LIST_COLUMN, 0.0));

			DisplayViewAction autoAddAction = new DisplayViewAction("movie.auto.actions.add");
			autoAddAction.setView(CoreUtils.<IView>getBean("addFromFileView"));

			panelButtons.addButton(new CreateNewPrincipalAction("movie.actions.add", "movieController"),
					builder.gbcSet(0, 1, GridBagUtils.NONE, GridBagUtils.BASELINE_LEADING, LIST_COLUMN, 0.0));
			panelButtons.addButton(autoAddAction,
					builder.gbcSet(0, 2, GridBagUtils.NONE, GridBagUtils.BASELINE_LEADING, 1, 1, LIST_COLUMN, 0.0, 10, 0));
		}

		private void addTitle(PanelBuilder builder){
			PanelBuilder titleBuilder = builder.addPanel(BorderLayout.NORTH);

			JLabel label = titleBuilder.addI18nLabel("movie.panel.list.title",
					titleBuilder.gbcSet(0, 0, GridBagUtils.HORIZONTAL, GridBagUtils.BASELINE_LEADING, 1.0, 0.0));

			titleBuilder.addButton(new RefreshAction(), titleBuilder.gbcSet(1, 0));
			titleBuilder.addButton(new ExpandAction(), titleBuilder.gbcSet(2, 0));
			titleBuilder.addButton(new CollapseAction(), titleBuilder.gbcSet(3, 0));

			label.setFont(filthyTitleFont);
		}

		private void addTree(PanelBuilder builder){
			treeModel = new SortManager().createInitialModel(IMoviesService.DATA_TYPE);

			displayListChanged();

			treeMovies = builder.addScrolledTree(treeModel, new FilthyCellRenderer(), BorderLayout.CENTER);
		}

		/**
         * Build the internal panel film.
         *
         * @param parent The parent builder.
         */
        private void buildPanelMovie(PanelBuilder parent){
            layeredPanel = new FilthyCardPanel<MoviePanel>();

            MoviePanel viewPanel = new ViewMoviePanel();
            MoviePanel editPanel = new EditMoviePanel();

            layeredPanel.addLayer(viewPanel, viewPanel.getKey());
            layeredPanel.addLayer(editPanel, editPanel.getKey());

            setDisplayedView(VIEW_VIEW);

            parent.add(layeredPanel, parent.gbcSet(1, 0, GridBagUtils.BOTH, GridBagUtils.FIRST_LINE_START, 1 - LIST_COLUMN, 1.0));
        }

        @Override
        protected void paintComponent(Graphics g){
            if (!isVisible()){
                return;
            }

            Graphics2D g2 = (Graphics2D) g;

            if (gradientImage == null || tracker.hasSizeChanged()){
                gradientImage = ImageUtils.createCompatibleImage(getWidth(), getHeight());
                Graphics2D g2d = (Graphics2D) gradientImage.getGraphics();
                Composite composite = g2.getComposite();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.setPaint(backgroundGradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));

                g2d.drawImage(light, 0, 0, getWidth(), light.getHeight(), null);
                g2d.setComposite(composite);
                g2d.dispose();
            }

            g2.drawImage(gradientImage, 0, 0, null);

            tracker.updateSize();
        }

        @Override
        public void displayListChanged(){
            SORTER.sort(treeModel, "Movies", IMoviesService.SORT_BY_CATEGORY);
        }

        @Override
        public boolean validateContent() {
            Collection<JThequeError> errors = new ArrayList<JThequeError>(5);

            layeredPanel.getCurrentLayer().validate(errors);

            for (JThequeError error : errors) {
                Managers.getManager(IErrorManager.class).addError(error);
            }

            return errors.isEmpty();
        }
    }
	
    @Override
    public void objectChanged(ObjectChangedEvent event){
        layeredPanel.getCurrentLayer().setMovie((Movie) event.getObject());
    }

    @Override
    public IMovieFormBean fillMovieFormBean(){
        return layeredPanel.getCurrentLayer().fillMovieFormBean();
    }

    @Override
    public Iterable<Movie> getDisplayList(){
        return model.getDisplayList();
    }

    @Override
    public void setDisplayedView(String view){
        layeredPanel.displayLayer(view);
    }

    @Override
    public MoviePanel getCurrentView(){
        return layeredPanel.getCurrentLayer();
    }

	@Override
	public void select(Movie movie){
		IJThequeLogger logger = CoreUtils.getLogger(getClass());

		logger.debug("Select {}:{}", movie.getTitle(), movie.getId());

		for(int i = 0; i < treeModel.getChildCount(treeModel.getRoot()); i++){
			Object child = treeModel.getChild(treeModel.getRoot(), i);

			treeMovies.expandRow(i);

			logger.debug("Get child {}", child);
			logger.debug("Expand row {}", i);

			for(int j = 0; j < treeModel.getChildCount(child); j++){
				Entity m = (Entity) treeModel.getChild(child, j);

				logger.debug("Get movie {}:{}", m, m.getId());

				if(m.getId() == movie.getId()){
					treeMovies.setSelectionRow(i + j + 1);

					logger.debug("Select {}", m, i + j + 1);

					return;
				}
			}

			logger.debug("Collapse row {}", i);

			treeMovies.collapseRow(i);
		}
	}

    @Override
    public void selectFirst(){
		Object firstCategory = treeModel.getChild(treeModel.getRoot(), 0);

		if(firstCategory != null && treeModel.getChildCount(firstCategory) > 0){
			treeMovies.expandRow(0);
			treeMovies.setSelectionRow(1);
		}
    }

	@Override
	public void refreshData(){
		for(MoviePanel panel : layeredPanel.getLayers()){
			panel.setMovie(model.getCurrentMovie());
		}
	}

	@Override
	public Movie getSelectedMovie(){
		return treeMovies.getSelectionPath() == null ? null : (Movie) treeMovies.getSelectionPath().getLastPathComponent();
	}

	@Override
	public void expandAll(){
		treeMovies.expandAll();
	}

	@Override
	public void collapseAll(){
		treeMovies.collapseAll();
	}

	@Override
    public void resort(){
        getImplementationView().displayListChanged();
    }
}