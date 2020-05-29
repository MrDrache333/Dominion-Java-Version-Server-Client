package de.uol.swp.client.game.container;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Region;

/**
 * Layoutcontainer für die Karten, die von der Hand ausgespielt worden sind.
 *
 * @author Devin
 * @since Sprint 6
 */

public class PlayedCardLayoutContainer extends Region {

    /**
     * Instantiiert einen neuen PlayedCardLayoutContainer.
     *
     * @author Devin
     * @since Sprint 6
     */
    public PlayedCardLayoutContainer() {
    }

    /**
     * Instantiiert einen neuen PlayedCardLayoutContainer.
     *
     * @param layoutX x-Koordinate
     * @param layoutY y-Koordinate
     * @param height  Höhe
     * @param width   Breite
     * @author Devin
     * @since Sprint 6
     */
    public PlayedCardLayoutContainer(double layoutX, double layoutY, double height, double width, String id) {
        this.setLayoutX(layoutX);
        this.setLayoutY(layoutY);
        this.setPrefHeight(height);
        this.setPrefWidth(width);
        this.setId(id);
        if (this.getId().equals("1.PCLC")) {
            this.setRotate(180);
        } else {
            if (this.getId().equals("2.PCLC")) {
                this.setRotate(90);
            } else if (this.getId().equals("3.PCLC")) {
                this.setRotate(270);
            }
        }
    }

    /**
     * Hier wird festgelegt, wie sich die Kinder anordnen sollen.
     * Wenn die Pane breit genug ordnen sie sich, von der Mitte ausgehend, direkt nebeneinander an.
     * Ansonsten überlappen sie sich, aber immer nur so viel wie nötig.
     *
     * @author Devin
     * @since Sprint 6
     */
    @Override
    protected void layoutChildren() {
        ObservableList<Node> children = getChildren();
        double sum = 0;
        double size = children.size();
        for (Node child : children) {
            sum += Math.round(child.getBoundsInLocal().getWidth());
        }
        double diff = this.getWidth() - sum;
        if (diff > 0) {
            double counter = size;
            double width = sum / size;
            for (Node child : children) {
                child.relocate(this.getWidth() / 2 + width * (size / 2) - width * counter, 0);
                counter--;
            }
        } else {
            double change = (-diff) / (size - 1);
            int i = 0;
            for (Node child : children) {
                child.relocate(i, 0);
                i += Math.round(child.getBoundsInLocal().getWidth() - change);
            }
        }
    }

    /**
     * Getter für fie Liste der Kinderknoten
     *
     * @return Kinder
     * @author Devin
     * @since Sprint 6
     */
    @Override
    public ObservableList<Node> getChildren() {
        return super.getChildren();
    }

}

