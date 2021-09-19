module com.example.gomoku_dianyang_javafx {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.gomoku_dianyang_javafx to javafx.fxml;
    exports com.example.gomoku_dianyang_javafx;
}