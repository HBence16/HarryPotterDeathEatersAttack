module org.example.harrypotterthedeatheatersattack {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.harrypotterthedeatheatersattack to javafx.fxml;
    exports org.example.harrypotterthedeatheatersattack;
}