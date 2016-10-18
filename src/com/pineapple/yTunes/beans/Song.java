package com.pineapple.yTunes.beans;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import com.beaglebuddy.id3.enums.PictureType;
import com.beaglebuddy.id3.pojo.AttachedPicture;
import com.beaglebuddy.mp3.MP3;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import javafx.scene.media.Media;

public class Song {
	private StringProperty isPlayingString, songName, time, artist, album, trackNumber, bitrate;
	private int songDuration;
	public Image albumArt;
	private boolean isPlayingNow = false;
	private String lyrics;

	public String getLyrics() {
		return lyrics;
	}

	public void setLyrics(String lyrics) {
		this.lyrics = lyrics;
	}

	private URL location;
	private Media mediaFile;

	public Song(String filePath) {
		try {
			String pathForBeagle = filePath;
//			System.out.println("Before replace:" + pathForBeagle);
			pathForBeagle = pathForBeagle.replace("%20", " ");
//			System.out.println("After replace:" + pathForBeagle);
			
			MP3 mp3 = new MP3(pathForBeagle);
			
			// if there was any invalid information (ie, ID3v2.x frames) in the
			// .mp3 file,
			// then display the errors to the user
			if (mp3.hasErrors()) { // display the errors that were found
//				List<String> errors = mp3.getErrors();
//				for (String error : errors)
//					System.out.println(error);
				mp3.save(); // discard the invalid information (ID3v2.x frames)
				// and
			} // save only the valid frames back to the .mp3 file
			
			if (mp3.getAudioDuration() == 0) // if the length of the song hasn't
				// been specified,
				mp3.setAudioDuration(); // then calculate it from the mpeg audio
			// frames
			
			// see if the .mp3 file has valid MPEG audio frames
//			List<String> errors = mp3.validateMPEGFrames();
			
//			if (errors.size() != 0) {
//				for (String error : errors)
//					System.out.println(error);
//			}
			
			if (mp3.hasID3v1Tag()) // if the mp3 file has an obsolete ID3v1 tag
				// at the end of the .mp3 file
			{ // then remove it
				mp3.removeID3v1Tag();
				mp3.save();
			}
			
			// print out all the internal information about the .mp3 file
//			System.out.println(mp3);
			
			// print out some information about the song
			/*
			System.out.println("codec..............: " + mp3.getCodec()                          + "\n"         +
					"bit rate...........: " + mp3.getBitrate()                        + " kbits/s\n" +
					"bit rate type......: " + mp3.getBitrateType()                    + "\n"         +
					"frequency..........: " + mp3.getFrequency()                      + " hz\n"      +
					"audio duration.....: " + mp3.getAudioDuration()                  + " s\n"       +
					"audio size.........: " + mp3.getAudioSize()                      + " bytes\n"   +
					"album..............: " + mp3.getAlbum()                          + "\n"         +
					"artist.............: " + mp3.getBand()                           + "\n"         +
					"contributing artist: " + mp3.getLeadPerformer()                  + "\n"         +
					"lyrics by..........: " + mp3.getLyricsBy()                       + "\n"         +
					"music by...........: " + mp3.getMusicBy()                        + "\n"         +
					"picture............: " + mp3.getPicture(PictureType.FRONT_COVER) + "\n"         +
					"publisher..........: " + mp3.getPublisher()                      + "\n"         +
					"rating.............: " + mp3.getRating()                         + "\n"         +
					"title..............: " + mp3.getTitle()                          + "\n"         +
					"track #............: " + mp3.getTrack()                          + "\n"         +
					"year recorded......: " + mp3.getYear()                           + "\n"         +
					"lyrics.............: " + mp3.getLyrics()                         + "\n");
			*/
			System.out.println("Song: " + mp3.getTitle());
			
			// save the new information to the .mp3 file
			try{
				AttachedPicture picture = mp3.getPicture(PictureType.FRONT_COVER);
				byte[] byteArrayPic = picture.getImage();
				ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArrayPic);
				this.albumArt = new Image(inputStream);				
			} catch(NullPointerException e) {
				this.albumArt = null;
			}
			
//			Image image = new Image();
			songNameProperty().set(mp3.getTitle());
			setSongSeconds(mp3.getAudioDuration());
			String secondsToSet = "" + mp3.getAudioDuration()%60;
			if(secondsToSet.length() < 2)
				secondsToSet = "0" + secondsToSet;
			timeProperty().set("" + TimeUnit.SECONDS.toMinutes(mp3.getAudioDuration()) + ":" + secondsToSet);
			artistProperty().set(mp3.getBand());
			if(mp3.getBand() == null)
				artistProperty().set(mp3.getLeadPerformer());
			else if(mp3.getLeadPerformer() == null)
				artistProperty().set(mp3.getMusicBy());
			albumProperty().set(mp3.getAlbum());
			trackNumberProperty().set("" + mp3.getTrack());
			bitrateProperty().set(mp3.getBitrate() + "kbit/s");
			lyrics = mp3.getLyrics();
			
			setMediaFile(new Media("file:" + mp3.getPath().replace(" ", "%20")));
		} catch (IOException ex) {
			// an error occurred reading/saving the .mp3 file.
			// you may try to read it again to see if the error still occurs.
			ex.printStackTrace();
		}
	}
	
	public Image getAlbumImage() {
		return this.albumArt;
	}
	//isPlayingString
	public void setIsPlayingString(String value) {
		isPlayingStringProperty().set(value);
	}
	public String getIsPlayingString() {
			return isPlayingStringProperty().get();
	}
	public StringProperty isPlayingStringProperty() {
		if(isPlayingString == null)
			isPlayingString = new SimpleStringProperty(this, "isPlayingString");
		return isPlayingString;
	}
	public void setAsPlaying() {
		isPlayingStringProperty().set("\u25b6");
	}
	public void setAsNotPlaying() {
		isPlayingStringProperty().set(" ");
	}
	
	//songName
	public void setSongName(String value) {
		songNameProperty().set(value);
	}
	public String getSongName() {
		return songNameProperty().get();
	}
	public StringProperty songNameProperty() {
		if(songName == null)
			songName = new SimpleStringProperty(this, "songName");
		return songName;
	}
	
	//time
	public void setTime(String value) {
		timeProperty().set(value);
	}
	public String getTime() {
		return timeProperty().get();
	}
	public StringProperty timeProperty() {
		if(time == null)
			time = new SimpleStringProperty(this, "time");
		return time;
	}
	
	//artist
	public void setArtist(String value) {
		artistProperty().set(value);
	}
	public String getArtist() {
		return artistProperty().get();
	}
	public StringProperty artistProperty() {
		if(artist == null)
			artist = new SimpleStringProperty(this, "artist");
		return artist;
	}
	
	//album
	public void setAlbum(String value) {
		albumProperty().set(value);
	}
	public String getAlbum() {
		return albumProperty().get();
	}
	public StringProperty albumProperty() {
		if(album == null)
			album = new SimpleStringProperty(this, "album");
		return album;
	}
	
	//trackNumber
	public void setTrackNumber(String value) {
		trackNumberProperty().set(value);
	}
	public String getTrackNumber() {
		return trackNumberProperty().get();
	}
	public StringProperty trackNumberProperty() {
		if(trackNumber == null)
			trackNumber = new SimpleStringProperty(this, "trackNumber");
		return trackNumber;
	}
	
	//bitrate
	public void setBitrate(String value) {
		bitrateProperty().set(value);
	}
	public String getBitrate() {
		return bitrateProperty().get();
	}
	public StringProperty bitrateProperty() {
		if(bitrate == null)
			bitrate = new SimpleStringProperty(this, "bitrate");
		return bitrate;
	}
	
	//location
	public URL getLocation() {
		return location;
	}
	public void setLocation(URL location) {
		this.location = location;
	}

	public Media getMediaFile() {
		return mediaFile;
	}

	public void setMediaFile(Media mediaFile) {
		this.mediaFile = mediaFile;
	}
	
	public int getSongSeconds() {
		return songDuration;
	}
	
	public void setSongSeconds(int songSeconds) {
		this.songDuration = songSeconds;
	}

	@Override
	public String toString() {
		return String.format("%s\t-%s", this.getSongName(), this.getArtist());
	}

	public boolean isPlayingNow() {
		return isPlayingNow;
	}

	public void setPlayingNow(boolean isPlayingNow) {
		this.isPlayingNow = isPlayingNow;
	}

}
