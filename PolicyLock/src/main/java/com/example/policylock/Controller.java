package com.example.policylock;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.event.EventHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;
import org.bson.Document;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import xmlwise.Plist;

public class Controller {
    //For Login
    @FXML
    private Button loginButton;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Label incorrect;
    @FXML
    private Label triesLabel;
    @FXML
    private HBox notification;
    @FXML
    private Button closeNotificationButton;

    private int tries = 3;

    //Username & Password Settings
    private static final String USERNAME_VALUE = "u";
    private static final String PASSWORD_VALUE = "p";

    //Log out Buttons
    @FXML
    private Button confirmLogoutButton;
    @FXML
    private Button logoutButton;

    //Page switching buttons
    @FXML
    private Button homePageButton;
    @FXML
    private Button devicesPageButton;
    @FXML
    private Button applicationsPageButton;
    @FXML
    private Button applicationNameButton;
    @FXML
    private Button localDevicePageButton;
    @FXML
    private Button settingsPageButton;
    @FXML
    private Button permissionSettingsPageButton;
    @FXML
    private Button logSettingsPageButton;
    @FXML
    private Button breadcrumb;
    @FXML
    private Button notificationSettingsPageButton;
    @FXML
    private Button accountSettingsPageButton;
    @FXML
    private Button logPageButton;

    //Background anchorpane on which each UI element is placed. Use for inactivity timer
    @FXML
    private GridPane gridPane;

    @FXML
    private ScrollPane applicationsScrollPane;

    @FXML
    private ScrollPane permissionScrollPane;

    //Timer variables used for handling inactivity
    private int inactivityTimeAllowance = 2;
    private PauseTransition inactivityTimeCounter = new PauseTransition();
    private static boolean timeOutCompleted = false; //Variable used to check if timeout has already been completed to fix multiple log in screen issue from multiple anchor panes being activated

    //File Path Variables
    private static final String APPLICATIONS_PATH = "/Applications"; // for mac only

    //Breadcrumb Styling
    private static final String HIGHLIGHT_STYLE = "-fx-text-fill: #33D7FF; -fx-background-color: transparent;";
    private static final String UNHIGHLIGHT_STYLE = "-fx-text-fill: #909090; -fx-background-color: transparent;";

    public void highlightHome() { homePageButton.setStyle(HIGHLIGHT_STYLE); }

    public void unhighlightHome() { homePageButton.setStyle(UNHIGHLIGHT_STYLE); }

    public void highlightSettings() { settingsPageButton.setStyle(HIGHLIGHT_STYLE); }

    public void unhighlightSettings() { settingsPageButton.setStyle(UNHIGHLIGHT_STYLE); }

    public void highlightBreadcrumb() { breadcrumb.setStyle(HIGHLIGHT_STYLE); }

    public void unhighlightBreadcrumb() { breadcrumb.setStyle(UNHIGHLIGHT_STYLE); }

    public void highlightLogSettings() { logSettingsPageButton.setStyle(HIGHLIGHT_STYLE); }

    public void unhighlightLogSettings() { logSettingsPageButton.setStyle(UNHIGHLIGHT_STYLE); }

    public void highlightDevices() { devicesPageButton.setStyle(HIGHLIGHT_STYLE); }

    public void unhighlightDevices() { devicesPageButton.setStyle(UNHIGHLIGHT_STYLE); }

    public void highlightLocalDevice() { applicationsPageButton.setStyle(HIGHLIGHT_STYLE); }

    public void unhighlightLocalDevice() { applicationsPageButton.setStyle(UNHIGHLIGHT_STYLE); }

    public void login() throws IOException {

        if (username.getText().equals(USERNAME_VALUE) && password.getText().equals(PASSWORD_VALUE) && (tries > 0)) {
            Controller.timeOutCompleted = false; //Resets the timeout variable
            Stage stage = (Stage) loginButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader();
            if (loginButton.getText().equals("Login"))
                loader.setLocation(getClass().getResource("homeResize.fxml"));
            else
                loader.setLocation(getClass().getResource("account_settingsResize.fxml"));
            GridPane mainLayout = loader.load();
            stage.getScene().setRoot(mainLayout);
        }

        else {
            incorrect.setText("Incorrect username or password");
            tries -= 1;

            //Limited amount of tries
            if (tries > 0)
            {
                triesLabel.setText("Attempts Left: " + tries);
            }
            else{
                incorrect.setText("You have been locked out");
                triesLabel.setText("");
            }


            //Resets the login screen
            username.setText("");
            password.setText("");
            username.requestFocus();
        }
    }

