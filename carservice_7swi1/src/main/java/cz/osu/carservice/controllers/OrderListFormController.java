package cz.osu.carservice.controllers;

import cz.osu.carservice.controllers.mainController.MainController;
import cz.osu.carservice.models.entities.*;
import cz.osu.carservice.models.enums.FilterType;
import cz.osu.carservice.models.utils.FormUtils;
import cz.osu.carservice.models.utils.TextUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import javax.persistence.EntityManager;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Filter;
import java.util.stream.Collectors;

public class OrderListFormController extends MainController implements Initializable {
    //region ElementsVariable
    @FXML
    private TableView<Order> viewOrdersTbv;
    @FXML
    private TableColumn<Order, Long> orderIDColumn;
    @FXML
    private TableColumn<Order, LocalDateTime> dateColumn;
    @FXML
    private TableColumn<Order, String> registrationPlateColumn;
    @FXML
    private TableColumn<Order, String> carTypeColumn;
    @FXML
    private TableColumn<Order, Integer> productionYearColumn;
    @FXML
    private TableColumn<Order, String> noteColumn;
    @FXML
    private TableColumn<Order, String> customerNameColumn;
    @FXML
    private TableColumn<Order, String> customerSurnameColumn;
    @FXML
    private TableColumn<Order, String> telephoneColumn;
    @FXML
    private TableColumn<Order, String> emailColumn;
    @FXML
    private TableColumn<Order, String> stateColumn;
    @FXML
    private TableColumn<Order, String> cityColumn;
    @FXML
    private TableColumn<Order, String> streetColumn;
    @FXML
    private TableColumn<Order, String> streetNumberColumn;
    @FXML
    private TableColumn<Order, String> zipCodeColumn;
    @FXML
    private TableColumn<Order, String> timeColumn;
    @FXML
    private TableColumn<Order, String> carServiceColumn;
    @FXML
    private TableColumn<Order, String> tireServiceColumn;
    @FXML
    private TableColumn<Order, String> otherServiceColumn;
    @FXML
    private TableColumn<String, Void> detailColumn;
    @FXML
    private TableColumn<String, Void> editColumn;
    @FXML
    private TableColumn<String, Void> deleteColumn;
    @FXML
    private DatePicker filterDatePicker;
    @FXML
    private Label messageLbl;
    @FXML
    private CheckBox dateCheck;
    @FXML
    private ComboBox<String> filterCB;
    @FXML
    private Button searchBtn;
    @FXML
    private TextField searchField;
    //endregion

    //region Colors
    private final static String HEX_COLOR_GREEN = "#6cc969";
    private final static String HEX_COLOR_BLUE = "#60aac4";
    private final static String HEX_COLOR_RED = "#cf3232";
    //endregion

    //region DataVariable
    private EntityManager entityManager;
    private ObservableList<Order> orderCollections;
    //endregion

    @FXML
    private void returnToMainScene(MouseEvent event) {
        super.setNewFormScene("mainForm", event);
    }

    @FXML
    private void closeApplication(MouseEvent event) {
        super.closeApplication();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        filterDatePicker.setValue(LocalDate.now());
        dateCheck.setSelected(true);
        viewOrdersTbv.setPlaceholder(new Label("Nebyly nalezeny žádné objednávky"));

        for (FilterType type : FilterType.values()) {
            filterCB.getItems().add(type.getFilterType());
        }
        filterCB.getSelectionModel().select(0);

        entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();

        try {
            orderCollections = FXCollections.observableArrayList(findByDate(entityManager, filterDatePicker.getValue()));

            setDataToColumns(orderCollections);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            entityManager.close();
        }

        setButtonsToColumn(detailColumn, "Detail", "showForm", HEX_COLOR_GREEN, "cz.osu.carservice.controllers.ShowFormController");
        setButtonsToColumn(editColumn, "Edit", "editForm", HEX_COLOR_BLUE, "cz.osu.carservice.controllers.EditFormController");
        setButtonsToColumn(deleteColumn, "Delete", "warningForm", HEX_COLOR_RED, "cz.osu.carservice.controllers.WarningFormController");

        dateColumn.setSortType(TableColumn.SortType.ASCENDING);
        viewOrdersTbv.getSortOrder().add(dateColumn);
        viewOrdersTbv.sort();
    }

    @FXML
    private void onChangeItem() {
        entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();

        try {
            LocalDate temp = filterDatePicker.getValue();
            if (dateCheck.isSelected()) {
                orderCollections = FXCollections.observableArrayList(findByDate(entityManager, temp));
            } else {
                orderCollections = FXCollections.observableArrayList(findAllOrders(entityManager));
            }

            setDataToColumns(orderCollections);
        } catch (Exception e) {
            FormUtils.setTextAndRedColorToLabel(messageLbl, "Špatný formát data!");
        } finally {
            entityManager.close();

            dateColumn.setSortType(TableColumn.SortType.ASCENDING);
            viewOrdersTbv.getSortOrder().add(dateColumn);
            viewOrdersTbv.sort();
        }
    }

