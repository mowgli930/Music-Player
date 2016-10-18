package com.pineapple.yTunes.application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import com.pineapple.yTunes.beans.Song;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Controller implements Initializable {
	public ObservableList<Song> teamMembers = FXCollections.observableArrayList();
	//testing
	
	@FXML
	public Button playpause;
	@FXML
	public Button nextbutton;
	@FXML
	public Button previousbutton;
	@FXML
	public Button repeatButton;
    @FXML
    public Label songNameLabel;
    @FXML
    public Label artistAlbumLabel;
    @FXML
    ImageView coverArtView;
    
    @FXML
    public FlowPane tableContainer;
    @FXML
    public AnchorPane anchorPane;
    
	@FXML
	public TableView<Song> tableview;
	@FXML
	public TableColumn<Song, String> playingSongCol;
	@FXML
	public TableColumn<Song, String> songNameCol;
	@FXML
	public TableColumn<Song, String> timeCol;
	@FXML
	public TableColumn<Song, String> artistCol;
	@FXML
	public TableColumn<Song, String> albumCol;
	@FXML
	public TableColumn<Song, String> bitrateCol;
	@FXML
	public ProgressBar progress;
	
	
	//temporary variables
	boolean isPlaying;
	int indexPlaying = 0;
	Song currentSong;
	public MediaPlayer mediaPlayer;
	public boolean listRepeat = true;

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		nextbutton.setText(">>");
		previousbutton.setText("<<");
		playpause.setText(">");
		
		coverArtView.setOnMouseClicked(event -> {
			System.out.println("oh hi there mark");
			
			Windows.displayLyricWindow(teamMembers.get(indexPlaying));
		});
		//loading songs from directory
		try {
			Files.walk(Paths.get(System.getProperty("user.dir") + "/src/Media/")).forEach(filePath -> {
			    if (Files.isRegularFile(filePath)) {
			    	if(filePath.toString().substring(filePath.toString().length() - 4).equals(".mp3")) {
			    		teamMembers.add(new Song(filePath.toString()));
			    		
			    	}
			    }
			});
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(int i = 0; i < teamMembers.size(); i++)
			System.out.println(teamMembers.get(i).getSongName());
		
		isPlaying = false;
		indexPlaying = 0;
		if(!teamMembers.isEmpty()) {
			mediaPlayer = new MediaPlayer(teamMembers.get(0).getMediaFile());
			currentSong = teamMembers.get(0);
		}
		
		//setting keyboard shortcuts
		anchorPane.setOnKeyPressed(event -> {
			if(event.getCode().toString().equals("K"))
				playPauseButton(new ActionEvent());
			else if(event.getCode().toString().equals("J"))
				nextButton(new ActionEvent());
			else if(event.getCode().toString().equals("L"))
				previousButton(new ActionEvent());
		});
		tableContainer.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                if (db.hasFiles()) {
                    event.acceptTransferModes(TransferMode.COPY);
                } else {
                    event.consume();
                }
            }
        });
        
        // Dropping over surface
        tableContainer.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
                    success = true;
                    String filePath = null;
                    for (File file:db.getFiles()) {
                        filePath = file.getAbsolutePath();
                        
                        addSong(filePath);
                    }
                }
                event.setDropCompleted(success);
                event.consume();
            }
        });
        
        
		makeTable();
		return;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void makeTable() {
		//Setting up TableView
		playingSongCol = new TableColumn<Song, String>("  ");
		playingSongCol.setCellValueFactory(new PropertyValueFactory("isPlayingString"));
		songNameCol = new TableColumn<Song,String>("Song Name");
		songNameCol.setCellValueFactory(new PropertyValueFactory("songName"));
		timeCol = new TableColumn<Song,String>("Time");
		timeCol.setCellValueFactory(new PropertyValueFactory("time"));
		artistCol = new TableColumn<Song,String>("Artist");
		artistCol.setCellValueFactory(new PropertyValueFactory("artist"));
		albumCol = new TableColumn<Song,String>("Album");
		albumCol.setCellValueFactory(new PropertyValueFactory("album"));
		bitrateCol = new TableColumn<Song,String>("Bitrate");
		bitrateCol.setCellValueFactory(new PropertyValueFactory("bitrate"));
		

		teamMembers.addListener(new ListChangeListener<Song>() {

			@Override
			public void onChanged(Change<? extends Song> c) {
				c.next();
				if(c.wasPermutated()) {
					System.out.println("wow, I detected a permutation1313");
					indexPlaying = teamMembers.indexOf(currentSong);
				}
				else {
					System.out.println("wow, I detected a change");					
				}
					
			}
			
		});
		
		tableview.setItems(this.teamMembers);
		tableview.getColumns().setAll(playingSongCol, songNameCol, timeCol, artistCol, albumCol, bitrateCol);

		return;
	}	
		
	public void playPauseButton(ActionEvent event) {
		playpauseSong();
		mediaPlayer.setOnEndOfMedia((new Runnable() {
			@Override
			public void run() {
				nextButton(new ActionEvent());
			}
		}));
		GUIPlayerBox();
		return;
	}
	
	public void nextButton(ActionEvent event) {
		teamMembers.get(indexPlaying).setAsNotPlaying();
		previousSong();
		mediaPlayer.setOnEndOfMedia((new Runnable() {
			@Override
			public void run() {
				nextButton(new ActionEvent());
			}
		}));
		GUIPlayerBox();
		return;
	}
	
	public void previousButton(ActionEvent event) {
		teamMembers.get(indexPlaying).setAsNotPlaying();
//		teamMembers.get(teamMembers.indexOf(currentSong)).setAsNotPlaying();
		nextSong();
		mediaPlayer.setOnEndOfMedia((new Runnable() {
			@Override
			public void run() {
				nextButton(new ActionEvent());
			}
		}));
		GUIPlayerBox();
		return;
	}
	
	public void GUIPlayerBox(){
		teamMembers.get(indexPlaying).setAsPlaying();
		tableview.setItems(this.teamMembers);

		if(getPlayingSong().getAlbumImage() != null)
			coverArtView.setImage(getPlayingSong().getAlbumImage());
//		else
//			coverArtView.setImage(new Image());
//			coverArtView.setImage(new Image("/Users/leo/Documents/workspaces/GraphicalInterfaces and Interaction/yTunes/resources/missingArtwork.jpeg"));
//			coverArtView.setImage(new Image(System.getProperty("user.dir") + "/resources/missingArtwork.png"));
		
		songNameLabel.setText(getPlayingSong().getSongName());
		artistAlbumLabel.setText(String.format("%s - %s", getPlayingSong().getArtist(), getPlayingSong().getAlbum()));
		if(isPlaying)
			playpause.setText("||");
		else if(!isPlaying)
			playpause.setText(">");
		return;
		
	}
	
	@FXML
	public void onDragOverHandle(Event event) {
		System.out.println("onDragOver");
		return;
	}
	
	public void onDragDroppedHandle(Event event) {
		System.out.println("onDragDropped");
		return;
	}
	
	@FXML
	public void repeatButton(ActionEvent event) {
		repeatToggle();
		
		if(listRepeat == true)
			repeatButton.setText("Turn Off Repeat");
		else if(listRepeat == false)
			repeatButton.setText("Turn On Repeat");
	}

	public void addSong(String filePath) {
		File source = new File(filePath);
		File dest = new File(System.getProperty("user.dir") + "/src/Media/" + source.getName());
		try {
			Files.copy(source.toPath(), dest.toPath());
			teamMembers.add(new Song(dest.toPath().toString()));
		} catch (FileAlreadyExistsException e) {
			System.err.println("Song has already been added");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	
	//PlaylistModel methods, should be rewritten
	public Song getPlayingSong() {
		return teamMembers.get(indexPlaying);
	}
	
//	public boolean getListRepeat() {
//		return this.listRepeat;
//	}
	
	public void playpauseSong() {
		if(isPlaying) {
			System.out.println("isPlaying is true");
			mediaPlayer.pause();
			isPlaying = false;
		}
		else if(isPlaying == false) {
			System.out.println("isPlaying is false");
			mediaPlayer.play();
			isPlaying = true;
		}
		
		return;
	}
	
	public void nextSong() {
		indexPlaying++;
		mediaPlayer.stop();
		try{
			currentSong = teamMembers.get(indexPlaying);
			Media hit = teamMembers.get(indexPlaying).getMediaFile();
			mediaPlayer = new MediaPlayer(hit);
			if(isPlaying)
				mediaPlayer.play();
		} catch(IndexOutOfBoundsException e) {
			indexPlaying = 0;
			Media hit = teamMembers.get(indexPlaying).getMediaFile();
			mediaPlayer = new MediaPlayer(hit);
			if(listRepeat && isPlaying)
				mediaPlayer.play();
			else{
				mediaPlayer.stop();
				isPlaying = false;
			}
			
		}
		
		return;
	}
	
	public void previousSong() {
		indexPlaying--;
		mediaPlayer.stop();
		try{
			currentSong = teamMembers.get(indexPlaying);
			Media hit = teamMembers.get(indexPlaying).getMediaFile();			
			mediaPlayer = new MediaPlayer(hit);
			if(isPlaying)
				mediaPlayer.play();
		} catch(IndexOutOfBoundsException e) {
			indexPlaying = teamMembers.size() - 1;
			Media hit = teamMembers.get(indexPlaying).getMediaFile();
			mediaPlayer = new MediaPlayer(hit);
			if(listRepeat && isPlaying)
				mediaPlayer.play();
			else {
				mediaPlayer.stop();
				isPlaying = false;
			}
		}
		
		return;
	}
	
	public void repeatToggle() {
		if(listRepeat == true)
			listRepeat = false;
		else if(listRepeat == false)
			listRepeat = true;
		return;
	}

}
