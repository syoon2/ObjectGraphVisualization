package ch.hsr.ogv;

import ch.hsr.ogv.controller.*;
import ch.hsr.ogv.dataaccess.Persistence;
import ch.hsr.ogv.dataaccess.UserPreferences;
import ch.hsr.ogv.util.FXMLResourceUtil;
import ch.hsr.ogv.util.ResourceLocator;
import ch.hsr.ogv.util.ResourceLocator.Resource;
import ch.hsr.ogv.view.SubSceneAdapter;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Initialises the stage and the controllers.
 */
public class StageBuilder {

    private final static Logger logger = LogManager.getLogger(StageBuilder.class);

    private static final int MIN_WIDTH = 1024;
    private static final int MIN_HEIGHT = 768;

    private String appTitle = "Object Graph Visualizer";
    private Stage primaryStage;
    private BorderPane rootLayout;
    private SubSceneAdapter subSceneAdapter;

    private ModelViewConnector mvConnector;
    private ObjectGraph objectGraph;
    private Persistence persistence;

    private ViewController viewController = new ViewController();
    private SelectionController selectionController = new SelectionController();
    private ContextMenuController contextMenuController = new ContextMenuController();
    private TextFieldController textFieldController = new TextFieldController();
    private MouseMoveController mouseMoveController = new MouseMoveController();
    private CameraController cameraController = new CameraController();
    private DragMoveController dragMoveController = new DragMoveController();
    private DragResizeController dragResizeController = new DragResizeController();
    private RelationCreationController relationCreationController = new RelationCreationController();

    private ModelController stageManager = new ModelController();

    public StageBuilder(Stage primaryStage) {
        if (primaryStage == null) {
            throw new IllegalArgumentException("The primaryStage argument can not be null!");
        }
        this.primaryStage = primaryStage;

        loadRootLayout();
        setupStage();

        initMVConnector();
        initObjectGraph();
        initPersistancy();

        initViewController();
        initSelectionController();
        initContextMenuController();
        initMouseMoveController();
        initCameraController();
        initDragController();
        initRelationCreationController();

        initStageManager();

        this.selectionController.setSelected(this.subSceneAdapter, true, this.subSceneAdapter);
    }

    private void setupStage() {
        this.primaryStage.setTitle(this.appTitle);
        this.primaryStage.setMinWidth(MIN_WIDTH);
        this.primaryStage.setMinHeight(MIN_HEIGHT);
        this.primaryStage.getIcons().add(new Image(ResourceLocator.getResourcePath(Resource.ICON_GIF).toExternalForm())); // set the application icon

        Pane canvas = (Pane) this.rootLayout.getCenter();
        this.subSceneAdapter = new SubSceneAdapter(canvas.getWidth(), canvas.getHeight());
        SubScene subScene = this.subSceneAdapter.getSubScene();
        canvas.getChildren().add(subScene);
        subScene.widthProperty().bind(canvas.widthProperty());
        subScene.heightProperty().bind(canvas.heightProperty());

        Scene scene = new Scene(this.rootLayout);
        String sceneCSS = ResourceLocator.getResourcePath(Resource.SCENE_CSS).toExternalForm();
        scene.getStylesheets().add(sceneCSS);
        this.primaryStage.setScene(scene);
        this.primaryStage.show();
        this.subSceneAdapter.getSubScene().requestFocus();
    }

    private void loadRootLayout() {
        FXMLLoader loader = FXMLResourceUtil.prepareLoader(Resource.ROOTLAYOUT_FXML); // load rootlayout from fxml file
        try {
            loader.setController(this.viewController);
            this.rootLayout = (BorderPane) loader.load();
        }
        catch (IOException | ClassCastException e) {
            logger.debug(Level.DEBUG, e);
        }
    }

    private void initMVConnector() {
        this.mvConnector = new ModelViewConnector();
    }

    private void initObjectGraph() {
        this.objectGraph = new ObjectGraph(this.mvConnector, this.subSceneAdapter);
    }

    private void initPersistancy() {
        UserPreferences.setOGVFilePath(null); // reset user preferences of file path
        persistence = new Persistence(this.mvConnector.getModelManager());
    }

    private void initViewController() {
        this.viewController.setPrimaryStage(this.primaryStage);
        this.viewController.setSubSceneAdapter(this.subSceneAdapter);
        this.viewController.setMVConnector(this.mvConnector);
        this.viewController.setObjectGraph(this.objectGraph);
        this.viewController.setPersistence(this.persistence);
        this.viewController.setSelectionController(this.selectionController);
        this.viewController.setCameraController(this.cameraController);
        this.viewController.setRelationCreationController(this.relationCreationController);
    }

    private void initSelectionController() {
        this.selectionController.enableSubSceneSelection(this.subSceneAdapter);
        this.selectionController.addObserver(this.viewController);
        this.selectionController.addObserver(this.contextMenuController);
    }

    private void initContextMenuController() {
        this.contextMenuController.enableActionEvents(this.selectionController, this.subSceneAdapter);
        this.contextMenuController.setMVConnector(this.mvConnector);
        this.contextMenuController.setRelationCreationController(this.relationCreationController);
        this.contextMenuController.enableContextMenu(this.subSceneAdapter);
    }

    private void initMouseMoveController() {
        this.mouseMoveController.enableMouseMove(this.subSceneAdapter.getFloor());
        this.mouseMoveController.addObserver(relationCreationController);
    }

    private void initCameraController() {
        this.cameraController.enableCamera(this.subSceneAdapter);
    }

    private void initDragController() {
        this.dragMoveController.addObserver(this.cameraController);
        this.dragMoveController.addObserver(this.viewController);
        this.dragMoveController.addObserver(this.selectionController);
        this.dragResizeController.addObserver(this.cameraController);
        this.dragResizeController.addObserver(this.viewController);
        this.dragResizeController.addObserver(this.selectionController);
    }

    private void initRelationCreationController() {
        relationCreationController.setSelectionController(selectionController);
        relationCreationController.setSubSceneAdapter(subSceneAdapter);
        relationCreationController.setMvConnector(mvConnector);
    }

    private void initStageManager() {
        this.stageManager.setRootLayout(this.rootLayout);
        this.stageManager.setSubSceneAdapter(this.subSceneAdapter);
        this.stageManager.setMVConnector(this.mvConnector);
        this.stageManager.setSelectionController(this.selectionController);

        this.stageManager.setContextMenuController(this.contextMenuController);
        this.stageManager.setTextFieldController(this.textFieldController);
        this.stageManager.setMouseMoveController(this.mouseMoveController);
        this.stageManager.setDragMoveController(this.dragMoveController);
        this.stageManager.setDragResizeController(this.dragResizeController);
    }

}
