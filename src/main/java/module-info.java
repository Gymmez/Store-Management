module com.example.ecommerce {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;


    opens com.example.ecommerce to javafx.fxml;
    exports com.example.ecommerce;
}