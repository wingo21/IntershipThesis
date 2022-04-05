package com.example.internshipthesis;

import androidx.annotation.NonNull;
import androidx.car.app.CarContext;
import androidx.car.app.Screen;
import androidx.car.app.hardware.CarHardwareManager;
import androidx.car.app.model.Pane;
import androidx.car.app.model.PaneTemplate;
import androidx.car.app.model.Row;
import androidx.car.app.model.Template;

/**
 * This is a simple Activity that gets installed on the Head Unit (car screen).
 * It's pretty much just a check that the app has been correctly connected to
 * the car and will be able to communicate with it
 */

public class InternshipThesisScreen extends Screen implements CarHardwareManager {

    public InternshipThesisScreen(CarContext carContext) {
        super(carContext);
    }

    @NonNull
    @Override
    public Template onGetTemplate() {
        Row row = new Row.Builder().setTitle("The application is correctly connected to the car").addText("You will now be able to start navigation directly on the car screen from the phone").build();

        return new PaneTemplate.Builder(new Pane.Builder().addRow(row).build()).setTitle("Enjoy").build();

    }
}
