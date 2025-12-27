package com.example.ecommerce;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class HelloApplication extends Application {
    private final ObservableList<OrderItem> products =
            FXCollections.observableArrayList(
                    new OrderItem(333,"Laptop", 1200),
                    new OrderItem(444,"Mouse", 25),
                    new OrderItem(777,"Keyboard", 400),
                    new OrderItem(666,"Monitor", 1000),
                    new OrderItem(1234,"Headphones", 40)
            );
    private Order currentOrder = new Order(1);
    private ObservableList<OrderItem> cart =
            FXCollections.observableArrayList();


    @Override
    public void start(Stage stage) throws IOException {
        Image bgImage = new Image(
                getClass().getResource("/image.png").toExternalForm()
        );
        ImageView background = new ImageView(bgImage);
        background.setFitWidth(600);
        background.setFitHeight(400);

        // 🔹 Overlay content
        Label title = new Label("E-Commerce");
        title.setStyle("-fx-font-size: 28px; -fx-text-fill: white; -fx-font-weight: bold;");

        TextField nameField = new TextField();
        nameField.setPromptText("Enter your Name");

        TextField emailField = new TextField();
        emailField.setPromptText("Enter ID");

        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white;");

        loginButton.setOnAction(e -> {
            try {
                openCatalog();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            // 👉 later: switch to catalog scene
        });

        VBox content = new VBox(15, title, nameField, emailField, loginButton);
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-background-color: rgba(0,0,0,0.5);");
        content.setPrefSize(600, 400);

        StackPane root = new StackPane(background, content);

        Scene scene = new Scene(root, 600, 400);

        stage.setTitle("E-Commerce Login");
        stage.setScene(scene);
        stage.show();

    }
    private void openCatalog()throws IOException, InterruptedException{
        Stage primaryStage=new Stage();
        ComboBox<OrderItem> comboBox = new ComboBox<>(products);
        comboBox.setPromptText("Select Item");

        Button addBtn = new Button("Add to Cart");
        ListView<OrderItem> cartView = new ListView<>(cart);

        addBtn.setOnAction(e -> {
            OrderItem selected = comboBox.getValue();
            if (selected != null) {
                currentOrder.addItem(selected); // add to Order
                cart.add(selected);
            }
        });
        Button checkoutBtn = new Button("Checkout");
        checkoutBtn.setOnAction(e -> {
            try {
                openCheckoutStage();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox root = new VBox(10,
                new Label("Select Product:"),
                comboBox,
                addBtn,
                new Label("Cart:"),
                cartView,
                checkoutBtn
        );
        root.setAlignment(Pos.CENTER);

        primaryStage.setScene(new Scene(root, 400, 500));
        primaryStage.setTitle("E-Commerce Shop");
        primaryStage.show();

    }
    private void openCheckoutStage() throws IOException, InterruptedException {
        if (cart.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Cart is empty").showAndWait();
            return;
        }
        String reso= EcommerceApi.postOrder(currentOrder.toPostJson(1001));
        String idPart = reso.split("\"id\"\\s*:\\s*")[1];

        // Step 2: Take the value before the next comma
        String idStr = idPart.split(",")[0].trim();

        // Step 3: Convert to integer
        int id = Integer.parseInt(idStr);

        System.out.println(reso);

        Stage checkoutStage = new Stage();
        Button proceedPaymentBtn = new Button("Proceed to Payment");

        proceedPaymentBtn.setOnAction(e -> {
            checkoutStage.close();
            openPaymentStage(id, currentOrder.getPrice());
        });

        VBox root = new VBox(15,
                new Label("✅ Order Created"),
                new Label("Order ID: "+id ),
                new Label("Total: $" +currentOrder.getPrice()),
                proceedPaymentBtn
        );
        root.setAlignment(Pos.CENTER);
        checkoutStage.setScene(new Scene(root, 300, 200));
        checkoutStage.setTitle("Confirmation");
        checkoutStage.show();

        cart.clear();
    }
    private void openPaymentStage(int orderId, double total) {
        Stage paymentStage = new Stage();

        TextField holderField = new TextField();
        holderField.setPromptText("Cardholder Name");

        TextField cardField = new TextField();
        cardField.setPromptText("Card Number");

        Button payBtn = new Button("Pay");

        payBtn.setOnAction(e -> {
            String holder = holderField.getText().trim();
            String card = cardField.getText().trim();
            if (holder.isEmpty() && card.isEmpty()){
                visacashprint();
                paymentStage.close();
                System.exit(0);
            }
            if ((!(card.isEmpty()) && (holder.isEmpty())) || (card.isEmpty() && !(holder.isEmpty()))) {
                new Alert(Alert.AlertType.WARNING, "Please fill all fields").showAndWait();

                return;
            }
            visacashprint(holder,card);
            VisaPay paid=new VisaPay(111, currentOrder.getPrice(), holder, card);
            // Here you would call your payment API
            System.out.println("Processing payment for Order ID " + orderId +
                    ", Holder: " + holder + ", Card: " + card + ", Total: $" +currentOrder.getPrice() );

            paymentStage.close();

            // Show final confirmation
            Stage finalStage = new Stage();
            String resp="";
            try {
                resp=EcommerceApi.postPayment(paid.toPostJson(orderId));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            System.out.println(resp);
            VBox root = new VBox(10,
                    new Label("💳 Payment Successful!"),
                    new Label("Order ID: " + orderId),
                    new Label("Amount Paid: $" + total)
            );
            finalStage.setScene(new Scene(root, 300, 150));
            finalStage.setTitle("Payment Confirmation");
            finalStage.show();
        });

        VBox root = new VBox(10,
                new Label("Payment for Order ID: " + orderId),
                new Label("Total: $" + total),
                holderField,
                cardField,
                payBtn
        );

        paymentStage.setScene(new Scene(root, 350, 250));
        paymentStage.setTitle("Payment");
        paymentStage.show();
    }
    //Overloading
    public void visacashprint(){
        System.out.println("Paid by Cash!");
    }
    public void visacashprint(String holder, String card){
        System.out.println("Paid by VISA");
    }

}

