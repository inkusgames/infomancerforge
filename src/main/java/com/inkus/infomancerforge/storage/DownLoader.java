package com.inkus.infomancerforge.storage;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DownLoader<type> extends Thread {
	public enum DownloadType {
		Text{
			public Object convertType(byte[] bytes) {
				return new String(bytes);
			}
		},
		Binary{
			public Object convertType(byte[] bytes) {
				return bytes;
			}
		};
		
		public abstract Object convertType(byte[] bytes);
	}

	public enum DownloadState {
		Waiting{
			public boolean isCompleted(){
				return false;
			}
		},
		Started{
			public boolean isCompleted(){
				return false;
			}
		},
		Failed{
			public boolean isCompleted(){
				return true;
			}
		},
		Completed{
			public boolean isCompleted(){
				return true;
			}
		};
		
		public abstract boolean isCompleted();
	}
	
	private DownloadType downloadType;
	private DownloadState downloadState=DownloadState.Waiting;
	private String url;
	private type data;
	private DownloadProgressListener<type> listener;
	
	private static List<DownLoader<?>> downloaders=new ArrayList<>();

	private DownLoader(DownloadType downloadType,String url,DownloadProgressListener<type> listener) {
		System.out.println("Downloader created");
		this.downloadType=downloadType;
		this.url=url;
		this.listener=listener;
		synchronized (downloaders) {
			downloaders.add(this);
		}
		start();
	}

	private void setState(DownloadState newState) {
		downloadState=newState;
		listener.downloadStateChanged(this, downloadState);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		try {
			System.out.println("Downloader starting");
			setState(DownloadState.Started);
			URL downloadRul=new URL(url);
			InputStream input=downloadRul.openStream();
	        byte[] bytes = input.readAllBytes();
	        data=(type)downloadType.convertType(bytes);
			input.close();
			setState(DownloadState.Completed);
			listener.downloadDone(this, data);
		} catch (Throwable e) {
			setState(DownloadState.Failed);
			e.printStackTrace();
		} finally {
			synchronized (downloaders) {
				downloaders.remove(this);
			}
		}
	}

	public DownloadState getDownloadState() {
		return downloadState;
	}

	public type getData() {
		return data;
	}

	public String getUrl() {
		return url;
	}

	public interface DownloadProgressListener<type> {
		public void downloadDone(DownLoader<type> downLoader, type data);
		public void downloadStateChanged(DownLoader<type> downLoader,DownloadState downloadState);
	}
	
	public static DownLoader<String> getStringDownloader(String url,DownloadProgressListener<String> listener){
		return new DownLoader<String>(DownloadType.Text,url,listener);
	}
	
	public static DownLoader<byte[]> getBinaryDownloader(String url,DownloadProgressListener<byte[]> listener){
		return new DownLoader<byte[]>(DownloadType.Binary,url,listener);
	}
}
