package com.techess.gui;

import com.techess.engine.Alliance;
import com.techess.engine.pieces.Piece;
import com.techess.engine.pieces.PieceType;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


/**
 * Created by igor on 03.03.18.
 */
public class ResourceManager {
    private static ResourceManager instance = null;
    private static final String DEFAULT_PATH_TO_SPRITE_SET = "resources/img/chessPiecesSpriteSet.png";
    public static final int TILE_SIZE = 64;
    private BufferedImage piecesSpriteSet = null;
    private BufferedImage[][] pieceImages = null;
    private ImageIcon[][] scaledPieceIcons = null;

    private ResourceManager(){
        try {
            this.piecesSpriteSet = ImageIO.read(new File(DEFAULT_PATH_TO_SPRITE_SET));
            this.pieceImages = createPieceImages();
            this.scaledPieceIcons = createScaledPieceIcons();
            System.out.println("The sprite set of chess pieces has been successfully loaded");
        } catch (IOException e) {
            System.out.println("Could not load image");
            //e.printStackTrace();
        }
    }

    public static synchronized ResourceManager getInstance(){
        if(instance == null){
            instance = new ResourceManager();
        }
        return instance;
    }

    public BufferedImage getPieceImage(final Piece piece){
        int x = piece.getPieceType().ordinal();
        int y = piece.getAlliance().ordinal();
        return this.pieceImages[y][x];
    }

    public ImageIcon getScaledPieceIcon(final Piece piece){
        int x = piece.getPieceType().ordinal();
        int y = piece.getAlliance().ordinal();
        return this.scaledPieceIcons[y][x];
    }

    private BufferedImage[][] createPieceImages(){
        final int numOfAlliances = Alliance.values().length;
        final int numOfPieceTypes = PieceType.values().length;
        final BufferedImage[][] pieceImages = new BufferedImage[numOfAlliances][numOfPieceTypes];
        for(int y = 0; y < numOfAlliances; ++y){
            for(int x = 0; x < numOfPieceTypes; ++x){
                pieceImages[y][x] = this.piecesSpriteSet.getSubimage(x * TILE_SIZE, y * TILE_SIZE,
                        TILE_SIZE, TILE_SIZE);
            }
        }
        return pieceImages;
    }

    private ImageIcon[][] createScaledPieceIcons() {
        final int numOfAlliances = Alliance.values().length;
        final int numOfPieceTypes = PieceType.values().length;
        final ImageIcon[][] scaledPieceIcons = new ImageIcon[numOfAlliances][numOfPieceTypes];
        for(int y = 0; y < numOfAlliances; ++y){
            for(int x = 0; x < numOfPieceTypes; ++x){
                Image image = ResourceManager.getScaledImage(this.pieceImages[y][x], TILE_SIZE / 2,
                        TILE_SIZE / 2);
                scaledPieceIcons[y][x] = new ImageIcon(image);
            }
        }
        return scaledPieceIcons;
    }

    private static Image getScaledImage(final Image pieceImage, final int newWidth, final int newHeight){
        return pieceImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
    }
}