    @FXML
    private void searchOrders() {
        onChangeItem();

        FilterType searchType = Arrays.stream(FilterType.values())
                .filter(value -> value.getFilterType().equals(filterCB.getSelectionModel().getSelectedItem()))
                .findFirst()
                .orElse(null);

        ObservableList<Order> newList = null;
        String filterValue = searchField.getText().toLowerCase();

        if (TextUtils.isTextEmpty(filterValue)) return;

        switch (Objects.requireNonNull(searchType)) {
            case ID -> {
                List<Order> potentialResult = orderCollections.stream().filter(value -> String.valueOf(value.getId()).equals(filterValue)).collect(Collectors.toList());
                newList = FXCollections.observableArrayList(potentialResult);
            }
            case CAR_REGISTRATION_PLATE -> {
                List<Order> potentialResult = orderCollections.stream().filter(value -> String.valueOf(value.getRegistration_plate()).toLowerCase().equals(filterValue)).collect(Collectors.toList());
                newList = FXCollections.observableArrayList(potentialResult);
            }
            case CUSTOMER_SURNAME -> {
                List<Order> potentialResult = orderCollections.stream().filter(value -> String.valueOf(value.getCustomer().getSurname()).toLowerCase().equals(filterValue)).collect(Collectors.toList());
                newList = FXCollections.observableArrayList(potentialResult);
            }
            case CAR_YEAR_PRODUCTION -> {
                List<Order> potentialResult = orderCollections.stream().filter(value -> String.valueOf(value.getYear_of_production()).equals(filterValue)).collect(Collectors.toList());
                newList = FXCollections.observableArrayList(potentialResult);
            }
            case CUSTOMER_NAME -> {
                List<Order> potentialResult = orderCollections.stream().filter(value -> String.valueOf(value.getCustomer().getFirstName()).toLowerCase().equals(filterValue)).collect(Collectors.toList());
                newList = FXCollections.observableArrayList(potentialResult);
            }
            case CAR_TYPE -> {
                List<Order> potentialResult = orderCollections.stream().filter(value -> String.valueOf(value.getType_of_car()).toLowerCase().equals(filterValue)).collect(Collectors.toList());
                newList = FXCollections.observableArrayList(potentialResult);
            }
            case STATE -> {
                List<Order> potentialResult = orderCollections.stream().filter(value -> value.getCustomer().getAddress().getState().getState_short().toLowerCase().equals(filterValue)).collect(Collectors.toList());
                newList = FXCollections.observableArrayList(potentialResult);
            }
            case CITY -> {
                List<Order> potentialResult = orderCollections.stream().filter(value -> value.getCustomer().getAddress().getCity().toLowerCase().equals(filterValue)).collect(Collectors.toList());
                newList = FXCollections.observableArrayList(potentialResult);
            }
        }

        setDataToColumns(newList);
        dateColumn.setSortType(TableColumn.SortType.ASCENDING);
        viewOrdersTbv.getSortOrder().add(dateColumn);
        viewOrdersTbv.sort();
    }

    private void setButtonsToColumn(TableColumn<String, Void> column, String nameOfButton, String formToRun, String buttonColor, String className) {
        column.setCellFactory(new Callback<>() {
            @Override
            public TableCell<String, Void> call(final TableColumn<String, Void> param) {
                final TableCell<String, Void> cell = new TableCell<>() {
                    private final Button btn = new Button(nameOfButton);

                    {

                        btn.setOnAction((ActionEvent event) -> {
                            Order order = viewOrdersTbv.getItems().get(this.getTableRow().getIndex());
                            OrderListFormController.super.setNewFormScene(formToRun, order, event);
                        });

                        btn.setStyle(String.format("-fx-background-color: %s;", buttonColor));
                        btn.setCursor(Cursor.HAND);

                        try {
                            btn.setUserData(Class.forName(className));
                        } catch (ClassNotFoundException exception) {
                            exception.printStackTrace();
                        }
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                cell.setStyle("-fx-alignment: CENTER;");
                return cell;
            }
        });
    }

    private void setDataToColumns(ObservableList<Order> list) {

        orderIDColumn.setCellValueFactory(new PropertyValueFactory<>("Id"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("Date_of_fulfillment"));
        registrationPlateColumn.setCellValueFactory(new PropertyValueFactory<>("Registration_plate"));
        carTypeColumn.setCellValueFactory(new PropertyValueFactory<>("Type_of_car"));
        productionYearColumn.setCellValueFactory(new PropertyValueFactory<>("Year_of_production"));
        noteColumn.setCellValueFactory(new PropertyValueFactory<>("Note"));

        carServiceColumn.setCellValueFactory((data -> new SimpleStringProperty(data.getValue().getCar_service() == 1 ? "Ano" : "Ne")));
        tireServiceColumn.setCellValueFactory((data -> new SimpleStringProperty(data.getValue().getTire_service() == 1 ? "Ano" : "Ne")));
        otherServiceColumn.setCellValueFactory((data -> new SimpleStringProperty(data.getValue().getOther_service() == 1 ? "Ano" : "Ne")));

        customerNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCustomer().getFirstName()));
        customerSurnameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCustomer().getSurname()));
        telephoneColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCustomer().getTelephoneNumber()));
        emailColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCustomer().getEmail()));

        stateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCustomer().getAddress().getState().getState_short()));
        cityColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCustomer().getAddress().getCity()));
        streetColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCustomer().getAddress().getStreet()));
        streetNumberColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCustomer().getAddress().getStreetNumber()));
        zipCodeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCustomer().getAddress().getPostCode()));

        timeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTime().getTime().toString()));

        viewOrdersTbv.setItems(list);
    }

    private List<Order> findByDate(EntityManager entityManager, LocalDate date) {
        return entityManager.createNamedQuery("Order.findAllByDate", Order.class)
                .setParameter("dateOfFulfillment", date)
                .getResultList();
    }

    private List<Order> findAllOrders(EntityManager entityManager) {
        return entityManager.createNamedQuery("Order.findAll", Order.class)
                .getResultList();
    }
}
