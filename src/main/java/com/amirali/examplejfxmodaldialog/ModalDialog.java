package com.amirali.examplejfxmodaldialog;

import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ModalDialog extends Stage {

    private final Builder builder;
    private final ScaleTransition scaleTransition = new ScaleTransition();
    private final Parent ownerRoot;
    private final GaussianBlur gaussianBlur = new GaussianBlur(0);

    public ModalDialog(Builder builder) {
        super(StageStyle.TRANSPARENT);

        this.builder = builder;
        ownerRoot = builder.owner.getScene().getRoot();
        
        initOwner(builder.owner);
        initModality(Modality.APPLICATION_MODAL);
        var scene = new Scene(builder.root, Color.TRANSPARENT);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("modal-dialog-theme.css")).toExternalForm());
        setScene(scene);

        scaleTransition.durationProperty().bind(builder.durationProperty);
        scaleTransition.setFromX(0);
        scaleTransition.setFromY(0);
        scaleTransition.setToX(1);
        scaleTransition.setToY(1);
        scaleTransition.setNode(builder.root);
        scaleTransition.currentTimeProperty().addListener((observableValue, oldValue, newValue) -> {
            var radius = (newValue.toMillis() / scaleTransition.getDuration().toMillis()) * builder.blurRadiusProperty.doubleValue();
            gaussianBlur.setRadius(radius);
        });
        scaleTransition.setInterpolator(Interpolator.EASE_BOTH);
    }

    public void setDialogTitle(@NotNull String title) {
        builder.dialogTitleProperty.set(title);
    }

    public String getDialogTitle() {
        return builder.dialogTitleProperty.get();
    }

    public StringProperty dialogTitleProperty() {
        return builder.dialogTitleProperty;
    }

    public void setDialogMessage(@NotNull String message) {
        builder.dialogMessageProperty.set(message);
    }

    public String getDialogMessage() {
        return builder.dialogMessageProperty.get();
    }

    public StringProperty dialogMessageProperty() {
        return builder.dialogMessageProperty;
    }

    public void setBlurRadius(double radius) {
        builder.blurRadiusProperty.set(radius);
    }

    public double getBlurRadius() {
        return builder.blurRadiusProperty.get();
    }

    public DoubleProperty blurRadiusProperty() {
        return builder.blurRadiusProperty;
    }

    public void setDuration(@NotNull Duration duration) {
        builder.durationProperty.set(duration);
    }

    public Duration getDuration() {
        return builder.durationProperty.get();
    }

    public ObjectProperty<Duration> durationProperty() {
        return builder.durationProperty;
    }

    public static class Builder {

        // UI components
        private final BorderPane root = new BorderPane();
        private final Label titleLabel = new Label(), messageLabel = new Label();
        private final ButtonBar buttonBar = new ButtonBar();
        private final Button positiveButton = new Button(), negativeButton = new Button();

        private boolean titleAdded, messageAdded, positiveButtonAdded, negativeButtonAdded;
        private final StringProperty dialogTitleProperty = new SimpleStringProperty() {
            @Override
            public void set(String s) {
                super.set(s);
                if (!titleAdded) {
                    root.setTop(titleLabel);
                    titleAdded = true;
                }
            }
        }, dialogMessageProperty = new SimpleStringProperty() {
            @Override
            public void set(String s) {
                super.set(s);
                if (!messageAdded) {
                    root.setCenter(messageLabel);
                    messageAdded = true;
                }
            }
        };
        private final DoubleProperty blurRadiusProperty = new SimpleDoubleProperty(10);
        private final ObjectProperty<Duration> durationProperty = new SimpleObjectProperty<>(Duration.millis(300));
        private final Window owner;
        private ModalDialog modalDialog;

        public Builder(@NotNull Window owner) {
            this.owner = owner;

            root.setId("window");
            root.setPrefSize(400, 200);

            titleLabel.textProperty().bind(dialogTitleProperty);
            titleLabel.setId("title");
            titleLabel.setPadding(new Insets(8));

            messageLabel.textProperty().bind(dialogMessageProperty);
            messageLabel.setId("message");
            messageLabel.setPadding(new Insets(8));
            messageLabel.setAlignment(Pos.TOP_LEFT);
            messageLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

            positiveButton.setId("positive-button");
            negativeButton.setId("negative-button");

            buttonBar.setPadding(new Insets(8));
            root.setBottom(buttonBar);
        }

        public Builder(@NotNull Window owner, @NotNull String title, @NotNull String message) {
            this(owner);
            dialogTitleProperty.set(title);
            dialogMessageProperty.set(message);
        }

        public Builder setDialogMessage(@NotNull String message) {
            dialogMessageProperty.set(message);

            return this;
        }

        public Builder setDialogTitle(@NotNull String title) {
            dialogTitleProperty.set(title);

            return this;
        }

        public Builder setPositiveButton(@NotNull String text, @NotNull EventHandler<ActionEvent> event) {
            positiveButton.setText(text);
            positiveButton.setOnAction(event1 -> {
                if (modalDialog != null)
                    modalDialog.closeDialog();
                event.handle(event1);
            });

            if (!positiveButtonAdded) {
                buttonBar.getButtons().add(positiveButton);
                positiveButtonAdded = true;
            }

            return this;
        }

        public Builder setNegativeButton(@NotNull String text, @NotNull EventHandler<ActionEvent> event) {
            negativeButton.setText(text);
            negativeButton.setOnAction(event1 -> {
                if (modalDialog != null)
                    modalDialog.closeDialog();
                event.handle(event1);
            });

            if (!negativeButtonAdded) {
                buttonBar.getButtons().add(negativeButton);
                negativeButtonAdded = true;
            }

            return this;
        }

        public Builder setBlurRadius(double radius) {
            blurRadiusProperty.set(radius);

            return this;
        }

        public Builder setDuration(@NotNull Duration duration) {
            durationProperty.set(duration);

            return this;
        }

        public ModalDialog create() {
            modalDialog = new ModalDialog(this);
            return modalDialog;
        }
    }

    public void openDialog() {
        show();
        var bounds = ownerRoot.localToScreen(ownerRoot.getBoundsInLocal());
        setX(bounds.getMinX() + (bounds.getWidth() - getWidth()) / 2);
        setY(bounds.getMinY() + (bounds.getHeight() - getHeight()) / 2);
        ownerRoot.setEffect(gaussianBlur);
        scaleTransition.play();
    }

    public void closeDialog() {
        scaleTransition.setAutoReverse(true);
        scaleTransition.setCycleCount(2);
        scaleTransition.setOnFinished(event -> {
            close();
            ownerRoot.setEffect(null);
        });
        scaleTransition.playFrom(scaleTransition.getDuration());
    }
}
