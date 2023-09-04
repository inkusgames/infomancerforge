package com.inkus.infomancerforge.utils;

import java.io.IOException;
import java.io.OutputStream;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class OutputStreamLimitedDocument extends OutputStream {
	private int maxBufferSize;
	private int maxLineSize;
	
	private byte[] currentLine;
	private int pos=0;
	
	private PlainDocument document;
	
	public OutputStreamLimitedDocument(int maxBufferSize, int maxLineSize){
		this.maxBufferSize=maxBufferSize;
		this.maxLineSize=maxLineSize;
		document=new PlainDocument();
		currentLine=new byte[maxLineSize];
	}

	private void endLine() {
		try {
			synchronized (this) {
				String newLine=new String(currentLine,0,pos)+"\n";
				System.out.println(newLine);
				pos=0;
				document.insertString(document.getLength(),newLine,null);
				
				while (document.getLength()>maxBufferSize) {
					var e=document.getParagraphElement(0);
					document.remove(0, e.getEndOffset());
				}
			}
		} catch (BadLocationException e) {
			// TODO: Add to proper logs
			e.printStackTrace();
		}
	}
	
	@Override
	public void write(int b) throws IOException {
		if (b=='\n' || pos==maxLineSize) {
			endLine();
		}else {
			currentLine[pos++]=(byte)b;
		}
	}

	public Document getDocument() {
		return document;
	}
}