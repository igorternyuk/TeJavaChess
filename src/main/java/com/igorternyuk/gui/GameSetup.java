package com.igorternyuk.gui;

import com.igorternyuk.engine.Alliance;
import com.igorternyuk.engine.player.Player;
import com.igorternyuk.gui.View.PlayerType;

import javax.swing.*;
import java.awt.*;

/**
 * Created by igor on 07.12.18.
 */
public class GameSetup extends JDialog {
    private PlayerType whitePlayerType;
    private PlayerType blackPlayerType;
    private JSpinner searchDepthSpinner;

    GameSetup(final JFrame frame, final boolean modal) {
        super(frame, modal);
        final JPanel panel = new JPanel(new GridLayout(0, 1));
        final JRadioButton whiteHumanButton = new JRadioButton(PlayerType.HUMAN.getLabel());
        final JRadioButton whiteComputerButton = new JRadioButton(PlayerType.COMPUTER.getLabel());
        final JRadioButton blackHumanButton = new JRadioButton(PlayerType.HUMAN.getLabel());
        final JRadioButton blackComputerButton = new JRadioButton(PlayerType.COMPUTER.getLabel());
        whiteHumanButton.setActionCommand(PlayerType.HUMAN.getLabel());
        final ButtonGroup whiteGroup = new ButtonGroup();
        whiteGroup.add(whiteHumanButton);
        whiteGroup.add(whiteComputerButton);
        whiteHumanButton.setSelected(true);

        final ButtonGroup blackGroup = new ButtonGroup();
        blackGroup.add(blackHumanButton);
        blackGroup.add(blackComputerButton);
        blackHumanButton.setSelected(true);

        getContentPane().add(panel);
        panel.add(new JLabel("White"));
        panel.add(whiteHumanButton);
        panel.add(whiteComputerButton);
        panel.add(new JLabel("Black"));
        panel.add(blackHumanButton);
        panel.add(blackComputerButton);

        panel.add(new JLabel("Search"));
        this.searchDepthSpinner = addLabeledSpinner(panel, "Search Depth",
                new SpinnerNumberModel(6, 0, 10, 1));

        final JButton cancelButton = new JButton("Cancel");
        final JButton okButton = new JButton("OK");

        okButton.addActionListener((e) -> {
            whitePlayerType = whiteComputerButton.isSelected()
                    ? PlayerType.COMPUTER
                    : PlayerType.HUMAN;
            blackPlayerType = blackComputerButton.isSelected()
                    ? PlayerType.COMPUTER
                    : PlayerType.HUMAN;
            GameSetup.this.setVisible(false);
        });

        cancelButton.addActionListener((e) -> {
            System.out.println("Cancel");
            GameSetup.this.setVisible(false);
        });

        panel.add(cancelButton);
        panel.add(okButton);

        setLocationRelativeTo(frame);
        pack();
        setVisible(false);
    }

    PlayerType getWhitePlayerType() {
        return this.whitePlayerType;
    }

    PlayerType getBlackPlayerType() {
        return this.blackPlayerType;
    }

    public int getSearchDepthValue() {
        return (int) searchDepthSpinner.getValue();
    }

    void promptUser() {
        setVisible(true);
        repaint();
    }

    boolean isAIPlayer(final Player player) {
        if (player.getAlliance() == Alliance.WHITE) {
            return getWhitePlayerType() == PlayerType.COMPUTER;
        }
        return getBlackPlayerType() == PlayerType.COMPUTER;
    }

    private static JSpinner addLabeledSpinner(final Container container,
                                              final String text,
                                              final SpinnerModel spinnerModel) {
        final JLabel label = new JLabel(text);
        container.add(label);
        final JSpinner spinner = new JSpinner(spinnerModel);
        label.setLabelFor(spinner);
        container.add(spinner);
        return spinner;
    }
}