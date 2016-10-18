package com.pineapple.yTunes.application;

import com.pineapple.yTunes.beans.Song;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Windows {

	public static void displayLyricWindow(Song currentSong) {
		Stage window = new Stage();
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Lyric");
		Button closeButton = new Button("Done");

		closeButton.setOnAction(e -> {
			window.close();
		});
		
		Label titleLabel = new Label(currentSong.getArtist() + " - " + currentSong.getSongName());
		ImageView imageView = new ImageView();
		if(currentSong.getAlbumImage() != null) {
			imageView.setImage(currentSong.getAlbumImage());
			imageView.maxHeight(20.0);
			imageView.maxWidth(20.0);
		}
		
		
		
		VBox vbox = new VBox(50);
		vbox.setPadding(new Insets(0, 10, 10, 10));
		vbox.setSpacing(10);
		vbox.setAlignment(Pos.CENTER);
		
		Text lyric = new Text(currentSong.getLyrics());
		
		VBox vbox2 = new VBox();
		
		vbox2.getChildren().addAll(imageView, lyric);
		scrollPane.setContent(vbox2);
		vbox.getChildren().addAll(titleLabel, scrollPane, closeButton);
		
		Scene scene = new Scene(vbox);
		window.setScene(scene);
		window.showAndWait();
		
		return;

	}

}
