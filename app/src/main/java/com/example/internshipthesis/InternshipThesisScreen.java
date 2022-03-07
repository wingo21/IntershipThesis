package com.example.internshipthesis;

import androidx.annotation.NonNull;
import androidx.car.app.CarContext;
import androidx.car.app.Screen;
import androidx.car.app.model.Pane;
import androidx.car.app.model.PaneTemplate;
import androidx.car.app.model.Row;
import androidx.car.app.model.Template;

public class InternshipThesisScreen extends Screen {
    public InternshipThesisScreen(CarContext carContext) {
        super(carContext);
    }

    @NonNull
    @Override
    public Template onGetTemplate() {
        Row row = new Row.Builder().setTitle("Se stai vedendo questo, funziona").addText("Esempio").build();

        return new PaneTemplate.Builder(new Pane.Builder().addRow(row).build()).setTitle("Ciaooooo").build();
    }
}