    public void closeNotification() { notification.setVisible(false); }

    //Method that handles when the enter key is pressed in text boxes on the enter page
    @FXML
    private void onEnter(ActionEvent event) throws IOException{
        //Checks if the source calling the actionEvent is the Username box or password
        if (event.getSource().getClass().equals(username.getClass()))
            password.requestFocus();
        else { login(); }
    }

    public void home() throws IOException {
        Stage stage = (Stage) homePageButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("homeResize.fxml"));
        GridPane mainLayout = loader.load();
        stage.getScene().setRoot(mainLayout);
    }

    public void devices() throws IOException {
        Stage stage = (Stage) devicesPageButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("devicesResize.fxml"));
        GridPane mainLayout = loader.load();
        stage.getScene().setRoot(mainLayout);
        pauseInactivityTimer();
    }

    public void settings() throws IOException {
        Stage stage = (Stage) settingsPageButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("settingsResize.fxml"));
        GridPane mainLayout = loader.load();
        stage.getScene().setRoot(mainLayout);
        pauseInactivityTimer();
    }

    public void permissionSettings() throws IOException {
        Stage stage = (Stage) permissionSettingsPageButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("permission_settingsResize.fxml"));
        GridPane mainLayout = loader.load();
        stage.getScene().setRoot(mainLayout);
        pauseInactivityTimer();
    }

    public void logSettings() throws IOException {
        Stage stage = (Stage) logSettingsPageButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("log_settingsResize.fxml"));
        GridPane mainLayout = loader.load();
        stage.getScene().setRoot(mainLayout);
        pauseInactivityTimer();
    }

    public void logLogSettings() throws IOException {
        Stage stage = (Stage) logSettingsPageButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("log_settingsResize.fxml"));
        GridPane mainLayout = loader.load();
        Controller c = loader.getController();
        c.breadcrumb.setText("LOG");
        stage.getScene().setRoot(mainLayout);
        pauseInactivityTimer();
    }

    public void breadcrumbTrace() throws IOException {
        Stage stage = (Stage) breadcrumb.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();
        if (breadcrumb.getText().equals("SETTINGS"))
            loader.setLocation(getClass().getResource("settingsResize.fxml"));
        else
            loader.setLocation(getClass().getResource("logResize.fxml"));
        GridPane mainLayout = loader.load();
        stage.getScene().setRoot(mainLayout);
        pauseInactivityTimer();
    }

    public void notificationSettings() throws IOException {
        Stage stage = (Stage) notificationSettingsPageButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("notification_settingsResize.fxml"));
        GridPane mainLayout = loader.load();
        stage.getScene().setRoot(mainLayout);
        pauseInactivityTimer();
    }

    public void accountSettingsLogin() throws IOException {
        Stage stage = (Stage) accountSettingsPageButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("account_settingsLogin.fxml"));
        GridPane mainLayout = loader.load();
        stage.getScene().setRoot(mainLayout);
        pauseInactivityTimer();
    }

    public void log() throws IOException {
        Stage stage = (Stage) logPageButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("logResize.fxml"));
        GridPane mainLayout = loader.load();
        stage.getScene().setRoot(mainLayout);
        pauseInactivityTimer();
    }

    public void confirmLogout() throws IOException {
        Stage stage = (Stage) confirmLogoutButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("confirm_logoutResize.fxml"));
        GridPane mainLayout = loader.load();
        stage.getScene().setRoot(mainLayout);
        pauseInactivityTimer();
    }

    public void logout() throws IOException {
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("loginResize.fxml"));
        GridPane mainLayout = loader.load();
        stage.getScene().setRoot(mainLayout);
        pauseInactivityTimer();
    }

    public void pauseInactivityTimer(){
        inactivityTimeCounter.stop();
    }
    public void inactivityTimer(){
        inactivityTimeCounter.setDuration(Duration.seconds(inactivityTimeAllowance));
        inactivityTimeCounter.setOnFinished( event -> {
            try {
                appTimeOut();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        inactivityTimeCounter.play();
    }

    private void appTimeOut() throws IOException {
        if (!timeOutCompleted) {
            Stage stage = (Stage) gridPane.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("loginResize.fxml"));
            GridPane mainLayout = loader.load();
            Controller c = loader.getController();
            c.notification.setVisible(true);
            stage.getScene().setRoot(mainLayout);
            timeOutCompleted = true;
        }
    }

    public void goToApplicationsPage() throws IOException {
        Stage stage = (Stage) applicationsPageButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("deviceApplications.fxml"));
        GridPane mainLayout = loader.load();
        stage.getScene().setRoot(mainLayout);
        pauseInactivityTimer();

        applicationsScrollPane = (ScrollPane) loader.getNamespace().get("applicationsScrollPane");
        VBox appVBox = new VBox();


        ArrayList<Application> apps = getLocalApplications();
        ArrayList<HBox> rows = new ArrayList<>();
        for (int i = 0; i < apps.size(); i += 3) {
            HBox newRow = new HBox();
            newRow.getChildren().add(createApplicationButton(apps.get(i)));
            if (i + 1 < apps.size()) {
                newRow.getChildren().add(createApplicationButton(apps.get(i + 1)));
            }
            if (i + 2 < apps.size()) {
                newRow.getChildren().add(createApplicationButton(apps.get(i + 2)));
            }
            rows.add(newRow);
        }

        for (HBox hBox : rows) {
            hBox.setSpacing(30);
            appVBox.getChildren().add(hBox);
            appVBox.setAlignment(Pos.CENTER);
        }

        appVBox.setSpacing(30);
        appVBox.setPadding(new Insets(30, 30, 30, 30));

        applicationsScrollPane.setContent(appVBox);
        updateApplicationsForDatabase();
    }

    private Button createApplicationButton(Application app) {
        Image iconImage;
//        try {
//            File file = new File(app.getPath());
//            final javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
//            javax.swing.Icon icon = fc.getUI().getFileView(fc).getIcon(file);
//
//            BufferedImage bufferedImage = new BufferedImage(
//                    icon.getIconWidth(),
//                    icon.getIconHeight(),
//                    BufferedImage.TYPE_INT_ARGB
//            );
//            icon.paintIcon(null, bufferedImage.getGraphics(), 0, 0);
//            iconImage = SwingFXUtils.toFXImage(bufferedImage, null);
//        }
//        catch (Exception e) {
//            File defaultImageFile = new File(System.getProperty("user.dir") + "/src/main/java/com/example/policylock/Images/DefaultIcon.png");
//            iconImage = new Image(defaultImageFile.toURI().toString());
//        }

        File defaultImageFile = new File(System.getProperty("user.dir") + "/src/main/resources/img/DefaultIcon.png");
        iconImage = new Image(defaultImageFile.toURI().toString());


        ImageView btnView = new ImageView(iconImage);
        btnView.setFitHeight(40);
        btnView.setPreserveRatio(true);

        Button newApp = new Button();
        newApp.setPrefSize(150, 80);
        newApp.setGraphic(btnView);
        newApp.setContentDisplay(ContentDisplay.TOP);
        newApp.setText(app.name);
        newApp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    goToApplicationPage(newApp, app);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return newApp;
    }
    //permissions and log of a specific application
    public void goToApplicationPage(Button newApp, Application app) throws IOException {
        String applicationName = app.name;
        Stage stage = (Stage) newApp.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("application.fxml"));
        GridPane mainLayout = loader.load();
        Controller c = loader.getController();
        c.applicationNameButton.setText(applicationName);

        applicationsScrollPane = (ScrollPane) loader.getNamespace().get("permissionScrollPane");
        VBox appVBox = new VBox();

        HBox colNames = createPermissionRow(new Permission("Name", "Description"), Font.font("Arial", FontWeight.BOLD, 15));
        colNames.setBackground(new Background(new BackgroundFill(Color.YELLOW, null, null)));

        appVBox.getChildren().add(colNames);

        Background oddBackground = new Background(new BackgroundFill(Color.WHITE, null, null));
        Background evenBackground = new Background(new BackgroundFill(Color.LIGHTGRAY, null, null));

        if (app.getButtonFormat().length() == 0) {
            HBox errorRow = createPermissionRow(new Permission("ERROR", "Could not read permissions"), Font.font("Arial", FontWeight.NORMAL, 14));
            appVBox.getChildren().add(errorRow);
        }

        boolean _odd = false;
        for (Permission permission : app.getPermissions()) {
            HBox newRow = createPermissionRow(permission, Font.font("Arial", FontWeight.NORMAL, 14));
            if (_odd) {
                newRow.setBackground(oddBackground);
            }
            else {
                newRow.setBackground(evenBackground);
            }
            _odd = !_odd;
            appVBox.getChildren().add(newRow);
        }



//        TableView permTable = createPermissionsTable(app.getPermissions());
//        appVBox.getChildren().add(permTable);

        applicationsScrollPane.setContent(appVBox);

        stage.getScene().setRoot(mainLayout);
        pauseInactivityTimer();

    }

    private TableView createPermissionsTable(ArrayList<Permission> permissions) {
        TableView permTable = new TableView();
        permTable.setEditable(true);

        TableColumn nameCol = new TableColumn("Permission");
        nameCol.setMinWidth(500);
        nameCol.setCellFactory(new PropertyValueFactory<Permission, String>("name"));
        TableColumn descCol = new TableColumn("Description");
        descCol.setMinWidth(1000);
        descCol.setCellFactory(new PropertyValueFactory<Permission, String>("description"));

        ObservableList<Permission> data = FXCollections.observableArrayList(permissions);
        permTable.setItems(data);

        permTable.getColumns().addAll(nameCol, descCol);

        return permTable;
    }

    private HBox createPermissionRow(Permission permission, Font font) {
        int nameWidth = 300;
        int descWidth = 500;

        HBox newRow = new HBox();
        Label permName = new Label();
        permName.setText(permission.getName());
        permName.setMinWidth(nameWidth);
        permName.setMaxWidth(nameWidth);
        permName.setFont(font);

        Label permDesc = new Label();
        permDesc.setText(permission.getDescription());
        permDesc.setMinWidth(descWidth);
        permDesc.setMaxWidth(descWidth);
        permDesc.setFont(font);

        newRow.setPadding(new Insets(5, 5, 5, 5));

        newRow.getChildren().addAll(permName, permDesc);

        return newRow;
    }


    /**
     * Gets a list of the applications on a device.
     * Currently, only works for Macs that did not move the default location of applications directory.
     * @return List of Application objects
     */
    public ArrayList<Application> getLocalApplications() {
        ArrayList<Application> apps = new ArrayList<Application>();
        File f = new File("/Applications");
        ArrayList<File> files = new ArrayList<File>(Arrays.asList(f.listFiles()));
        for (File file : files) {
            if (!file.getName().startsWith(".")) {
                String name = file.getName().split("\\.")[0];

                GregorianCalendar date = new GregorianCalendar();
                date.setTimeInMillis(file.lastModified());

                Application newApp = new Application(name);
                newApp.setDateLastModified(date);
                newApp.setPath(file.getPath());

                ArrayList<File> pckContents = new ArrayList<>(Arrays.asList(file.listFiles()));
                for (File content: pckContents) {
                    if (content.getName().equals("Contents")) {
                        ArrayList<File> folders = new ArrayList<>(Arrays.asList(content.listFiles()));
                        for (File folder : folders) {
                            if (folder.getName().endsWith(".plist")) {
                                ArrayList<Permission> perms = parsePermissions(folder);
                                newApp.setPermissions(perms);
                            }
                        }
                    }
                }

                apps.add(newApp);
            }
        }
        return apps;
    }

    /**
     * Parses the permissions of an application's Info.plist file
     * @param plist Info.plist file
     * @return ArrayList of Permission objects
     */
    public ArrayList<Permission> parsePermissions(File plist) {
        ArrayList<Permission> perms = new ArrayList<>();
        try {
            Map<String, Object> properties = Plist.load(plist.getPath());
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                try {
                    if (entry.getKey().startsWith("NS")) {
                        perms.add(new Permission(entry.getKey().substring(2), entry.getValue().toString()));
                    }
                }
                catch (Exception exception) {
                    System.out.println("Caught it here: " + exception);
                }
            }
            return perms;
        }
        catch (Exception e) {
            System.out.println("Error: " + e);
            perms.add(new Permission("ERROR", "Could not read permissions"));
            return perms;
        }
    }


    private void updateApplicationsForDatabase() {
//        String uri = "mongodb+srv://PolicyLock:PolicyLock@policylock.rrwer.mongodb.net/PolicyLock?retryWrites=true&w=majority";
//        try (MongoClient mongoClient = MongoClients.create(uri)) {
//            MongoDatabase database = mongoClient.getDatabase("PolicyLock");
//            MongoCollection<Document> collection = database.getCollection("Devices");
//            Document doc = collection.find(eq("Test1", "Hello World!")).first();
//            System.out.println(doc.toJson());
//        }
    }

public void buildPermissionList(ArrayList<Permission> perms, Map.Entry<String, Object> entry){
    try {
        if (entry.getKey().startsWith("NS")) {
            perms.add(new Permission(entry.getKey().substring(2), entry.getValue().toString()));
        }
    }
    catch (Exception exception) {
        System.out.println("Caught it here: " + exception);
    }
}




}